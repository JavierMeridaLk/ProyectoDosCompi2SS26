package com.mycompany.proyectodoscompi2.tablas;

import java.util.List;

import com.mycompany.proyectodoscompi2.tipos.Posicion;
import com.mycompany.proyectodoscompi2.tipos.Tipo;
import com.mycompany.proyectodoscompi2.tipos.Visibilidad;

// Una entrada de la tabla de simbolos
public record Simbolo(
        String nombre,
        CategoriaSimbolo categoria,
        Tipo tipo,
        List<Tipo> parametros,
        String ambito,
        String archivo,
        Posicion posicion,
        int desplazamiento,
        Visibilidad visibilidad,
        String clase,
        boolean sobrescribe) {

    public static Simbolo variable(String nombre, CategoriaSimbolo categoria, Tipo tipo, String ambito,
            String archivo, Posicion posicion, int desplazamiento) {
        return new Simbolo(nombre, categoria, tipo, List.of(), ambito, archivo, posicion, desplazamiento,
                Visibilidad.PUBLIC, null, false);
    }

    // Misma firma
    public boolean mismaFirma(Simbolo otro) {
        return nombre.equals(otro.nombre) && parametros.equals(otro.parametros);
    }

    // Etiqueta de la funcion en el codigo generado 
    public String etiqueta() {
        StringBuilder sufijo = new StringBuilder();
        parametros.forEach(tipo -> sufijo.append('_').append(tipo.toString().replace("[]", "_arr")));
        return switch (categoria) {
            case FUNCION -> "f_" + nombre;
            case CONSTRUCTOR -> clase + "_constructor" + sufijo;
            default -> clase + "_" + nombre + sufijo;
        };
    }
}
