package com.mycompany.proyectodoscompi2.cuartetas;

import java.util.ArrayList;
import java.util.List;

// Convierte las cuartetas en codigo de tres direcciones en text, P es el frame pointer y H la siguiente celda libre del heap
public final class TraductorC3D {

    private TraductorC3D() {
    }

    public static List<String> traducir(List<Cuarteta> cuartetas) {
        List<String> lineas = new ArrayList<>();
        for (Cuarteta cuarteta : cuartetas) {
            String instruccion = instruccion(cuarteta);
            boolean sinSangria = switch (cuarteta.operador()) {
                case "label", "func", "end" -> true;
                default -> false;
            };
            lineas.add(sinSangria ? instruccion : "    " + instruccion);
        }
        return lineas;
    }

    private static String instruccion(Cuarteta cuarteta) {
        String a = cuarteta.argumento1();
        String b = cuarteta.argumento2();
        String r = cuarteta.resultado();
        return switch (cuarteta.operador()) {
            case "=" -> r + " = " + a;
            case "+", "-", "*", "/", "%", "<", ">", "<=", ">=", "==", "!=" ->
                r + " = " + a + " " + cuarteta.operador() + " " + b;
            case "!" -> r + " = !" + a;
            case "neg" -> r + " = -" + a;
            case "(int)" -> r + " = (int) " + a;
            case "(double)" -> r + " = (double) " + a;
            case "leer_stack", "leer_heap" -> r + " = " + cuarteta.operador().substring(5) + celda(a, b);
            case "escribir_stack", "escribir_heap" -> cuarteta.operador().substring(9) + celda(r, b) + " = " + a;
            case "label" -> r + ":";
            case "goto" -> "goto " + r;
            case "if ==", "if !=", "if <", "if >", "if <=", "if >=" ->
                "if " + a + " " + cuarteta.operador().substring(3) + " " + b + " goto " + r;
            case "func" -> "func " + r + "    // marco: " + a + " celdas";
            case "end" -> "end";
            case "call" -> "call " + r;
            case "halt" -> "halt";
            case "print" -> "print " + b + " " + a;
            case "leer_caracter" -> r + " = leer caracter";
            default -> throw new IllegalArgumentException("Cuarteta desconocida: " + cuarteta);
        };
    }

    // [base + desplazamiento]
    private static String celda(String base, String desplazamiento) {
        return "[" + base + (desplazamiento.equals("0") ? "" : " + " + desplazamiento) + "]";
    }
}
