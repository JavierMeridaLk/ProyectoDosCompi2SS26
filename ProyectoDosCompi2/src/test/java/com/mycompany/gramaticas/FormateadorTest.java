package com.mycompany.gramaticas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;

import com.mycompany.proyectodoscompi2.analizador.Lenguaje;
import com.mycompany.proyectodoscompi2.archivos.FormateadorCodigo;

// Pruebas del boton Tabular codigo
//
// @author xavi
public class FormateadorTest {

    private static String tabular(Lenguaje lenguaje, String codigo) throws FormateadorCodigo.ErrorFormato {
        return FormateadorCodigo.formatear(lenguaje, codigo);
    }

    @Test
    void zetarianoPorLlaves() throws Exception {
        String desordenado = """
                public class Perro extends Animal {
                @Override
                      public void comer() {
                super.comer();
                if (x > 0) {
                println("{ no cuenta }");
                    }
                  }
                }""";
        String esperado = """
                public class Perro extends Animal {
                    @Override
                    public void comer() {
                        super.comer();
                        if (x > 0) {
                            println("{ no cuenta }");
                        }
                    }
                }""";
        assertEquals(esperado, tabular(Lenguaje.ZETARIANO, desordenado));
    }

    @Test
    void pigLatinConFinisYComentarios() throws Exception {
        String desordenado = """
                MAIOR>
                    si (x == null) {
                >> "hola";
                  ## comentario { con llave ##
                        } aliter {
                dum (verum) {
                interrumpe;
                } finis;
                } finis;
                FINIS;""";
        String esperado = """
                MAIOR>
                si (x == null) {
                    >> "hola";
                    ## comentario { con llave ##
                } aliter {
                    dum (verum) {
                        interrumpe;
                    } finis;
                } finis;
                FINIS;""";
        assertEquals(esperado, tabular(Lenguaje.PIG_LATIN, desordenado));
    }

    @Test
    void yPasaDeDosEspaciosACuatro() throws Exception {
        String desordenado = "%funciones\ndefinir f(entero n) -> entero:\n  si (n > 0) entonces\n    retornar n\n"
                + "  retornar 0\n";
        String esperado = "%funciones\ndefinir f(entero n) -> entero:\n    si (n > 0) entonces\n        retornar n\n"
                + "    retornar 0\n";
        assertEquals(esperado, tabular(Lenguaje.Y, desordenado));
    }

    @Test
    void yConIndentacionInconsistenteNoSeTabula() {
        String codigo = "%funciones\ndefinir f():\n        a = 1\n    b = 2\n";
        assertThrows(FormateadorCodigo.ErrorFormato.class, () -> tabular(Lenguaje.Y, codigo));
    }

    @Test
    void conservaLosSaltosDeLineaDeWindows() throws Exception {
        String codigo = "class A {\r\nint x;\r\n}";
        assertEquals("class A {\r\n    int x;\r\n}", tabular(Lenguaje.ZETARIANO, codigo));
    }

    // Tabular nunca cambia el programa (mismos tokens) y tabular dos veces da lo mismo que una
    @Test
    void tabularLosProgramasDeLasPruebasDeGramatica() throws Exception {
        Path base = Path.of(getClass().getResource("/gramaticas").toURI());
        int revisados = 0;
        for (Lenguaje lenguaje : Lenguaje.values()) {
            String carpeta = switch (lenguaje) {
                case PIG_LATIN -> "piglatin";
                case Y -> "y";
                case ZETARIANO -> "zetariano";
            };
            List<Path> archivos;
            try (Stream<Path> listado = Files.list(base.resolve(carpeta))) {
                archivos = listado.sorted().toList();
            }
            for (Path archivo : archivos) {
                for (String caso : Files.readString(archivo, StandardCharsets.UTF_8).split("(?m)^@@@ ")) {
                    if (!caso.startsWith("OK archivo")) {
                        continue;
                    }
                    String nombre = archivo.getFileName() + ": " + caso.lines().findFirst().orElse("");
                    String codigo = caso.substring(caso.indexOf('\n') + 1).replaceAll("(?m)^@@> .*\\n", "");
                    String tabulado = tabular(lenguaje, codigo);
                    assertEquals(tokens(lenguaje, codigo), tokens(lenguaje, tabulado), nombre + ": cambió el programa");
                    assertEquals(tabulado, tabular(lenguaje, tabulado), nombre + ": tabular dos veces da otro resultado");
                    revisados++;
                }
            }
        }
        assertTrue(revisados > 50, "Se revisaron pocos programas: " + revisados);
    }

    // Tokens visibles del codigo (sin espacios ni saltos de linea)
    private static List<String> tokens(Lenguaje lenguaje, String codigo) {
        Lexer lexer = lenguaje.crearLexer(CharStreams.fromString(codigo));
        lexer.removeErrorListeners();
        List<String> textos = new ArrayList<>();
        for (Token token = lexer.nextToken(); token.getType() != Token.EOF; token = lexer.nextToken()) {
            if (token.getStartIndex() <= token.getStopIndex()) {
                textos.add(lexer.getVocabulary().getSymbolicName(token.getType()) + ":" + token.getText().strip());
            }
        }
        return textos;
    }
}
