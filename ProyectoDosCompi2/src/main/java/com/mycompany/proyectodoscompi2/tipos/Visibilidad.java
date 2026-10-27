package com.mycompany.proyectodoscompi2.tipos;

// Encapsulamiento de atributos, metodos y constructores
public enum Visibilidad {
    PRIVATE("private", 0),
    DEFAULT("default", 0),
    PROTECTED("protected", 1),
    PUBLIC("public", 2);

    private final String nombre;
    private final int nivel;

    Visibilidad(String nombre, int nivel) {
        this.nombre = nombre;
        this.nivel = nivel;
    }

    // Un metodo sobrescrito no puede tener menos nivel que el del padre
    public int getNivel() {
        return nivel;
    }

    // private y default no se heredan
    public boolean seHereda() {
        return nivel > 0;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
