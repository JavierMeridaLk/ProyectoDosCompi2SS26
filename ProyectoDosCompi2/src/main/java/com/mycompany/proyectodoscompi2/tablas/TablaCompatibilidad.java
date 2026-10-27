package com.mycompany.proyectodoscompi2.tablas;

import com.mycompany.proyectodoscompi2.tipos.OperadorBinario;
import com.mycompany.proyectodoscompi2.tipos.OperadorUnario;
import com.mycompany.proyectodoscompi2.tipos.TipoPrimitivo;
import static com.mycompany.proyectodoscompi2.tipos.TipoPrimitivo.BOOLEANO;
import static com.mycompany.proyectodoscompi2.tipos.TipoPrimitivo.CADENA;
import static com.mycompany.proyectodoscompi2.tipos.TipoPrimitivo.CARACTER;
import static com.mycompany.proyectodoscompi2.tipos.TipoPrimitivo.DECIMAL;
import static com.mycompany.proyectodoscompi2.tipos.TipoPrimitivo.ENTERO;
import static com.mycompany.proyectodoscompi2.tipos.TipoPrimitivo.NULO;
import static com.mycompany.proyectodoscompi2.tipos.TipoPrimitivo.VOID;

// Compatibilidad de tipos primitivos, igual para los tres lenguajes
public final class TablaCompatibilidad {

    private TablaCompatibilidad() {
    }

    // Tipo del resultado, o null si la operacion no es valida
    public static TipoPrimitivo resultado(OperadorBinario operador, TipoPrimitivo izquierda, TipoPrimitivo derecha) {
        switch (operador) {
            case SUMA:
                if ((izquierda == CADENA && esValor(derecha)) || (derecha == CADENA && esValor(izquierda))) {
                    return CADENA;
                }
                return aritmetico(izquierda, derecha);
            case RESTA:
            case MULTIPLICACION:
            case DIVISION:
                return aritmetico(izquierda, derecha);
            case MODULO:
                return esEntero(izquierda) && esEntero(derecha) ? ENTERO : null;
            case MENOR_QUE:
            case MENOR_O_IGUAL_QUE:
            case MAYOR_QUE:
            case MAYOR_O_IGUAL_QUE:
                return esNumerico(izquierda) && esNumerico(derecha) ? BOOLEANO : null;
            case IGUAL_QUE:
            case DIFERENTE_DE:
                return (esNumerico(izquierda) && esNumerico(derecha)) || (izquierda == derecha && izquierda != VOID)
                        ? BOOLEANO : null;
            case Y_LOGICO:
            case O_LOGICO:
                return izquierda == BOOLEANO && derecha == BOOLEANO ? BOOLEANO : null;
            default:
                return null;
        }
    }

    // Tipo del resultado, o null si la operacion no es valida
    public static TipoPrimitivo resultado(OperadorUnario operador, TipoPrimitivo operando) {
        switch (operador) {
            case NEGATIVO:
            case POSITIVO:
                return esNumerico(operando) ? (operando == DECIMAL ? DECIMAL : ENTERO) : null;
            case PRE_INCREMENTO:
            case PRE_DECREMENTO:
                return esNumerico(operando) ? operando : null;
            case NEGACION:
                return operando == BOOLEANO ? BOOLEANO : null;
            default:
                return null;
        }
    }

    // Se puede guardar un primitivo de tipo origen donde se espera uno de tipo destino
    public static boolean esAsignable(TipoPrimitivo destino, TipoPrimitivo origen) {
        if (destino == origen && destino != NULO) {
            return true;
        }
        return switch (destino) {
            case DECIMAL -> origen == ENTERO || origen == CARACTER;
            case ENTERO -> origen == CARACTER;
            default -> false;
        };
    }

    private static TipoPrimitivo aritmetico(TipoPrimitivo izquierda, TipoPrimitivo derecha) {
        if (!esNumerico(izquierda) || !esNumerico(derecha)) {
            return null;
        }
        return izquierda == DECIMAL || derecha == DECIMAL ? DECIMAL : ENTERO;
    }

    public static boolean esEntero(TipoPrimitivo tipo) {
        return tipo == ENTERO || tipo == CARACTER;
    }

    public static boolean esNumerico(TipoPrimitivo tipo) {
        return esEntero(tipo) || tipo == DECIMAL;
    }

    // Tipos que tienen un valor (no void ni nulo)
    private static boolean esValor(TipoPrimitivo tipo) {
        return tipo != null && tipo != VOID && tipo != NULO;
    }

    // Tipos que muestra la tabla de compatibilidad (sin void, nulo ni los internos)
    public static TipoPrimitivo[] tipos() {
        return new TipoPrimitivo[] {ENTERO, DECIMAL, CADENA, CARACTER, BOOLEANO};
    }
}
