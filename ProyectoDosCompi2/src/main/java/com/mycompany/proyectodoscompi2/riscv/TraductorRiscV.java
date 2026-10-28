package com.mycompany.proyectodoscompi2.riscv;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mycompany.proyectodoscompi2.cuartetas.Cuarteta;
import com.mycompany.proyectodoscompi2.cuartetas.TraductorC3D;

// Traduce las cuartetas a ensamblador RISC-V (RV32IMFD) para el simulador RARS.
// Registros fijos:
//   s0  direccion del stack          s1  direccion del heap
//   s2  P (numero de celda)          s3  H (numero de celda)
//   s4  direccion de stack[P]: el frame pointer en bytes, se recalcula cada vez que cambia P
// Cada temporal vive en una celda del marco de su funcion, despues de las variables (el tamano del marco ya los
// cuenta), asi la recursion no los pisa. t0-t2 / ft0-ft2 se usan para operar, t5 y t6 para direcciones.
public final class TraductorRiscV {

    public static final int CELDAS_STACK = 393_216;          // 3 MB
    public static final int CELDAS_HEAP = 393_216;           // 3 MB
    private static final int INICIO_STACK = 0x7fc00000;     // inicio de la zona de la pila en RARS
    private static final int CELDA = 8;
    private static final int LARGO_LINEA = 1024;            // buffer de la linea que se lee del teclado

    private final StringBuilder texto = new StringBuilder();
    private final Map<String, String> constantes = new LinkedHashMap<>();   // literal decimal -> etiqueta
    private final Map<String, Integer> celdas = new LinkedHashMap<>();     // temporal -> celda del marco
    private final Set<String> decimales = new HashSet<>();                 // temporales que guardan un decimal
    private int saltos;
    private boolean usaLectura;

    private TraductorRiscV() {
    }

    public static String traducir(List<Cuarteta> cuartetas) {
        return new TraductorRiscV().generar(cuartetas);
    }

    private String generar(List<Cuarteta> cuartetas) {
        inicio(cuartetas.stream().anyMatch(c -> c.operador().equals("func") && c.resultado().equals("main")));
        int i = 0;
        while (i < cuartetas.size()) {
            if (!cuartetas.get(i).operador().equals("func")) {
                throw new IllegalArgumentException("Cuarteta fuera de una función: " + cuartetas.get(i));
            }
            int fin = i;
            while (fin < cuartetas.size() && !cuartetas.get(fin).operador().equals("end")) {
                fin++;
            }
            if (fin == cuartetas.size()) {
                throw new IllegalArgumentException("La función " + cuartetas.get(i).resultado() + " no termina");
            }
            funcion(cuartetas.subList(i, fin + 1));
            i = fin + 1;
        }

        StringBuilder programa = new StringBuilder();
        programa.append("# Generado por IDE XS2 - RISC-V (RV32IMFD) para RARS\n");
        programa.append("# s0 = stack   s1 = heap   s2 = P   s3 = H   s4 = &stack[P] (frame pointer)   s5 = linea leida\n");
        programa.append("# cada celda del stack y del heap ocupa ").append(CELDA).append(" bytes\n\n");
        programa.append(".data\n");
        constantes.forEach((literal, etiqueta) -> programa.append(etiqueta).append(":\t.double ")
                .append(new BigDecimal(literal).toPlainString()).append('\n'));
        if (usaLectura) {
            programa.append("_linea:\t.space ").append(LARGO_LINEA).append('\n');
        }
        programa.append("\n.text\n").append(texto);
        return programa.toString();
    }


    private void inicio(boolean hayMain) {
        etiqueta("inicio");
        comentario("stack y heap del codigo de tres direcciones");
        instruccion("li", "s0, " + INICIO_STACK);
        instruccion("li", "a0, " + CELDAS_HEAP * CELDA);
        instruccion("li", "a7, 9");
        instruccion("ecall", "");
        instruccion("mv", "s1, a0");
        instruccion("li", "s2, 0");
        instruccion("li", "s3, 0");
        instruccion("mv", "s4, s0");
        instruccion("li", "s5, 0");
        if (hayMain) {
            instruccion("jal", "ra, main");
        } else {
            comentario("sin programa principal (.pig): no hay main que ejecutar");
        }
        instruccion("li", "a7, 10");
        instruccion("ecall", "");
    }

    //Funciones

    private void funcion(List<Cuarteta> cuerpo) {
        int marco = Integer.parseInt(cuerpo.get(0).argumento1());
        celdas.clear();
        for (Cuarteta c : cuerpo) {
            for (String operando : new String[] {c.argumento1(), c.argumento2(), c.resultado()}) {
                if (esTemporal(operando)) {
                    celdas.putIfAbsent(operando, 0);
                }
            }
        }
        int celda = marco - celdas.size();
        for (String temporal : celdas.keySet()) {
            celdas.put(temporal, celda++);
        }
        inferirDecimales(cuerpo);
        texto.append('\n');
        for (Cuarteta c : cuerpo) {
            traducir(c);
        }
    }

    // Un temporal es decimal si alguna de sus asignaciones produce un decimal 
    private void inferirDecimales(List<Cuarteta> cuerpo) {
        decimales.clear();
        boolean cambio = true;
        while (cambio) {
            cambio = false;
            for (Cuarteta c : cuerpo) {
                if (!esTemporal(c.resultado())) {
                    continue;
                }
                boolean decimal = switch (c.operador()) {
                    case "=", "neg" -> esDecimal(c.argumento1());
                    case "+", "-", "*", "/", "%" -> esDecimal(c.argumento1()) || esDecimal(c.argumento2());
                    case "(double)" -> true;
                    case "leer_stack", "leer_heap" -> c.decimal();
                    default -> false;
                };
                if (decimal && decimales.add(c.resultado())) {
                    cambio = true;
                }
            }
        }
    }

    //Cuartetas

    private void traducir(Cuarteta c) {
        String op = c.operador();
        String a = c.argumento1();
        String b = c.argumento2();
        String r = c.resultado();
        switch (op) {
            case "func" -> {
                etiqueta(nombre(r));
                comentario("marco: " + a + " celdas");
                instruccion("addi", "sp, sp, -4");
                instruccion("sw", "ra, 0(sp)");
                return;
            }
            case "label" -> {
                etiqueta(nombre(r));
                return;
            }
            default -> comentario(TraductorC3D.instruccion(c));
        }
        switch (op) {
            case "=" -> {
                if (esDecimal(a) || decimales.contains(r)) {
                    cargarDecimal(a, "ft0");
                    guardarDecimal(r, "ft0");
                } else {
                    cargarEntero(a, "t0");
                    guardarEntero(r, "t0");
                }
            }
            case "+", "-", "*", "/", "%" -> aritmetica(op, a, b, r);
            case "<", ">", "<=", ">=", "==", "!=" -> {
                comparar(op, a, b);
                guardarEntero(r, "t2");
            }
            case "!" -> {
                cargarEntero(a, "t0");
                instruccion("seqz", "t2, t0");
                guardarEntero(r, "t2");
            }
            case "neg" -> {
                if (esDecimal(a)) {
                    cargarDecimal(a, "ft0");
                    instruccion("fneg.d", "ft2, ft0");
                    guardarDecimal(r, "ft2");
                } else {
                    cargarEntero(a, "t0");
                    instruccion("neg", "t2, t0");
                    guardarEntero(r, "t2");
                }
            }
            case "(int)" -> {
                if (esDecimal(a)) {
                    cargarDecimal(a, "ft0");
                    instruccion("fcvt.w.d", "t2, ft0, rtz");
                } else {
                    cargarEntero(a, "t2");
                }
                guardarEntero(r, "t2");
            }
            case "(double)" -> {
                cargarDecimal(a, "ft0");
                guardarDecimal(r, "ft0");
            }
            case "leer_stack", "leer_heap" -> {
                String celda = celda(op.substring(5), a, b);
                if (c.decimal()) {
                    instruccion("fld", "ft0, " + celda);
                    guardarDecimal(r, "ft0");
                } else {
                    instruccion("lw", "t2, " + celda);
                    guardarEntero(r, "t2");
                }
            }
            case "escribir_stack", "escribir_heap" -> {
                String celda = celda(op.substring(9), r, b);
                if (esDecimal(a)) {
                    cargarDecimal(a, "ft0");
                    instruccion("fsd", "ft0, " + celda);
                } else {
                    cargarEntero(a, "t2");
                    instruccion("sw", "t2, " + celda);
                }
            }
            case "goto" -> instruccion("j", nombre(r));
            case "if ==", "if !=", "if <", "if >", "if <=", "if >=" -> saltarSi(op.substring(3), a, b, nombre(r));
            case "call" -> instruccion("jal", "ra, " + nombre(r));
            case "end" -> {
                instruccion("lw", "ra, 0(sp)");
                instruccion("addi", "sp, sp, 4");
                instruccion("ret", "");
            }
            case "halt" -> {
                instruccion("li", "a7, 10");
                instruccion("ecall", "");
            }
            case "print" -> imprimir(a, b);
            case "leer_caracter" -> leerCaracter(r);
            default -> throw new IllegalArgumentException("Cuarteta desconocida: " + c);
        }
    }

    private void aritmetica(String op, String a, String b, String r) {
        if (esDecimal(a) || esDecimal(b)) {
            cargarDecimal(a, "ft0");
            cargarDecimal(b, "ft1");
            switch (op) {
                case "+" -> instruccion("fadd.d", "ft2, ft0, ft1");
                case "-" -> instruccion("fsub.d", "ft2, ft0, ft1");
                case "*" -> instruccion("fmul.d", "ft2, ft0, ft1");
                case "/" -> instruccion("fdiv.d", "ft2, ft0, ft1");
                default -> {
                    // a - truncar(a / b) * b
                    instruccion("fdiv.d", "ft2, ft0, ft1");
                    instruccion("fcvt.w.d", "t2, ft2, rtz");
                    instruccion("fcvt.d.w", "ft2, t2");
                    instruccion("fmul.d", "ft2, ft2, ft1");
                    instruccion("fsub.d", "ft2, ft0, ft2");
                }
            }
            guardarDecimal(r, "ft2");
            return;
        }
        cargarEntero(a, "t0");
        cargarEntero(b, "t1");
        String instruccion = switch (op) {
            case "+" -> "add";
            case "-" -> "sub";
            case "*" -> "mul";
            case "/" -> "div";
            default -> "rem";
        };
        instruccion(instruccion, "t2, t0, t1");
        guardarEntero(r, "t2");
    }

    // Deja 1 o 0 en t2
    private void comparar(String op, String a, String b) {
        if (esDecimal(a) || esDecimal(b)) {
            cargarDecimal(a, "ft0");
            cargarDecimal(b, "ft1");
            switch (op) {
                case "<" -> instruccion("flt.d", "t2, ft0, ft1");
                case ">" -> instruccion("flt.d", "t2, ft1, ft0");
                case "<=" -> instruccion("fle.d", "t2, ft0, ft1");
                case ">=" -> instruccion("fle.d", "t2, ft1, ft0");
                case "==" -> instruccion("feq.d", "t2, ft0, ft1");
                default -> {
                    instruccion("feq.d", "t2, ft0, ft1");
                    instruccion("xori", "t2, t2, 1");
                }
            }
            return;
        }
        cargarEntero(a, "t0");
        cargarEntero(b, "t1");
        switch (op) {
            case "<" -> instruccion("slt", "t2, t0, t1");
            case ">" -> instruccion("slt", "t2, t1, t0");
            case "<=" -> {
                instruccion("slt", "t2, t1, t0");
                instruccion("xori", "t2, t2, 1");
            }
            case ">=" -> {
                instruccion("slt", "t2, t0, t1");
                instruccion("xori", "t2, t2, 1");
            }
            case "==" -> {
                instruccion("sub", "t2, t0, t1");
                instruccion("seqz", "t2, t2");
            }
            default -> {
                instruccion("sub", "t2, t0, t1");
                instruccion("snez", "t2, t2");
            }
        }
    }

    private void saltarSi(String op, String a, String b, String destino) {
        String siguiente = "_sigue" + (++saltos);
        if (esDecimal(a) || esDecimal(b)) {
            comparar(op, a, b);
            instruccion("beqz", "t2, " + siguiente);
        } else {
            cargarEntero(a, "t0");
            cargarEntero(b, "t1");
            String contrario = switch (op) {
                case "==" -> "bne";
                case "!=" -> "beq";
                case "<" -> "bge";
                case ">" -> "ble";
                case "<=" -> "bgt";
                default -> "blt";
            };
            instruccion(contrario, "t0, t1, " + siguiente);
        }
        instruccion("j", destino);
        etiqueta(siguiente);
    }

    // En la consola RARS lee una linea completa por llamada
    private void leerCaracter(String destino) {
        usaLectura = true;
        int n = ++saltos;
        String hayLinea = "_linea" + n;
        String finDeLinea = "_finlinea" + n;
        String listo = "_leido" + n;
        instruccion("bnez", "s5, " + hayLinea);
        instruccion("la", "s5, _linea");
        instruccion("mv", "a0, s5");
        instruccion("li", "a1, " + LARGO_LINEA);
        instruccion("li", "a7, 8");
        instruccion("ecall", "");
        etiqueta(hayLinea);
        instruccion("lbu", "a0, 0(s5)");
        instruccion("addi", "s5, s5, 1");
        instruccion("beqz", "a0, " + finDeLinea);
        instruccion("li", "t0, 10");
        instruccion("bne", "a0, t0, " + listo);
        etiqueta(finDeLinea);
        instruccion("li", "s5, 0");
        instruccion("li", "a0, 10");
        etiqueta(listo);
        guardarEntero(destino, "a0");
    }

    // print entero (1), decimal (3) o caracter (11)
    private void imprimir(String valor, String como) {
        int llamada;
        switch (como) {
            case "decimal" -> {
                cargarDecimal(valor, "fa0");
                llamada = 3;
            }
            case "caracter" -> {
                cargarEntero(valor, "a0");
                llamada = 11;
            }
            default -> {
                cargarEntero(valor, "a0");
                llamada = 1;
            }
        }
        instruccion("li", "a7, " + llamada);
        instruccion("ecall", "");
    }

    //Operandos

    private void cargarEntero(String operando, String registro) {
        if (esEntero(operando)) {
            instruccion("li", registro + ", " + operando);
        } else if (esLiteralDecimal(operando)) {
            instruccion("li", registro + ", " + (int) Double.parseDouble(operando));
        } else if (operando.equals("P")) {
            instruccion("mv", registro + ", s2");
        } else if (operando.equals("H")) {
            instruccion("mv", registro + ", s3");
        } else if (decimales.contains(operando)) {
            enMarco("fld", "ft3", celda(operando));
            instruccion("fcvt.w.d", registro + ", ft3, rtz");
        } else {
            enMarco("lw", registro, celda(operando));
        }
    }

    private void cargarDecimal(String operando, String registro) {
        if (esLiteralDecimal(operando)) {
            instruccion("la", "t6, " + constante(operando));
            instruccion("fld", registro + ", 0(t6)");
        } else if (decimales.contains(operando)) {
            enMarco("fld", registro, celda(operando));
        } else {
            cargarEntero(operando, "t6");
            instruccion("fcvt.d.w", registro + ", t6");
        }
    }

    private void guardarEntero(String destino, String registro) {
        if (destino.equals("P")) {
            instruccion("mv", "s2, " + registro);
            instruccion("slli", "s4, s2, 3");
            instruccion("add", "s4, s4, s0");
        } else if (destino.equals("H")) {
            instruccion("mv", "s3, " + registro);
        } else if (decimales.contains(destino)) {
            instruccion("fcvt.d.w", "ft3, " + registro);
            enMarco("fsd", "ft3", celda(destino));
        } else {
            enMarco("sw", registro, celda(destino));
        }
    }

    private void guardarDecimal(String destino, String registro) {
        if (decimales.contains(destino)) {
            enMarco("fsd", registro, celda(destino));
        } else {
            instruccion("fcvt.w.d", "t6, " + registro + ", rtz");
            guardarEntero(destino, "t6");
        }
    }

    // Celda de un temporal en el marco actual
    private void enMarco(String instruccion, String registro, int celda) {
        int bytes = celda * CELDA;
        if (bytes < 2048) {
            instruccion(instruccion, registro + ", " + bytes + "(s4)");
        } else {
            instruccion("li", "t5, " + bytes);
            instruccion("add", "t5, t5, s4");
            instruccion(instruccion, registro + ", 0(t5)");
        }
    }

    // stack[base + desplazamiento] o heap[base + desplazamiento] como operando de lw/sw/fld/fsd.
    private String celda(String memoria, String base, String desplazamiento) {
        boolean stack = memoria.equals("stack");
        long bytes = esEntero(desplazamiento) ? Long.parseLong(desplazamiento) * CELDA : Long.MAX_VALUE;
        boolean corto = bytes >= -2048 && bytes < 2048;
        if (stack && base.equals("P") && corto) {
            return bytes + "(s4)";
        }
        cargarEntero(base, "t0");
        if (!corto) {
            cargarEntero(desplazamiento, "t1");
            instruccion("add", "t0, t0, t1");
        }
        instruccion("slli", "t0, t0, 3");
        instruccion("add", "t0, t0, " + (stack ? "s0" : "s1"));
        return (corto ? bytes : 0) + "(t0)";
    }

    private int celda(String temporal) {
        Integer celda = celdas.get(temporal);
        if (celda == null) {
            throw new IllegalArgumentException("Operando desconocido: " + temporal);
        }
        return celda;
    }

    private String constante(String literal) {
        return constantes.computeIfAbsent(literal, l -> "_decimal" + constantes.size());
    }

    private boolean esDecimal(String operando) {
        return esLiteralDecimal(operando) || decimales.contains(operando);
    }

    private static boolean esTemporal(String operando) {
        return operando.matches("t\\d+");
    }

    private static boolean esEntero(String operando) {
        return operando.matches("-?\\d+");
    }

    private static boolean esLiteralDecimal(String operando) {
        return operando.matches("-?\\d+\\.\\d+([eE]-?\\d+)?");
    }

    // Las etiquetas de RISC-V solo llevan letras, digitos y _
    private static String nombre(String etiqueta) {
        StringBuilder nombre = new StringBuilder();
        etiqueta.codePoints().forEach(c -> {
            if (c < 128 && (Character.isLetterOrDigit(c) || c == '_')) {
                nombre.appendCodePoint(c);
            } else {
                nombre.append('_').append(Integer.toHexString(c));
            }
        });
        return nombre.toString();
    }

    //Texto

    private void etiqueta(String nombre) {
        texto.append(nombre).append(":\n");
    }

    private void comentario(String comentario) {
        texto.append("    # ").append(comentario).append('\n');
    }

    private void instruccion(String instruccion, String operandos) {
        texto.append("    ").append(instruccion);
        if (!operandos.isEmpty()) {
            texto.append(" ".repeat(Math.max(1, 10 - instruccion.length()))).append(operandos);
        }
        texto.append('\n');
    }
}
