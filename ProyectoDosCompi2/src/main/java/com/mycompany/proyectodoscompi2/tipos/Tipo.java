package com.mycompany.proyectodoscompi2.tipos;

// Tipo de una variable, parametro o funcion
public record Tipo(TipoPrimitivo primitivo, String nombreClase, int dimensiones) {

    public static final Tipo ENTERO = primitivo(TipoPrimitivo.ENTERO);
    public static final Tipo DECIMAL = primitivo(TipoPrimitivo.DECIMAL);
    public static final Tipo CADENA = primitivo(TipoPrimitivo.CADENA);
    public static final Tipo CARACTER = primitivo(TipoPrimitivo.CARACTER);
    public static final Tipo BOOLEANO = primitivo(TipoPrimitivo.BOOLEANO);
    public static final Tipo VOID = primitivo(TipoPrimitivo.VOID);
    public static final Tipo NULO = primitivo(TipoPrimitivo.NULO);
    public static final Tipo LEIDO = primitivo(TipoPrimitivo.LEIDO);
    public static final Tipo ERROR = primitivo(TipoPrimitivo.ERROR);

    public static Tipo primitivo(TipoPrimitivo primitivo) {
        return new Tipo(primitivo, null, 0);
    }

    public static Tipo clase(String nombre) {
        return new Tipo(null, nombre, 0);
    }

    public Tipo conDimensiones(int dimensiones) {
        return new Tipo(primitivo, nombreClase, dimensiones);
    }

    public boolean esPrimitivo() {
        return primitivo != null;
    }

    // Primitivo y no arreglo
    public boolean esSimple() {
        return esPrimitivo() && dimensiones == 0;
    }

    // Estructura o clase (no arreglo)
    public boolean esObjeto() {
        return !esPrimitivo() && dimensiones == 0;
    }

    public boolean esError() {
        return primitivo == TipoPrimitivo.ERROR;
    }

    @Override
    public String toString() {
        return (esPrimitivo() ? primitivo.name().toLowerCase() : nombreClase) + "[]".repeat(dimensiones);
    }
}
