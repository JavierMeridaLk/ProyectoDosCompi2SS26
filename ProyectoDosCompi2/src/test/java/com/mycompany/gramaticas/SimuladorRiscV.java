package com.mycompany.gramaticas;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Ejecuta el ensamblador RISC-V que genera TraductorRiscV para probarlo sin RARS.
// Solo conoce las instrucciones que usa el traductor (cualquier otra es un error) y las llamadas al sistema de
// RARS: 1 imprimir entero, 3 imprimir decimal, 9 sbrk, 10 salir, 11 imprimir caracter, 12 leer caracter.
// La memoria usa las mismas direcciones que RARS: datos en 0x10010000 y sbrk desde 0x10040000.
public class SimuladorRiscV {

    private static final int BASE = 0x10000000;
    private static final int DATOS = 0x10010000;
    private static final int SBRK = 0x10040000;
    private static final int MEMORIA = 0x01000000;     // 16 MB desde BASE
    private static final long PASOS_MAXIMOS = 200_000_000;
    private static final Pattern MEMORIA_OPERANDO = Pattern.compile("(-?\\d+)\\((\\w+)\\)");

    private record Instruccion(int linea, String nombre, String[] operandos, int direccion) {
    }

    private static final java.util.Set<String> CONOCIDAS = java.util.Set.of("li", "la", "mv", "neg", "seqz", "snez",
            "add", "sub", "mul", "div", "rem", "slt", "addi", "xori", "slli", "lw", "sw", "fld", "fsd", "fadd.d",
            "fsub.d", "fmul.d", "fdiv.d", "fneg.d", "fcvt.d.w", "fcvt.w.d", "feq.d", "flt.d", "fle.d", "beq", "bne",
            "blt", "bge", "ble", "bgt", "beqz", "j", "jal", "ret", "ecall");
    private static final int ALCANCE_BRANCH = 4096;        // +-4 KB
    private static final int ALCANCE_JAL = 1 << 20;        // +-1 MB
    private int saltoMasLargo;

    private final List<Instruccion> programa = new ArrayList<>();
    private final Map<String, Integer> etiquetas = new HashMap<>();   // etiqueta de .text -> instruccion
    private final Map<String, Integer> datos = new HashMap<>();       // etiqueta de .data -> direccion
    private final ByteBuffer memoria = ByteBuffer.allocate(MEMORIA).order(ByteOrder.LITTLE_ENDIAN);
    private final int[] x = new int[32];
    private final double[] f = new double[32];
    private final StringBuilder salida = new StringBuilder();
    private final String entrada;
    private int leidos;
    private int finHeap = SBRK;

    public SimuladorRiscV(String ensamblador, String entrada) {
        this.entrada = entrada;
        ensamblar(ensamblador);
    }

    // ==================================================================
    //                          Ensamblado
    // ==================================================================

    private void ensamblar(String ensamblador) {
        boolean enTexto = false;
        int siguienteDato = DATOS;
        String[] lineas = ensamblador.split("\n");
        for (int i = 0; i < lineas.length; i++) {
            String linea = lineas[i];
            int comentario = linea.indexOf('#');
            if (comentario >= 0) {
                linea = linea.substring(0, comentario);
            }
            linea = linea.strip();
            if (linea.isEmpty()) {
                continue;
            }
            if (linea.equals(".data") || linea.equals(".text")) {
                enTexto = linea.equals(".text");
                continue;
            }
            int dosPuntos = linea.indexOf(':');
            if (dosPuntos > 0 && linea.substring(0, dosPuntos).matches("[A-Za-z_][\\w]*")) {
                String etiqueta = linea.substring(0, dosPuntos);
                if (etiquetas.containsKey(etiqueta) || datos.containsKey(etiqueta)) {
                    throw error(i, "etiqueta repetida " + etiqueta);
                }
                if (enTexto) {
                    etiquetas.put(etiqueta, programa.size());
                } else {
                    datos.put(etiqueta, siguienteDato);
                }
                linea = linea.substring(dosPuntos + 1).strip();
                if (linea.isEmpty()) {
                    continue;
                }
            }
            if (!enTexto) {
                if (!linea.startsWith(".double ")) {
                    throw error(i, "directiva desconocida: " + linea);
                }
                memoria.putDouble(siguienteDato - BASE, Double.parseDouble(linea.substring(8).strip()));
                siguienteDato += 8;
                continue;
            }
            String[] partes = linea.split("\\s+", 2);
            String[] operandos = partes.length == 1 ? new String[0] : partes[1].split("\\s*,\\s*");
            if (!CONOCIDAS.contains(partes[0])) {
                throw error(i, "instrucción desconocida: " + partes[0]);
            }
            programa.add(new Instruccion(i + 1, partes[0], operandos, direccionTexto));
            direccionTexto += tamano(partes[0], operandos);
        }
        revisarAlcances();
    }

    private int direccionTexto;

    // Bytes que ocupa en RARS: li con un valor de mas de 12 bits y la son dos instrucciones
    private static int tamano(String nombre, String[] operandos) {
        if (nombre.equals("la")) {
            return 8;
        }
        if (nombre.equals("li")) {
            long valor = Long.parseLong(operandos[1]);
            return valor >= -2048 && valor <= 2047 ? 4 : 8;
        }
        return 4;
    }

    // Un branch llega a +-4 KB y un j/jal a +-1 MB: RARS no ensambla un salto mas lejano
    private void revisarAlcances() {
        for (Instruccion i : programa) {
            String[] o = i.operandos();
            String destino = switch (i.nombre()) {
                case "beq", "bne", "blt", "bge", "ble", "bgt" -> o[2];
                case "beqz", "j" -> o[o.length - 1];
                case "jal" -> o[1];
                default -> null;
            };
            if (destino == null) {
                continue;
            }
            Integer indice = etiquetas.get(destino);
            if (indice == null) {
                throw new IllegalStateException("línea " + i.linea() + ": etiqueta inexistente " + destino);
            }
            int destinoBytes = indice < programa.size() ? programa.get(indice).direccion() : direccionTexto;
            int distancia = Math.abs(destinoBytes - i.direccion());
            boolean branch = i.nombre().startsWith("b");
            if (distancia >= (branch ? ALCANCE_BRANCH : ALCANCE_JAL)) {
                throw new IllegalStateException("línea " + i.linea() + ": " + i.nombre() + " a " + destino
                        + " está a " + distancia + " bytes, fuera de su alcance");
            }
            if (!branch) {
                saltoMasLargo = Math.max(saltoMasLargo, distancia);
            }
        }
    }

    // Distancia en bytes del j/jal mas lejano (para comprobar que una prueba de verdad necesita saltos largos)
    public int saltoMasLargo() {
        return saltoMasLargo;
    }

    private static IllegalStateException error(int linea, String mensaje) {
        return new IllegalStateException("línea " + (linea + 1) + ": " + mensaje);
    }

    // ==================================================================
    //                          Ejecucion
    // ==================================================================

    // Ejecuta desde la primera instruccion de .text y devuelve lo que se imprimio
    public String ejecutar() {
        x[2] = BASE + MEMORIA - 16;    // sp
        int pc = 0;
        long pasos = 0;
        while (true) {
            if (++pasos > PASOS_MAXIMOS) {
                throw new IllegalStateException("Demasiados pasos (¿ciclo infinito?). Salida: " + salida);
            }
            if (pc < 0 || pc >= programa.size()) {
                throw new IllegalStateException("El programa se salió del código (pc = " + pc + ")");
            }
            Instruccion i = programa.get(pc);
            String[] o = i.operandos();
            int siguiente = pc + 1;
            try {
                switch (i.nombre()) {
                    case "li" -> escribir(o[0], (int) Long.parseLong(o[1]));
                    case "la" -> escribir(o[0], dato(o[1]));
                    case "mv" -> escribir(o[0], leer(o[1]));
                    case "neg" -> escribir(o[0], -leer(o[1]));
                    case "seqz" -> escribir(o[0], leer(o[1]) == 0 ? 1 : 0);
                    case "snez" -> escribir(o[0], leer(o[1]) != 0 ? 1 : 0);
                    case "add" -> escribir(o[0], leer(o[1]) + leer(o[2]));
                    case "sub" -> escribir(o[0], leer(o[1]) - leer(o[2]));
                    case "mul" -> escribir(o[0], leer(o[1]) * leer(o[2]));
                    case "div" -> escribir(o[0], dividir(leer(o[1]), leer(o[2]), false));
                    case "rem" -> escribir(o[0], dividir(leer(o[1]), leer(o[2]), true));
                    case "slt" -> escribir(o[0], leer(o[1]) < leer(o[2]) ? 1 : 0);
                    case "addi" -> escribir(o[0], leer(o[1]) + inmediato(o[2]));
                    case "xori" -> escribir(o[0], leer(o[1]) ^ inmediato(o[2]));
                    case "slli" -> escribir(o[0], leer(o[1]) << Integer.parseInt(o[2]));
                    case "lw" -> escribir(o[0], memoria.getInt(direccion(o[1], 4)));
                    case "sw" -> memoria.putInt(direccion(o[1], 4), leer(o[0]));
                    case "fld" -> f[flotante(o[0])] = memoria.getDouble(direccion(o[1], 8));
                    case "fsd" -> memoria.putDouble(direccion(o[1], 8), f[flotante(o[0])]);
                    case "fadd.d" -> f[flotante(o[0])] = f[flotante(o[1])] + f[flotante(o[2])];
                    case "fsub.d" -> f[flotante(o[0])] = f[flotante(o[1])] - f[flotante(o[2])];
                    case "fmul.d" -> f[flotante(o[0])] = f[flotante(o[1])] * f[flotante(o[2])];
                    case "fdiv.d" -> f[flotante(o[0])] = f[flotante(o[1])] / f[flotante(o[2])];
                    case "fneg.d" -> f[flotante(o[0])] = -f[flotante(o[1])];
                    case "fcvt.d.w" -> f[flotante(o[0])] = leer(o[1]);
                    case "fcvt.w.d" -> {
                        if (o.length != 3 || !o[2].equals("rtz")) {
                            throw new IllegalStateException("fcvt.w.d sin rtz");
                        }
                        escribir(o[0], (int) f[flotante(o[1])]);
                    }
                    case "feq.d" -> escribir(o[0], f[flotante(o[1])] == f[flotante(o[2])] ? 1 : 0);
                    case "flt.d" -> escribir(o[0], f[flotante(o[1])] < f[flotante(o[2])] ? 1 : 0);
                    case "fle.d" -> escribir(o[0], f[flotante(o[1])] <= f[flotante(o[2])] ? 1 : 0);
                    case "beq", "bne", "blt", "bge", "ble", "bgt" -> {
                        int a = leer(o[0]);
                        int b = leer(o[1]);
                        boolean salta = switch (i.nombre()) {
                            case "beq" -> a == b;
                            case "bne" -> a != b;
                            case "blt" -> a < b;
                            case "bge" -> a >= b;
                            case "ble" -> a <= b;
                            default -> a > b;
                        };
                        if (salta) {
                            siguiente = destino(o[2]);
                        }
                    }
                    case "beqz" -> {
                        if (leer(o[0]) == 0) {
                            siguiente = destino(o[1]);
                        }
                    }
                    case "j" -> siguiente = destino(o[0]);
                    case "jal" -> {
                        escribir(o[0], pc + 1);
                        siguiente = destino(o[1]);
                    }
                    case "ret" -> siguiente = x[1];
                    case "ecall" -> {
                        if (!llamarSistema()) {
                            return salida.toString();
                        }
                    }
                    default -> throw new IllegalStateException("instrucción desconocida");
                }
            } catch (RuntimeException e) {
                throw new IllegalStateException("línea " + i.linea() + " (" + i.nombre() + " "
                        + String.join(", ", o) + "): " + e.getMessage(), e);
            }
            pc = siguiente;
        }
    }

    // false cuando el programa termina
    private boolean llamarSistema() {
        switch (x[17]) {
            case 1 -> salida.append(x[10]);
            case 3 -> salida.append(f[10]);
            case 9 -> {
                int pedida = x[10];
                x[10] = finHeap;
                finHeap += pedida;
            }
            case 10 -> {
                return false;
            }
            case 11 -> salida.appendCodePoint(x[10]);
            case 12 -> x[10] = leidos < entrada.length() ? entrada.charAt(leidos++) : '\n';
            default -> throw new IllegalStateException("llamada al sistema desconocida: " + x[17]);
        }
        return true;
    }

    private static int dividir(int a, int b, boolean resto) {
        if (b == 0) {
            return resto ? a : -1;    // RISC-V no lanza excepcion al dividir entre cero
        }
        return resto ? a % b : a / b;
    }

    private int destino(String etiqueta) {
        Integer indice = etiquetas.get(etiqueta);
        if (indice == null) {
            throw new IllegalStateException("etiqueta inexistente " + etiqueta);
        }
        return indice;
    }

    private int dato(String etiqueta) {
        Integer direccion = datos.get(etiqueta);
        if (direccion == null) {
            throw new IllegalStateException("dato inexistente " + etiqueta);
        }
        return direccion;
    }

    private static int inmediato(String texto) {
        int valor = Integer.parseInt(texto);
        if (valor < -2048 || valor > 2047) {
            throw new IllegalStateException("inmediato fuera de 12 bits: " + valor);
        }
        return valor;
    }

    // desplazamiento(registro) -> posicion en el arreglo de memoria
    private int direccion(String operando, int alineacion) {
        Matcher m = MEMORIA_OPERANDO.matcher(operando);
        if (!m.matches()) {
            throw new IllegalStateException("operando de memoria inválido: " + operando);
        }
        long direccion = (long) leer(m.group(2)) + inmediato(m.group(1));
        if (direccion % alineacion != 0) {
            throw new IllegalStateException("dirección no alineada: " + direccion);
        }
        if (direccion < BASE || direccion + alineacion > BASE + MEMORIA) {
            throw new IllegalStateException("dirección fuera de la memoria: 0x" + Long.toHexString(direccion));
        }
        return (int) (direccion - BASE);
    }

    private int leer(String registro) {
        return x[entero(registro)];
    }

    private void escribir(String registro, int valor) {
        int indice = entero(registro);
        if (indice != 0) {
            x[indice] = valor;
        }
    }

    private static final List<String> NOMBRES = List.of("zero", "ra", "sp", "gp", "tp", "t0", "t1", "t2", "s0",
            "s1", "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7", "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9",
            "s10", "s11", "t3", "t4", "t5", "t6");
    private static final List<String> FLOTANTES = List.of("ft0", "ft1", "ft2", "ft3", "ft4", "ft5", "ft6", "ft7",
            "fs0", "fs1", "fa0", "fa1", "fa2", "fa3", "fa4", "fa5", "fa6", "fa7", "fs2", "fs3", "fs4", "fs5", "fs6",
            "fs7", "fs8", "fs9", "fs10", "fs11", "ft8", "ft9", "ft10", "ft11");

    private static int entero(String registro) {
        int indice = NOMBRES.indexOf(registro);
        if (indice < 0) {
            throw new IllegalStateException("registro entero inválido: " + registro);
        }
        return indice;
    }

    private static int flotante(String registro) {
        int indice = FLOTANTES.indexOf(registro);
        if (indice < 0) {
            throw new IllegalStateException("registro flotante inválido: " + registro);
        }
        return indice;
    }
}
