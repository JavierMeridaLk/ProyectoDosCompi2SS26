package com.mycompany.proyectodoscompi2.analizador;

import java.util.Optional;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;

import com.mycompany.ZetarianoLexer;
import com.mycompany.ZetarianoParser;
import com.mycompany.pigLatinLexer;
import com.mycompany.pigLatinParser;
import com.mycompany.proyectodoscompi2.tipos.Tipo;
import com.mycompany.proyectodoscompi2.tipos.TipoPrimitivo;
import com.mycompany.yLexer;
import com.mycompany.yParser;

public enum Lenguaje {
    // TipoPrimitivo entero, decimal, cadena, caracter, booleano, void, nulo
    PIG_LATIN("pig", "Pig Latin", "numerus", "decimalis", "textum", "littera", "bool", "void", "null"),
    Y("y", "Y?", "entero", "flotante", "cadena", "caracter", "bool", "void", "nulo"),
    ZETARIANO("z", "Zetariano", "int", "double", "String", "char", "boolean", "void", "null");

    private final String extension;
    private final String nombre;
    private final String[] tipos;

    Lenguaje(String extension, String nombre, String... tipos) {
        this.extension = extension;
        this.nombre = nombre;
        this.tipos = tipos;
    }

    public String getNombre() {
        return nombre;
    }

    public String getExtension() {
        return extension;
    }

    // Nombre de un tipo con las palabras del lenguaje 
    public String nombreDe(Tipo tipo) {
        String base;
        if (!tipo.esPrimitivo()) {
            base = tipo.nombreClase();
        } else if (tipo.primitivo() == TipoPrimitivo.LEIDO) {
            base = tipos[TipoPrimitivo.CADENA.ordinal()];
        } else if (tipo.primitivo() == TipoPrimitivo.ERROR) {
            base = "?";
        } else {
            base = tipos[tipo.primitivo().ordinal()];
        }
        return base + "[]".repeat(tipo.dimensiones());
    }

    public String booleano(boolean valor) {
        return switch (this) {
            case PIG_LATIN -> valor ? "verum" : "falsus";
            case Y -> valor ? "verdadero" : "falso";
            case ZETARIANO -> valor ? "true" : "false";
        };
    }

    // Lexer generado por ANTLR para este lenguaje
    public Lexer crearLexer(CharStream entrada) {
        return switch (this) {
            case PIG_LATIN -> new pigLatinLexer(entrada);
            case Y -> new yLexer(entrada);
            case ZETARIANO -> new ZetarianoLexer(entrada);
        };
    }

    public Parser crearParser(TokenStream tokens) {
        return switch (this) {
            case PIG_LATIN -> new pigLatinParser(tokens);
            case Y -> new yParser(tokens);
            case ZETARIANO -> new ZetarianoParser(tokens);
        };
    }

    // Regla inicial de la gramatica
    public ParserRuleContext reglaInicial(Parser parser) {
        return switch (this) {
            case PIG_LATIN -> ((pigLatinParser) parser).programa();
            case Y -> ((yParser) parser).programa();
            case ZETARIANO -> ((ZetarianoParser) parser).compilacion();
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
