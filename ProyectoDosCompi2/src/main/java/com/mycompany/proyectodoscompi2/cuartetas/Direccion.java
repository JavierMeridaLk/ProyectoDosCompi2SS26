package com.mycompany.proyectodoscompi2.cuartetas;

import com.mycompany.proyectodoscompi2.tipos.Tipo;

// Una celda del stack o del heap donde se puede leer o escribir
public record Direccion(String base, int desplazamiento, boolean enHeap, Tipo tipo) {

    // Destino invalido
    public static Direccion error() {
        return new Direccion("0", 0, false, Tipo.ERROR);
    }

    public boolean esError() {
        return tipo.esError();
    }
}
