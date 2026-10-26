package com.mycompany.proyectodoscompi2.views.coloreado;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;

import com.mycompany.proyectodoscompi2.analizador.Lenguaje;

public class Coloreador {

    private static final int ESPERA_MS = 150;

    public static final Set<String> SIN_COLOR = Set.of("ESPACIOS", "ESPACIOS_EN_BLANCO", "NEWLINE", "INDENT", "DEDENT");

    private static final Map<Lenguaje, Map<String, EstiloToken>> ESTILOS = new EnumMap<>(Lenguaje.class);

    static {
        Map<String, EstiloToken> comunes = new HashMap<>();
        asignar(comunes, EstiloToken.NUMERO, "ENTERO", "DECIMAL");
        asignar(comunes, EstiloToken.TEXTO, "TEXTO", "CARACTER");
        asignar(comunes, EstiloToken.COMENTARIO, "COMENTARIO_DE_LINEA", "COMENTARIO_DE_BLOQUE");
        asignar(comunes, EstiloToken.IDENTIFICADOR, "IDENTIFICADOR");
        asignar(comunes, EstiloToken.AGRUPACION, "PARENTESIS_IZQUIERDO", "PARENTESIS_DERECHO",
                "CORCHETE_IZQUIERDO", "CORCHETE_DERECHO", "LLAVE_IZQUIERDA", "LLAVE_DERECHA");
        asignar(comunes, EstiloToken.PUNTUACION, "PUNTO_Y_COMA", "COMA", "PUNTO", "DOS_PUNTOS");
        asignar(comunes, EstiloToken.OPERADOR, "SUMA", "RESTA", "MULTIPLICACION", "DIVISION", "MODULO",
                "ASIGNACION", "IGUAL_QUE", "DIFERENTE_DE", "MENOR_QUE", "MAYOR_QUE", "MENOR_O_IGUAL_QUE",
                "MAYOR_O_IGUAL_QUE", "Y_LOGICO", "O_LOGICO", "NO_LOGICO", "INCREMENTO", "DECREMENTO");
        asignar(comunes, EstiloToken.ERROR, "ERROR_LEXICO", "TEXTO_SIN_CERRAR", "COMENTARIO_SIN_CERRAR",
                "CARACTER_INVALIDO");

        Map<String, EstiloToken> zetariano = new HashMap<>(comunes);
        asignar(zetariano, EstiloToken.CONTROL, "IF", "ELSE", "SWITCH", "CASE", "DEFAULT", "FOR", "WHILE", "DO",
                "BREAK", "CONTINUE", "RETURN");
        asignar(zetariano, EstiloToken.DECLARACION, "PUBLIC", "PRIVATE", "PROTECTED", "STATIC", "CLASS",
                "EXTENDS", "NEW");
        asignar(zetariano, EstiloToken.TIPO, "INT", "DOUBLE", "CHAR", "BOOLEAN", "STRING", "VOID");
        asignar(zetariano, EstiloToken.VALOR_ESPECIAL, "THIS", "SUPER", "NULL", "TRUE", "FALSE");
        asignar(zetariano, EstiloToken.ANOTACION, "OVERRIDE");
        asignar(zetariano, EstiloToken.ENTRADA_SALIDA, "PRINTLN", "PRINT", "READLN");
        asignar(zetariano, EstiloToken.OPERADOR, "MAS_IGUAL", "MENOS_IGUAL", "POR_IGUAL", "ENTRE_IGUAL",
                "MODULO_IGUAL", "INTERROGACION");
        ESTILOS.put(Lenguaje.ZETARIANO, zetariano);

        Map<String, EstiloToken> pigLatin = new HashMap<>(comunes);
        // FINIS es la palabra "finis" que cierra bloques; FIN_PROGRAMA es "FINIS" que cierra el programa
        asignar(pigLatin, EstiloToken.CONTROL, "SI", "ALITER", "DUM", "FACERE", "PER", "PERGE", "INTERRUMPE",
                "FINIS");
        asignar(pigLatin, EstiloToken.DECLARACION, "IMPORT", "ESTO", "SERIES", "NOVUS");
        asignar(pigLatin, EstiloToken.TIPO, "NUMERUS", "TEXTUM", "DECIMALIS", "LITTERA", "BOOL");
        asignar(pigLatin, EstiloToken.VALOR_ESPECIAL, "VERUM", "FALSUS", "NULL");
        asignar(pigLatin, EstiloToken.ENTRADA_SALIDA, "IMPRIMIR", "LEER");
        asignar(pigLatin, EstiloToken.SECCION, "VARIABILES", "MAIOR", "FIN_PROGRAMA");
        asignar(pigLatin, EstiloToken.OPERADOR, "NEGACION");
        ESTILOS.put(Lenguaje.PIG_LATIN, pigLatin);

        Map<String, EstiloToken> y = new HashMap<>(comunes);
        asignar(y, EstiloToken.CONTROL, "SI", "SINO", "CONTRARIO", "ENTONCES", "ELEGIR", "CASO", "SIEMPRE",
                "PARA", "MIENTRAS", "HACER", "ROMPER", "CONTINUAR", "RETORNAR");
        asignar(y, EstiloToken.DECLARACION, "DEFINIR", "ESTRUCTURA");
        asignar(y, EstiloToken.TIPO, "ENTERO_T", "FLOTANTE_T", "CADENA_T", "CARACTER_T", "BOOL_T");
        asignar(y, EstiloToken.VALOR_ESPECIAL, "VERDADERO", "FALSO");
        asignar(y, EstiloToken.ENTRADA_SALIDA, "IMPRIMIR", "LEER");
        asignar(y, EstiloToken.SECCION, "SECCION_ESTRUCTURAS", "SECCION_FUNCIONES");
        asignar(y, EstiloToken.OPERADOR, "FLECHA");
        ESTILOS.put(Lenguaje.Y, y);
    }

    private static void asignar(Map<String, EstiloToken> mapa, EstiloToken estilo, String... nombres) {
        for (String nombre : nombres) {
            mapa.put(nombre, estilo);
        }
    }

    // Estilo de un tipo de token por su nombre
    public static EstiloToken estiloDe(Lenguaje lenguaje, String nombreToken) {
        return ESTILOS.get(lenguaje).get(nombreToken);
    }

    // Clasificacion 


    public record Tramo(int inicio, int fin, EstiloToken estilo) {
    }

    private record Pieza(String nombre, int inicio, int fin, boolean visible, boolean pintable) {
    }

    public static List<Tramo> clasificar(Lenguaje lenguaje, String contenido) {
        int[] posiciones = posicionesEnTexto(contenido);
        Lexer lexer = lenguaje.crearLexer(CharStreams.fromString(contenido));
        lexer.removeErrorListeners();
        Vocabulary vocabulario = lexer.getVocabulary();

        List<Pieza> piezas = new ArrayList<>();
        for (Token token = lexer.nextToken(); token.getType() != Token.EOF; token = lexer.nextToken()) {
            String nombre = vocabulario.getSymbolicName(token.getType());
            if (nombre == null) {
                continue;
            }
            boolean vacio = token.getStartIndex() > token.getStopIndex();
            int inicio = vacio ? 0 : posiciones[token.getStartIndex()];
            int fin = vacio ? 0 : posiciones[token.getStopIndex() + 1];
            piezas.add(new Pieza(nombre, inicio, fin, token.getChannel() == Token.DEFAULT_CHANNEL,
                    !vacio && !SIN_COLOR.contains(nombre)));
        }

        // Solo los tokens visibles cuentan como vecinos 
        List<Integer> visibles = new ArrayList<>();
        for (int i = 0; i < piezas.size(); i++) {
            if (piezas.get(i).visible()) {
                visibles.add(i);
            }
        }
        EstiloToken[] estilos = new EstiloToken[piezas.size()];
        for (int i = 0; i < piezas.size(); i++) {
            EstiloToken estilo = estiloDe(lenguaje, piezas.get(i).nombre());
            estilos[i] = estilo == null ? EstiloToken.NORMAL : estilo;
        }
        for (int v = 0; v < visibles.size(); v++) {
            int i = visibles.get(v);
            EstiloToken contextual = porContexto(lenguaje, piezas, visibles, v);
            if (contextual != null) {
                estilos[i] = contextual;
            }
        }

        List<Tramo> tramos = new ArrayList<>();
        for (int i = 0; i < piezas.size(); i++) {
            if (piezas.get(i).pintable()) {
                tramos.add(new Tramo(piezas.get(i).inicio(), piezas.get(i).fin(), estilos[i]));
            }
        }
        return tramos;
    }

    private static String vecino(List<Pieza> piezas, List<Integer> visibles, int v, int desplazamiento) {
        int j = v + desplazamiento;
        return j < 0 || j >= visibles.size() ? "" : piezas.get(visibles.get(j)).nombre();
    }

    private static EstiloToken porContexto(Lenguaje lenguaje, List<Pieza> piezas, List<Integer> visibles, int v) {
        String actual = vecino(piezas, visibles, v, 0);
        String anterior = vecino(piezas, visibles, v, -1);
        String siguiente = vecino(piezas, visibles, v, 1);

        if (lenguaje == Lenguaje.PIG_LATIN && actual.equals("MAYOR_QUE")
                && (anterior.equals("VARIABILES") || anterior.equals("MAIOR"))) {
            return EstiloToken.SECCION;
        }
        if (!actual.equals("IDENTIFICADOR")) {
            return null;
        }
        if (lenguaje == Lenguaje.PIG_LATIN && esRutaImportacion(piezas, visibles, v)) {
            return EstiloToken.CLASE;
        }
        if (Set.of("CLASS", "EXTENDS", "NEW", "NOVUS", "ESTRUCTURA", "FLECHA").contains(anterior)) {
            return EstiloToken.CLASE;
        }
        if (anterior.equals("LLAVE_DERECHA") && vecino(piezas, visibles, v, -2).equals("LLAVE_IZQUIERDA")
                && siguiente.equals("IDENTIFICADOR")) {
            return EstiloToken.CLASE;
        }
        if (anterior.equals("DEFINIR") || siguiente.equals("PARENTESIS_IZQUIERDO")) {
            return EstiloToken.FUNCION;
        }
        if (lenguaje == Lenguaje.PIG_LATIN && anterior.equals("DOS_PUNTOS")
                && (siguiente.equals("PUNTO_Y_COMA") || siguiente.equals("LLAVE_IZQUIERDA"))) {
            return EstiloToken.CLASE;
        }
        if (lenguaje != Lenguaje.PIG_LATIN) {
            if (siguiente.equals("IDENTIFICADOR")) {
                return EstiloToken.CLASE;
            }
            if (siguiente.equals("CORCHETE_IZQUIERDO") && vecino(piezas, visibles, v, 2).equals("CORCHETE_DERECHO")) {
                return EstiloToken.CLASE;
            }
        }
        return null;
    }

    private static boolean esRutaImportacion(List<Pieza> piezas, List<Integer> visibles, int v) {
        for (int j = v - 1; j >= 0; j--) {
            String nombre = vecino(piezas, visibles, j, 0);
            if (nombre.equals("IMPORT")) {
                return true;
            }
            if (!nombre.equals("IDENTIFICADOR") && !nombre.equals("PUNTO")) {
                return false;
            }
            if (nombre.equals(vecino(piezas, visibles, j + 1, 0))) {
                return false;
            }
        }
        return false;
    }

    private static int[] posicionesEnTexto(String contenido) {
        int puntos = contenido.codePointCount(0, contenido.length());
        int[] posiciones = new int[puntos + 1];
        int indice = 0;
        for (int i = 0; i < puntos; i++) {
            posiciones[i] = indice;
            indice += Character.charCount(contenido.codePointAt(indice));
        }
        posiciones[puntos] = indice;
        return posiciones;
    }

    //Editor 

    private final JTextPane texto;
    private final Timer temporizador;
    private Lenguaje lenguaje;

    public Coloreador(JTextPane texto) {
        this.texto = texto;
        this.temporizador = new Timer(ESPERA_MS, e -> colorear());
        this.temporizador.setRepeats(false);
        texto.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                temporizador.restart();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                temporizador.restart();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
    }

    // Lenguaje del archivo
    public void setLenguaje(Lenguaje lenguaje) {
        this.lenguaje = lenguaje;
        colorear();
    }

    public void colorear() {
        StyledDocument documento = texto.getStyledDocument();
        int largo = documento.getLength();
        documento.setCharacterAttributes(0, largo, EstiloToken.NORMAL.getAtributos(), true);
        if (lenguaje == null || largo == 0) {
            return;
        }
        String contenido;
        try {
            contenido = documento.getText(0, largo);
        } catch (BadLocationException e) {
            return;
        }
        for (Tramo tramo : clasificar(lenguaje, contenido)) {
            if (tramo.estilo() != EstiloToken.NORMAL) {
                documento.setCharacterAttributes(tramo.inicio(), tramo.fin() - tramo.inicio(),
                        tramo.estilo().getAtributos(), true);
            }
        }
    }
}
