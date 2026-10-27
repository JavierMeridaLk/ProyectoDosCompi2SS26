package com.mycompany.proyectodoscompi2.tablas;

import java.util.List;

import com.mycompany.proyectodoscompi2.tipos.Posicion;
import com.mycompany.proyectodoscompi2.tipos.Tipo;
import com.mycompany.proyectodoscompi2.tipos.Visibilidad;

// Atributo de una estructura o clase
public record Campo(String nombre, Tipo tipo, List<Integer> tamanos, Posicion posicion, Visibilidad visibilidad,
        String clase) {
}
