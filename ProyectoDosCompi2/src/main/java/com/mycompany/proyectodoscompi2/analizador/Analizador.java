package com.mycompany.proyectodoscompi2.analizador;

import java.nio.file.Path;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import com.mycompany.proyectodoscompi2.errores.EscuchaErroresLexicos;
import com.mycompany.proyectodoscompi2.errores.EscuchaErroresSintacticos;
import com.mycompany.proyectodoscompi2.errores.EstrategiaErroresSintacticos;
import com.mycompany.proyectodoscompi2.errores.ManejadorErrores;
import com.mycompany.proyectodoscompi2.errores.TipoError;

// Analisis lexico y sintactico de un archivo 
public final class Analizador {

    private Analizador() {
    }

    public static ArchivoAnalizado analizar(String codigo, Lenguaje lenguaje, String nombreArchivo, Path ruta) {
        ManejadorErrores manejador = new ManejadorErrores(nombreArchivo);

        //Lexico
        Lexer lexer = lenguaje.crearLexer(CharStreams.fromString(codigo, nombreArchivo));
        lexer.removeErrorListeners();
        lexer.addErrorListener(new EscuchaErroresLexicos(manejador));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        tokens.fill();
        reportarTokensInvalidos(tokens, lexer, manejador);

        //Sintactico
        Parser parser = lenguaje.crearParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new EscuchaErroresSintacticos(manejador));
        parser.setErrorHandler(new EstrategiaErroresSintacticos());
        ParserRuleContext arbol;
        try {
            arbol = lenguaje.reglaInicial(parser);
        } catch (StackOverflowError e) {
            manejador.agregar(TipoError.SINTACTICO, 1, 1, null,
                    "El programa tiene demasiados niveles de anidamiento para poder analizarse");
            arbol = new ParserRuleContext();
        }
        return new ArchivoAnalizado(lenguaje, ruta, arbol, manejador);
    }

    // Los tokens de error van al canal oculto para que el parser siga
    private static void reportarTokensInvalidos(CommonTokenStream tokens, Lexer lexer, ManejadorErrores manejador) {
        for (Token token : tokens.getTokens()) {
            String nombre = lexer.getVocabulary().getSymbolicName(token.getType());
            if (nombre == null) {
                continue;
            }
            switch (nombre) {
                case "ERROR_LEXICO" -> manejador.lexico(token,
                        "Símbolo no reconocido por el lenguaje: '" + token.getText() + "'");
                case "TEXTO_SIN_CERRAR" -> manejador.lexico(token,
                        "Cadena sin cerrar: falta la comilla doble (\") de cierre en la misma línea");
                case "CARACTER_INVALIDO" -> manejador.lexico(token,
                        "Literal de carácter inválido: debe contener exactamente un carácter entre comillas simples");
                case "COMENTARIO_SIN_CERRAR" -> manejador.lexico(token, "Comentario de bloque sin cerrar");
                default -> {
                }
            }
        }
    }
}
