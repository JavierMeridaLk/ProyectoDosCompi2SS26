package com.mycompany.proyectodoscompi2.analizador;

import java.util.Optional;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;

import com.mycompany.ZetarianoLexer;
import com.mycompany.pigLatinLexer;
import com.mycompany.yLexer;

public enum Lenguaje {
    PIG_LATIN("pig", "Pig Latin"),
    Y("y", "Y?"),
    ZETARIANO("z", "Zetariano");

    private final String extension;
    private final String nombre;

    Lenguaje(String extension, String nombre) {
        this.extension = extension;
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    // Lexer generado por ANTLR para este lenguaje
    public Lexer crearLexer(CharStream entrada) {
        return switch (this) {
            case PIG_LATIN -> new pigLatinLexer(entrada);
            case Y -> new yLexer(entrada);
            case ZETARIANO -> new ZetarianoLexer(entrada);
        };
    }

    //Detecta el lenguaje por la extension del archivo
    public static Optional<Lenguaje> desdeArchivo(String nombreArchivo) {
        int punto = nombreArchivo.lastIndexOf('.');
        if (punto < 0) {
            return Optional.empty();
        }
        String extension = nombreArchivo.substring(punto + 1);
        for (Lenguaje lenguaje : values()) {
            if (lenguaje.extension.equals(extension)) {
                return Optional.of(lenguaje);
            }
        }
        return Optional.empty();
    }
}
