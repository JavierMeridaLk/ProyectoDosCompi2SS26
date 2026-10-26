package com.mycompany.gramaticas;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import com.mycompany.ZetarianoLexer;
import com.mycompany.ZetarianoParser;
import com.mycompany.pigLatinLexer;
import com.mycompany.pigLatinParser;
import com.mycompany.yLexer;
import com.mycompany.yParser;

// Pruebas de las gramaticas con los lexers y parsers generados en src/main/antlr4.
//
// Los casos estan en src/test/resources/gramaticas/<lenguaje>/*.casos. Cada caso empieza con
//
//   @@@ ESPERADO tipo : nombre
//
// ESPERADO: OK, LEXICO (hay error lexico), SINTACTICO (error sintactico sin errores lexicos)
//           o ERROR (cualquier error).
// tipo:     cuanto codigo se escribe; el resto lo completa una plantilla del lenguaje
//           (archivo, miembro, sentencia, expr, variables, instr, funcion).
// En cualquier linea del caso pueden ir comprobaciones del arbol:
//   @@> forma: Aditiva(Primaria,Multiplicativa(Primaria,Primaria))   forma de la primera expresion
//   @@> tiene: DeclaracionObjeto, PrimarioSuperMetodo                 alternativas que deben aparecer
//   @@> notiene: DeclaracionConTipo                                   alternativas que no deben aparecer
//   @@> crlf                                                          usa saltos de linea de Windows (\r\n)
//
// @author xavi
public class PruebasGramaticasTest {

    private static final String ENCABEZADO = "@@@ ";
    private static final String COMPROBACION = "@@> ";

    private enum Esperado { OK, LEXICO, SINTACTICO, ERROR }

    private record Caso(String archivo, int linea, Esperado esperado, String tipo, String nombre,
            String codigo, String forma, List<String> tiene, List<String> notiene, boolean crlf) {
    }

    private record Resultado(List<String> lexicos, List<String> sintacticos, ParserRuleContext arbol,
            Parser parser) {
    }

    // ---------- Lenguajes ----------

    private interface Lenguaje {
        String plantilla(String tipo, String codigo);

        Resultado analizar(String codigo);
    }

    private static final Lenguaje ZETARIANO = new Lenguaje() {
        @Override
        public String plantilla(String tipo, String codigo) {
            return switch (tipo) {
                case "archivo" -> codigo;
                case "miembro" -> "public class Prueba {\n" + codigo + "\n}\n";
                case "sentencia" -> "public class Prueba {\npublic void metodo() {\n" + codigo + "\n}\n}\n";
                case "expr" -> "public class Prueba {\npublic int metodo() {\nreturn " + codigo + ";\n}\n}\n";
                default -> throw new IllegalArgumentException("Tipo desconocido para Zetariano: " + tipo);
            };
        }

        @Override
        public Resultado analizar(String codigo) {
            return PruebasGramaticasTest.analizar(codigo, ZetarianoLexer::new, ZetarianoParser::new,
                    p -> ((ZetarianoParser) p).compilacion());
        }
    };

    private static final Lenguaje PIG_LATIN = new Lenguaje() {
        @Override
        public String plantilla(String tipo, String codigo) {
            return switch (tipo) {
                case "archivo" -> codigo;
                case "variables" -> "VARIABILES>\n" + codigo + "\nMAIOR>\nFINIS;\n";
                case "instr" -> "MAIOR>\n" + codigo + "\nFINIS;\n";
                case "expr" -> "MAIOR>\n>> " + codigo + ";\nFINIS;\n";
                default -> throw new IllegalArgumentException("Tipo desconocido para Pig Latin: " + tipo);
            };
        }

        @Override
        public Resultado analizar(String codigo) {
            return PruebasGramaticasTest.analizar(codigo, pigLatinLexer::new, pigLatinParser::new,
                    p -> ((pigLatinParser) p).programa());
        }
    };

    private static final Lenguaje Y = new Lenguaje() {
        @Override
        public String plantilla(String tipo, String codigo) {
            return switch (tipo) {
                case "archivo" -> codigo;
                case "funcion" -> "%funciones\n" + codigo + "\n";
                case "instr" -> "%funciones\ndefinir prueba():\n" + sangrar(codigo) + "\n";
                case "expr" -> "%funciones\ndefinir prueba() -> entero:\n    retornar " + codigo + "\n";
                default -> throw new IllegalArgumentException("Tipo desconocido para Y?: " + tipo);
            };
        }

        @Override
        public Resultado analizar(String codigo) {
            return PruebasGramaticasTest.analizar(codigo, yLexer::new, yParser::new,
                    p -> ((yParser) p).programa());
        }
    };

    private static String sangrar(String codigo) {
        StringBuilder sb = new StringBuilder();
        for (String linea : codigo.split("\n", -1)) {
            sb.append(linea.isEmpty() ? "" : "    " + linea).append('\n');
        }
        return sb.toString();
    }

    // ---------- Analisis ----------

    private static Resultado analizar(String codigo, Function<CharStream, Lexer> crearLexer,
            Function<TokenStream, Parser> crearParser, Function<Parser, ParserRuleContext> reglaInicial) {
        List<String> lexicos = new ArrayList<>();
        List<String> sintacticos = new ArrayList<>();

        Lexer lexer = crearLexer.apply(CharStreams.fromString(codigo));
        lexer.removeErrorListeners();
        lexer.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> r, Object simbolo, int linea, int columna, String mensaje,
                    RecognitionException e) {
                lexicos.add(linea + ":" + columna + " " + mensaje);
            }
        });
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        tokens.fill();
        for (Token token : tokens.getTokens()) {
            String nombre = lexer.getVocabulary().getSymbolicName(token.getType());
            if (nombre != null && (nombre.equals("ERROR_LEXICO") || nombre.endsWith("_SIN_CERRAR")
                    || nombre.equals("CARACTER_INVALIDO"))) {
                lexicos.add(token.getLine() + ":" + token.getCharPositionInLine() + " " + nombre + " '"
                        + token.getText().replace("\n", "\\n") + "'");
            }
        }

        Parser parser = crearParser.apply(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> r, Object simbolo, int linea, int columna, String mensaje,
                    RecognitionException e) {
                sintacticos.add(linea + ":" + columna + " " + mensaje);
            }
        });
        ParserRuleContext arbol = reglaInicial.apply(parser);
        return new Resultado(lexicos, sintacticos, arbol, parser);
    }

    // ---------- Forma del arbol ----------

    private static String nombreAlternativa(ParseTree nodo) {
        String nombre = nodo.getClass().getSimpleName();
        return nombre.endsWith("Context") ? nombre.substring(0, nombre.length() - "Context".length()) : nombre;
    }

    private static boolean esExpresion(ParseTree nodo) {
        String nombre = nombreAlternativa(nodo);
        return nodo instanceof ParserRuleContext && nombre.startsWith("Expresion") && !nombre.equals("Expresion");
    }

    // Primera expresion del arbol (recorrido en preorden)
    private static ParseTree primeraExpresion(ParseTree nodo) {
        if (esExpresion(nodo)) {
            return nodo;
        }
        for (int i = 0; i < nodo.getChildCount(); i++) {
            ParseTree encontrada = primeraExpresion(nodo.getChild(i));
            if (encontrada != null) {
                return encontrada;
            }
        }
        return null;
    }

    // Aditiva(Primaria,Multiplicativa(Primaria,Primaria)): solo las expresiones, sin tokens
    private static String forma(ParseTree expresion) {
        List<String> hijas = new ArrayList<>();
        recolectarHijas(expresion, hijas, true);
        String nombre = nombreAlternativa(expresion).substring("Expresion".length());
        return hijas.isEmpty() ? nombre : nombre + "(" + String.join(",", hijas) + ")";
    }

    private static void recolectarHijas(ParseTree nodo, List<String> hijas, boolean raiz) {
        if (!raiz && esExpresion(nodo)) {
            hijas.add(forma(nodo));
            return;
        }
        for (int i = 0; i < nodo.getChildCount(); i++) {
            recolectarHijas(nodo.getChild(i), hijas, false);
        }
    }

    private static void alternativas(ParseTree nodo, Set<String> nombres) {
        if (nodo instanceof ParserRuleContext) {
            nombres.add(nombreAlternativa(nodo));
        }
        for (int i = 0; i < nodo.getChildCount(); i++) {
            alternativas(nodo.getChild(i), nombres);
        }
    }

    // ---------- Lectura de casos ----------

    private static List<Caso> leerCasos(Path archivo) throws IOException {
        List<Caso> casos = new ArrayList<>();
        List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);
        String nombreArchivo = archivo.getFileName().toString();
        int i = 0;
        while (i < lineas.size()) {
            String linea = lineas.get(i);
            if (!linea.startsWith(ENCABEZADO)) {
                i++;
                continue;
            }
            int inicio = i + 1;
            String encabezado = linea.substring(ENCABEZADO.length()).trim();
            int dosPuntos = encabezado.indexOf(':');
            String[] partes = encabezado.substring(0, dosPuntos).trim().split("\\s+");
            Esperado esperado = Esperado.valueOf(partes[0]);
            String tipo = partes[1];
            String nombre = encabezado.substring(dosPuntos + 1).trim();
            String forma = null;
            List<String> tiene = new ArrayList<>();
            List<String> notiene = new ArrayList<>();
            boolean crlf = false;
            i++;
            StringBuilder codigo = new StringBuilder();
            while (i < lineas.size() && !lineas.get(i).startsWith(ENCABEZADO)) {
                String actual = lineas.get(i);
                if (!actual.startsWith(COMPROBACION)) {
                    codigo.append(actual).append('\n');
                    i++;
                    continue;
                }
                String comprobacion = actual.substring(COMPROBACION.length()).trim();
                if (comprobacion.startsWith("forma:")) {
                    forma = comprobacion.substring("forma:".length()).trim();
                } else if (comprobacion.startsWith("tiene:")) {
                    tiene.addAll(lista(comprobacion.substring("tiene:".length())));
                } else if (comprobacion.startsWith("notiene:")) {
                    notiene.addAll(lista(comprobacion.substring("notiene:".length())));
                } else if (comprobacion.equals("crlf")) {
                    crlf = true;
                } else {
                    throw new IllegalStateException(nombreArchivo + ":" + (i + 1) + " comprobacion desconocida");
                }
                i++;
            }
            String texto = codigo.toString().replaceAll("\\s+$", "");
            casos.add(new Caso(nombreArchivo, inicio, esperado, tipo, nombre, texto, forma, tiene, notiene, crlf));
        }
        return casos;
    }

    private static List<String> lista(String texto) {
        List<String> elementos = new ArrayList<>();
        for (String parte : texto.split(",")) {
            if (!parte.isBlank()) {
                elementos.add(parte.trim());
            }
        }
        return elementos;
    }

    // ---------- Pruebas ----------

    @TestFactory
    Stream<DynamicNode> zetariano() throws Exception {
        return pruebasDe("zetariano", ZETARIANO);
    }

    @TestFactory
    Stream<DynamicNode> pigLatin() throws Exception {
        return pruebasDe("piglatin", PIG_LATIN);
    }

    @TestFactory
    Stream<DynamicNode> y() throws Exception {
        return pruebasDe("y", Y);
    }

    private Stream<DynamicNode> pruebasDe(String carpeta, Lenguaje lenguaje) throws IOException, URISyntaxException {
        Path directorio = Path.of(getClass().getResource("/gramaticas/" + carpeta).toURI());
        List<Path> archivos;
        try (Stream<Path> listado = Files.list(directorio)) {
            archivos = listado.filter(p -> p.toString().endsWith(".casos")).sorted().toList();
        }
        assertTrue(!archivos.isEmpty(), "No hay archivos .casos en " + directorio);
        List<DynamicNode> contenedores = new ArrayList<>();
        Set<String> nombres = new LinkedHashSet<>();
        for (Path archivo : archivos) {
            List<DynamicNode> pruebas = new ArrayList<>();
            for (Caso caso : leerCasos(archivo)) {
                String id = caso.archivo() + " > " + caso.nombre();
                if (!nombres.add(id)) {
                    fail("Caso repetido: " + id);
                }
                pruebas.add(DynamicTest.dynamicTest(
                        "[" + caso.esperado() + "] " + caso.tipo() + ": " + caso.nombre(),
                        () -> verificar(lenguaje, caso)));
            }
            contenedores.add(DynamicContainer.dynamicContainer(archivo.getFileName().toString(), pruebas));
        }
        return contenedores.stream();
    }

    private static void verificar(Lenguaje lenguaje, Caso caso) {
        String fuente = lenguaje.plantilla(caso.tipo(), caso.codigo());
        if (caso.crlf()) {
            fuente = fuente.replace("\n", "\r\n");
        }
        Resultado resultado = lenguaje.analizar(fuente);
        boolean hayLexicos = !resultado.lexicos().isEmpty();
        boolean haySintacticos = !resultado.sintacticos().isEmpty();
        boolean cumple = switch (caso.esperado()) {
            case OK -> !hayLexicos && !haySintacticos;
            case LEXICO -> hayLexicos;
            case SINTACTICO -> haySintacticos && !hayLexicos;
            case ERROR -> hayLexicos || haySintacticos;
        };
        if (!cumple) {
            fail(detalle(caso, fuente, resultado, "se esperaba " + caso.esperado()));
        }
        if (caso.forma() != null) {
            ParseTree expresion = primeraExpresion(resultado.arbol());
            String obtenida = expresion == null ? "(sin expresion)" : forma(expresion);
            if (!obtenida.equals(caso.forma())) {
                fail(detalle(caso, fuente, resultado, "forma esperada " + caso.forma() + "\n  forma obtenida " + obtenida));
            }
        }
        if (!caso.tiene().isEmpty() || !caso.notiene().isEmpty()) {
            Set<String> encontradas = new LinkedHashSet<>();
            alternativas(resultado.arbol(), encontradas);
            for (String alternativa : caso.tiene()) {
                if (!encontradas.contains(alternativa)) {
                    fail(detalle(caso, fuente, resultado, "falta la alternativa " + alternativa
                            + "\n  alternativas usadas: " + encontradas));
                }
            }
            for (String alternativa : caso.notiene()) {
                if (encontradas.contains(alternativa)) {
                    fail(detalle(caso, fuente, resultado, "no deberia usarse la alternativa " + alternativa));
                }
            }
        }
    }

    private static String detalle(Caso caso, String fuente, Resultado resultado, String problema) {
        return caso.archivo() + ":" + caso.linea() + " [" + caso.nombre() + "] " + problema
                + "\n  lexicos:     " + resultado.lexicos()
                + "\n  sintacticos: " + resultado.sintacticos()
                + "\n  arbol: " + resultado.arbol().toStringTree(resultado.parser())
                + "\n--- fuente ---\n" + fuente;
    }
}
