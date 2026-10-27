package com.mycompany.proyectodoscompi2.cuartetas;

import com.mycompany.proyectodoscompi2.tipos.Tipo;

// Resultado de una expresion
public record Valor(String direccion, Tipo tipo) {

    // Expresion con error
    public static Valor error() {
        return new Valor("0", Tipo.ERROR);
    }

    public boolean esError() {
        return tipo.esError();
    }
}
