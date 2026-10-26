package com.mycompany.gramaticas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Vocabulary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.mycompany.proyectodoscompi2.analizador.Lenguaje;
import com.mycompany.proyectodoscompi2.views.coloreado.Coloreador;
import com.mycompany.proyectodoscompi2.views.coloreado.Coloreador.Tramo;
import com.mycompany.proyectodoscompi2.views.coloreado.EstiloToken;

// Pruebas del coloreado (sin abrir la interfaz)
//
// @author xavi
public class ColoreadorTest {

    // Estilo del tramo que cubre exactamente la n-esima aparicion (desde 1) de un fragmento
    private static EstiloToken estiloDe(Lenguaje lenguaje, String codigo, String fragmento, int aparicion) {
        int indice = -1;
        for (int i = 0; i < aparicion; i++) {
            indice = codigo.indexOf(fragmento, indice + 1);
            if (indice < 0) {
                fail("No aparece '" + fragmento + "' " + aparicion + " veces");
            }
        }
        for (Tramo tramo : Coloreador.clasificar(lenguaje, codigo)) {
            if (tramo.inicio() == indice && tramo.fin() == indice + fragmento.length()) {
                return tramo.estilo();
            }
        }
        fail("Ningun token cubre exactamente '" + fragmento + "' en la posicion " + indice);
        return null;
    }

    private static void esperar(Lenguaje lenguaje, String codigo, String fragmento, int aparicion, EstiloToken estilo) {
        assertEquals(estilo, estiloDe(lenguaje, codigo, fragmento, aparicion),
                "'" + fragmento + "' (aparicion " + aparicion + ")");
    }

    private static void esperar(Lenguaje lenguaje, String codigo, String fragmento, EstiloToken estilo) {
        esperar(lenguaje, codigo, fragmento, 1, estilo);
    }

    // ---------- Todos los tokens tienen estilo ----------

    @ParameterizedTest
    @EnumSource(Lenguaje.class)
    void todosLosTokensTienenEstilo(Lenguaje lenguaje) {
        Vocabulary vocabulario = lenguaje.crearLexer(CharStreams.fromString("")).getVocabulary();
        List<String> sinEstilo = new ArrayList<>();
        for (int tipo = 1; tipo <= vocabulario.getMaxTokenType(); tipo++) {
            String nombre = vocabulario.getSymbolicName(tipo);
            if (nombre != null && !Coloreador.SIN_COLOR.contains(nombre)
                    && Coloreador.estiloDe(lenguaje, nombre) == null) {
                sinEstilo.add(nombre);
            }
        }
        assertTrue(sinEstilo.isEmpty(), lenguaje + ": tokens sin estilo " + sinEstilo);
    }

    // ---------- Zetariano ----------

    private static final String CLASE_Z = """
            // Perro hereda de Animal
            public class Perro extends Animal {
                private Persona duenio = null;
                protected int[] edades;
                Persona[] amigos;

                public Perro(String nombre) {
                    super(nombre);
                }

                @Override
                public void comer() {
                    super.comer();
                    Persona p = new Persona("Ana", 3.5);
                    if (this.duenio != null && true) {
                        println("Hola " + p.getNombre());
                    }
                    x += readln();
                }
            }
            """;

    @Test
    void zetarianoPalabrasYValores() {
        Lenguaje z = Lenguaje.ZETARIANO;
        esperar(z, CLASE_Z, "// Perro hereda de Animal", EstiloToken.COMENTARIO);
        esperar(z, CLASE_Z, "public", EstiloToken.DECLARACION);
        esperar(z, CLASE_Z, "class", EstiloToken.DECLARACION);
        esperar(z, CLASE_Z, "extends", EstiloToken.DECLARACION);
        esperar(z, CLASE_Z, "new", EstiloToken.DECLARACION);
        esperar(z, CLASE_Z, "int", EstiloToken.TIPO);
        esperar(z, CLASE_Z, "void", EstiloToken.TIPO);
        esperar(z, CLASE_Z, "String", EstiloToken.TIPO);
        esperar(z, CLASE_Z, "null", EstiloToken.VALOR_ESPECIAL);
        esperar(z, CLASE_Z, "super", EstiloToken.VALOR_ESPECIAL);
        esperar(z, CLASE_Z, "this", EstiloToken.VALOR_ESPECIAL);
        esperar(z, CLASE_Z, "true", EstiloToken.VALOR_ESPECIAL);
        esperar(z, CLASE_Z, "@Override", EstiloToken.ANOTACION);
        esperar(z, CLASE_Z, "if", EstiloToken.CONTROL);
        esperar(z, CLASE_Z, "println", EstiloToken.ENTRADA_SALIDA);
        esperar(z, CLASE_Z, "readln", EstiloToken.ENTRADA_SALIDA);
        esperar(z, CLASE_Z, "\"Ana\"", EstiloToken.TEXTO);
        esperar(z, CLASE_Z, "3.5", EstiloToken.NUMERO);
        esperar(z, CLASE_Z, "&&", EstiloToken.OPERADOR);
        esperar(z, CLASE_Z, "+=", EstiloToken.OPERADOR);
        esperar(z, CLASE_Z, "{", EstiloToken.AGRUPACION);
        esperar(z, CLASE_Z, ";", EstiloToken.PUNTUACION);
    }

    @Test
    void zetarianoContexto() {
        Lenguaje z = Lenguaje.ZETARIANO;
        esperar(z, CLASE_Z, "Perro", 2, EstiloToken.CLASE);          // class Perro
        esperar(z, CLASE_Z, "Animal", 2, EstiloToken.CLASE);         // extends Animal
        esperar(z, CLASE_Z, "Persona", 1, EstiloToken.CLASE);        // Persona duenio
        esperar(z, CLASE_Z, "Persona", 2, EstiloToken.CLASE);        // Persona[] amigos
        esperar(z, CLASE_Z, "Persona", 3, EstiloToken.CLASE);        // Persona p
        esperar(z, CLASE_Z, "Persona", 4, EstiloToken.CLASE);        // new Persona(
        esperar(z, CLASE_Z, "Perro", 3, EstiloToken.FUNCION);        // constructor Perro(
        esperar(z, CLASE_Z, "comer", 1, EstiloToken.FUNCION);
        esperar(z, CLASE_Z, "comer", 2, EstiloToken.FUNCION);        // super.comer()
        esperar(z, CLASE_Z, "getNombre", EstiloToken.FUNCION);
        esperar(z, CLASE_Z, "duenio", 1, EstiloToken.IDENTIFICADOR);
        esperar(z, CLASE_Z, "nombre", 1, EstiloToken.IDENTIFICADOR);
        esperar(z, CLASE_Z, "edades", EstiloToken.IDENTIFICADOR);
    }

    // ---------- Pig Latin ----------

    private static final String PROGRAMA_PIG = """
            ## programa
               de prueba ##
            import clases.Persona.z
            import lib.utilidades.y;
            VARIABILES>
            esto p : novus Profesor(12, "Ana") : Persona ;
            esto q : null : Persona ;
            esto r : Punto {1, 2};
            esto n : numerus 5;
            esto activo : verum;
            series v[3] : decimalis;
            MAIOR>
            >> p.getNombre() >> n;
            n <<
            si (q == null && non activo) {
                perge;
            } finis;
            per (esto i : numerus 0; i < 3; i++) {
                calcular(i);
            }
            FINIS;
            """;

    @Test
    void pigLatinPalabrasYSecciones() {
        Lenguaje pig = Lenguaje.PIG_LATIN;
        esperar(pig, PROGRAMA_PIG, "## programa\n   de prueba ##", EstiloToken.COMENTARIO);
        esperar(pig, PROGRAMA_PIG, "import", EstiloToken.DECLARACION);
        esperar(pig, PROGRAMA_PIG, "esto", EstiloToken.DECLARACION);
        esperar(pig, PROGRAMA_PIG, "novus", EstiloToken.DECLARACION);
        esperar(pig, PROGRAMA_PIG, "series", EstiloToken.DECLARACION);
        esperar(pig, PROGRAMA_PIG, "numerus", EstiloToken.TIPO);
        esperar(pig, PROGRAMA_PIG, "decimalis", EstiloToken.TIPO);
        esperar(pig, PROGRAMA_PIG, "null", EstiloToken.VALOR_ESPECIAL);
        esperar(pig, PROGRAMA_PIG, "verum", EstiloToken.VALOR_ESPECIAL);
        esperar(pig, PROGRAMA_PIG, "VARIABILES", EstiloToken.SECCION);
        esperar(pig, PROGRAMA_PIG, "MAIOR", EstiloToken.SECCION);
        esperar(pig, PROGRAMA_PIG, "FINIS", EstiloToken.SECCION);
        esperar(pig, PROGRAMA_PIG, "finis", EstiloToken.CONTROL);
        esperar(pig, PROGRAMA_PIG, "si", 1, EstiloToken.CONTROL);
        esperar(pig, PROGRAMA_PIG, "perge", EstiloToken.CONTROL);
        esperar(pig, PROGRAMA_PIG, "per", 2, EstiloToken.CONTROL);       // la 1ra esta dentro de "perge"
        esperar(pig, PROGRAMA_PIG, ">>", 1, EstiloToken.ENTRADA_SALIDA);
        esperar(pig, PROGRAMA_PIG, "<<", EstiloToken.ENTRADA_SALIDA);
        esperar(pig, PROGRAMA_PIG, "non", EstiloToken.OPERADOR);
        esperar(pig, PROGRAMA_PIG, "<", 3, EstiloToken.OPERADOR);         // i < 3 (los 2 primeros son el <<)
    }

    @Test
    void pigLatinContexto() {
        Lenguaje pig = Lenguaje.PIG_LATIN;
        esperar(pig, PROGRAMA_PIG, ">", 1, EstiloToken.SECCION);          // VARIABILES>
        esperar(pig, PROGRAMA_PIG, "clases", EstiloToken.CLASE);         // ruta del import
        esperar(pig, PROGRAMA_PIG, "Persona", 1, EstiloToken.CLASE);
        esperar(pig, PROGRAMA_PIG, "z", 1, EstiloToken.CLASE);
        esperar(pig, PROGRAMA_PIG, "utilidades", EstiloToken.CLASE);
        esperar(pig, PROGRAMA_PIG, "Profesor", EstiloToken.CLASE);       // novus Profesor(
        esperar(pig, PROGRAMA_PIG, "Persona", 2, EstiloToken.CLASE);     // : Persona ;
        esperar(pig, PROGRAMA_PIG, "Persona", 3, EstiloToken.CLASE);     // null : Persona ;
        esperar(pig, PROGRAMA_PIG, "Punto", EstiloToken.CLASE);          // : Punto {
        esperar(pig, PROGRAMA_PIG, "getNombre", EstiloToken.FUNCION);
        esperar(pig, PROGRAMA_PIG, "calcular", EstiloToken.FUNCION);
        esperar(pig, PROGRAMA_PIG, "activo", 1, EstiloToken.IDENTIFICADOR);
        esperar(pig, PROGRAMA_PIG, "q", 1, EstiloToken.IDENTIFICADOR);
    }

    // ---------- Y? ----------

    private static final String PROGRAMA_Y = """
            %estructuras
            estructura Punto:
                entero x
                flotante valores[3]

            %funciones
            /* utilidades */
            definir mover({}Punto p, []entero datos, Punto otro) -> Punto:
                Punto copia
                si (p.x > 0 && verdadero) entonces
                    imprimir("mover", p.x % 2)
                contrario
                    copia.x = leer()
                para(entero i = 0; i < 3; i++):
                    romper
                retornar distancia(p, otro)
            """;

    @Test
    void yPalabrasYSecciones() {
        Lenguaje y = Lenguaje.Y;
        esperar(y, PROGRAMA_Y, "%estructuras", EstiloToken.SECCION);
        esperar(y, PROGRAMA_Y, "%funciones", EstiloToken.SECCION);
        esperar(y, PROGRAMA_Y, "estructura", 2, EstiloToken.DECLARACION);    // la 1ra esta dentro de %estructuras
        esperar(y, PROGRAMA_Y, "definir", EstiloToken.DECLARACION);
        esperar(y, PROGRAMA_Y, "entero", 1, EstiloToken.TIPO);
        esperar(y, PROGRAMA_Y, "flotante", EstiloToken.TIPO);
        esperar(y, PROGRAMA_Y, "verdadero", EstiloToken.VALOR_ESPECIAL);
        esperar(y, PROGRAMA_Y, "entonces", EstiloToken.CONTROL);
        esperar(y, PROGRAMA_Y, "contrario", EstiloToken.CONTROL);
        esperar(y, PROGRAMA_Y, "romper", EstiloToken.CONTROL);
        esperar(y, PROGRAMA_Y, "retornar", EstiloToken.CONTROL);
        esperar(y, PROGRAMA_Y, "imprimir", EstiloToken.ENTRADA_SALIDA);
        esperar(y, PROGRAMA_Y, "leer", EstiloToken.ENTRADA_SALIDA);
        esperar(y, PROGRAMA_Y, "->", EstiloToken.OPERADOR);
        esperar(y, PROGRAMA_Y, "%", 3, EstiloToken.OPERADOR);            // p.x % 2
        esperar(y, PROGRAMA_Y, "/* utilidades */", EstiloToken.COMENTARIO);
    }

    @Test
    void yContexto() {
        Lenguaje y = Lenguaje.Y;
        esperar(y, PROGRAMA_Y, "Punto", 1, EstiloToken.CLASE);          // estructura Punto:
        esperar(y, PROGRAMA_Y, "Punto", 2, EstiloToken.CLASE);          // {}Punto p
        esperar(y, PROGRAMA_Y, "Punto", 3, EstiloToken.CLASE);          // Punto otro
        esperar(y, PROGRAMA_Y, "Punto", 4, EstiloToken.CLASE);          // -> Punto
        esperar(y, PROGRAMA_Y, "Punto", 5, EstiloToken.CLASE);          // Punto copia
        esperar(y, PROGRAMA_Y, "mover", 1, EstiloToken.FUNCION);        // definir mover
        esperar(y, PROGRAMA_Y, "distancia", EstiloToken.FUNCION);
        esperar(y, PROGRAMA_Y, "copia", 1, EstiloToken.IDENTIFICADOR);
        esperar(y, PROGRAMA_Y, "datos", EstiloToken.IDENTIFICADOR);
    }

    // ---------- Errores lexicos y Unicode ----------

    @Test
    void erroresLexicos() {
        esperar(Lenguaje.ZETARIANO, "int a = 5 $ 3;", "$", EstiloToken.ERROR);
        esperar(Lenguaje.ZETARIANO, "String s = \"sin cerrar;\nint b;", "\"sin cerrar;", EstiloToken.ERROR);
        esperar(Lenguaje.ZETARIANO, "char c = 'ab';", "'ab'", EstiloToken.ERROR);
        esperar(Lenguaje.ZETARIANO, "int x; /* abierto\nint y;", "/* abierto\nint y;", EstiloToken.ERROR);
        esperar(Lenguaje.PIG_LATIN, "MAIOR>\n>> 5 % 2;\nFINIS;", "%", EstiloToken.ERROR);
        esperar(Lenguaje.PIG_LATIN, "## nunca se cierra\nMAIOR>", "## nunca se cierra\nMAIOR>", EstiloToken.ERROR);
        esperar(Lenguaje.Y, "%funciones\ndefinir f():\n    a = 1 @ 2\n", "@", EstiloToken.ERROR);
        // despues del error el coloreado sigue
        esperar(Lenguaje.ZETARIANO, "int a = 5 $ 3; return a;", "return", EstiloToken.CONTROL);
    }

    @Test
    void caracteresFueraDelPlanoBasico() {
        // el emoji ocupa 2 posiciones en Java y 1 en ANTLR: lo que viene despues no debe correrse
        String codigo = "String s = \"😀😀\" + nombre; // 😀 fin\nreturn x;";
        esperar(Lenguaje.ZETARIANO, codigo, "\"😀😀\"", EstiloToken.TEXTO);
        esperar(Lenguaje.ZETARIANO, codigo, "nombre", EstiloToken.IDENTIFICADOR);
        esperar(Lenguaje.ZETARIANO, codigo, "// 😀 fin", EstiloToken.COMENTARIO);
        esperar(Lenguaje.ZETARIANO, codigo, "return", EstiloToken.CONTROL);
    }

    @Test
    void ySaltoDeLineaSeparaVecinos() {
        // "x" termina la linea y "y" empieza la siguiente: no es una declaracion "x y"
        String codigo = "%funciones\ndefinir f() -> entero:\n    retornar x\n    y = 2\n";
        esperar(Lenguaje.Y, codigo, "x", EstiloToken.IDENTIFICADOR);
        esperar(Lenguaje.Y, codigo, "y", 1, EstiloToken.IDENTIFICADOR);
    }

    @Test
    void indentacionInconsistenteNoRompeElColoreadoDeY() {
        String codigo = "%funciones\ndefinir f():\n        a = 1\n    b = 2\n";
        esperar(Lenguaje.Y, codigo, "b", EstiloToken.IDENTIFICADOR);
        esperar(Lenguaje.Y, codigo, "2", EstiloToken.NUMERO);
    }

    // ---------- Robustez con todos los casos de la gramatica ----------

    @Test
    void coloreaTodosLosCasosDeLaGramaticaSinFallar() throws Exception {
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
                String contenido = Files.readString(archivo, StandardCharsets.UTF_8);
                for (String caso : contenido.split("(?m)^@@@ ")) {
                    String codigo = caso.contains("\n") ? caso.substring(caso.indexOf('\n') + 1) : "";
                    revisarTramos(lenguaje, codigo, archivo.getFileName() + ": " + caso.lines().findFirst().orElse(""));
                    revisados++;
                }
            }
        }
        assertTrue(revisados > 600, "Se revisaron pocos casos: " + revisados);
    }

    private static void revisarTramos(Lenguaje lenguaje, String codigo, String nombre) {
        int anterior = 0;
        for (Tramo tramo : Coloreador.clasificar(lenguaje, codigo)) {
            if (tramo.inicio() < anterior || tramo.fin() <= tramo.inicio() || tramo.fin() > codigo.length()) {
                fail(nombre + ": tramo invalido " + tramo + " (texto de " + codigo.length() + ")");
            }
            anterior = tramo.fin();
        }
    }
}
