package com.mycompany.gramaticas;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mycompany.proyectodoscompi2.cuartetas.Cuarteta;

// Ejecuta las cuartetas para probar que el codigo de tres direcciones hace lo que debe.
// Solo existen el stack, el heap, P, H y los temporales; la entrada es un texto que se lee caracter por caracter.
// Los enteros son Long y los decimales Double (como los registros enteros y de punto flotante de RISC-V).
// Los temporales pertenecen al marco de la funcion (como en RISC-V, donde se guardan en el marco): al
// llamar se guardan y al volver se recuperan, asi la recursion no los pisa.
public class InterpreteC3D {

    private static final int CELDAS = 1_000_000;
    private static final long PASOS_MAXIMOS = 20_000_000;

    private final List<Cuarteta> cuartetas;
    private final Map<String, Integer> etiquetas = new HashMap<>();
    private final Object[] stack = new Object[CELDAS];
    private final Object[] heap = new Object[CELDAS];
    private final Map<String, Object> registros = new HashMap<>();
    private final Deque<Integer> retornos = new ArrayDeque<>();
    private final Deque<Map<String, Object>> temporalesGuardados = new ArrayDeque<>();
    private final StringBuilder salida = new StringBuilder();
    private final String entrada;
    private int leidos;

    public InterpreteC3D(List<Cuarteta> cuartetas, String entrada) {
        this.cuartetas = cuartetas;
        this.entrada = entrada;
        for (int i = 0; i < cuartetas.size(); i++) {
            Cuarteta c = cuartetas.get(i);
            if (c.operador().equals("label") || c.operador().equals("func")) {
                etiquetas.put(c.resultado(), i);
            }
        }
    }

    // Ejecuta desde func main y devuelve lo que se imprimio
    public String ejecutar() {
        Integer inicio = etiquetas.get("main");
        if (inicio == null) {
            throw new IllegalStateException("No hay func main");
        }
        int pc = inicio + 1;
        long pasos = 0;
        while (true) {
            if (++pasos > PASOS_MAXIMOS) {
                throw new IllegalStateException("Demasiados pasos (¿ciclo infinito?). Salida: " + salida);
            }
            Cuarteta c = cuartetas.get(pc);
            String op = c.operador();
            String a = c.argumento1();
            String b = c.argumento2();
            String r = c.resultado();
            switch (op) {
                case "=" -> asignar(r, valor(a));
                case "+", "-", "*", "/", "%" -> asignar(r, aritmetica(op, valor(a), valor(b)));
                case "<", ">", "<=", ">=", "==", "!=" -> asignar(r, comparar(op, valor(a), valor(b)) ? 1L : 0L);
                case "!" -> asignar(r, entero(valor(a)) == 0 ? 1L : 0L);
                case "neg" -> asignar(r, valor(a) instanceof Double d ? (Object) (-d) : (Object) (-entero(valor(a))));
                case "(int)" -> asignar(r, (long) decimal(valor(a)));
                case "(double)" -> asignar(r, decimal(valor(a)));
                case "leer_stack" -> asignar(r, leer(stack, celda(a, b), c.decimal()));
                case "leer_heap" -> asignar(r, leer(heap, celda(a, b), c.decimal()));
                case "escribir_stack" -> stack[celda(r, b)] = valor(a);
                case "escribir_heap" -> heap[celda(r, b)] = valor(a);
                case "label" -> {
                }
                case "goto" -> {
                    pc = destino(r);
                    continue;
                }
                case "if ==", "if !=", "if <", "if >", "if <=", "if >=" -> {
                    if (comparar(op.substring(3), valor(a), valor(b))) {
                        pc = destino(r);
                        continue;
                    }
                }
                case "call" -> {
                    temporalesGuardados.push(new HashMap<>(registros));
                    retornos.push(pc + 1);
                    pc = destino(r) + 1;
                    continue;
                }
                case "end" -> {
                    if (retornos.isEmpty()) {
                        return salida.toString();
                    }
                    pc = retornos.pop();
                    Map<String, Object> guardados = temporalesGuardados.pop();
                    guardados.put("P", registros.get("P"));
                    guardados.put("H", registros.get("H"));
                    registros.clear();
                    registros.putAll(guardados);
                    continue;
                }
                case "halt" -> {
                    return salida.toString();
                }
                case "print" -> imprimir(valor(a), b);
                case "leer_caracter" -> asignar(r, (long) (leidos < entrada.length() ? entrada.charAt(leidos++) : '\n'));
                case "func" -> throw new IllegalStateException("Se entró a una función sin call: " + r);
                default -> throw new IllegalStateException("Cuarteta desconocida: " + c);
            }
            pc++;
        }
    }

    private int destino(String etiqueta) {
        Integer indice = etiquetas.get(etiqueta);
        if (indice == null) {
            throw new IllegalStateException("Etiqueta inexistente: " + etiqueta);
        }
        return indice;
    }

    private void asignar(String registro, Object valor) {
        registros.put(registro, valor);
    }

    private Object valor(String operando) {
        if (operando.matches("-?\\d+")) {
            return Long.parseLong(operando);
        }
        if (operando.matches("-?\\d+\\.\\d+([eE]-?\\d+)?")) {
            return Double.parseDouble(operando);
        }
        if (operando.equals("P") || operando.equals("H")) {
            return registros.getOrDefault(operando, 0L);
        }
        Object valor = registros.get(operando);
        if (valor == null) {
            throw new IllegalStateException("Temporal sin valor: " + operando);
        }
        return valor;
    }

    private int celda(String base, String desplazamiento) {
        long posicion = entero(valor(base)) + entero(valor(desplazamiento));
        if (posicion < 0 || posicion >= CELDAS) {
            throw new IllegalStateException("Acceso fuera de la memoria: " + posicion);
        }
        return (int) posicion;
    }

    // La cuarteta dice si la celda tiene un decimal (en RISC-V se lee con fld o con lw): debe coincidir
    private static Object leer(Object[] memoria, int celda, boolean decimal) {
        Object valor = memoria[celda];
        if (valor == null) {
            return decimal ? (Object) 0.0 : (Object) 0L;
        }
        if (decimal != valor instanceof Double) {
            throw new IllegalStateException("La celda " + celda + " tiene " + valor + " y se leyó como "
                    + (decimal ? "decimal" : "entero"));
        }
        return valor;
    }

    private static long entero(Object valor) {
        if (valor instanceof Long l) {
            return l;
        }
        throw new IllegalStateException("Se esperaba un entero y llegó " + valor);
    }

    private static double decimal(Object valor) {
        return valor instanceof Double d ? d : (double) entero(valor);
    }

    private static Object aritmetica(String op, Object a, Object b) {
        if (a instanceof Double || b instanceof Double) {
            double x = decimal(a);
            double y = decimal(b);
            return switch (op) {
                case "+" -> x + y;
                case "-" -> x - y;
                case "*" -> x * y;
                case "/" -> x / y;
                default -> x % y;
            };
        }
        long x = entero(a);
        long y = entero(b);
        return switch (op) {
            case "+" -> x + y;
            case "-" -> x - y;
            case "*" -> x * y;
            case "/" -> x / y;
            default -> x % y;
        };
    }

    private static boolean comparar(String op, Object a, Object b) {
        int comparacion = a instanceof Double || b instanceof Double ? Double.compare(decimal(a), decimal(b))
                : Long.compare(entero(a), entero(b));
        return switch (op) {
            case "==" -> comparacion == 0;
            case "!=" -> comparacion != 0;
            case "<" -> comparacion < 0;
            case ">" -> comparacion > 0;
            case "<=" -> comparacion <= 0;
            default -> comparacion >= 0;
        };
    }

    private void imprimir(Object valor, String como) {
        switch (como) {
            case "caracter" -> salida.appendCodePoint((int) entero(valor));
            case "decimal" -> salida.append(decimal(valor));
            default -> salida.append(entero(valor));
        }
    }
}
