package com.mycompany.proyectodoscompi2.cuartetas;

//operador, operando1, operando2, resultado

public record Cuarteta(String operador, String argumento1, String argumento2, String resultado, boolean decimal) {

    public Cuarteta(String operador, String argumento1, String argumento2, String resultado) {
        this(operador, argumento1, argumento2, resultado, false);
    }

    @Override
    public String toString() {
        return "(" + operador + ", " + argumento1 + ", " + argumento2 + ", " + resultado + ")";
    }
}
