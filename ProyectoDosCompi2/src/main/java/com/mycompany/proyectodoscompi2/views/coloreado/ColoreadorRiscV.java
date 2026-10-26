package com.mycompany.proyectodoscompi2.views.coloreado;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.mycompany.proyectodoscompi2.views.componentes.Estilos;

// Colorea codigo ensamblador RISC-V linea por linea
public final class ColoreadorRiscV {

    private static final SimpleAttributeSet NORMAL = estilo("#1E1E1E", false);
    private static final SimpleAttributeSet ETIQUETA = estilo("#1565C0", true);
    private static final SimpleAttributeSet DIRECTIVA = estilo("#8E24AA", false);
    private static final SimpleAttributeSet INSTRUCCION = estilo("#00695C", true);
    private static final SimpleAttributeSet REGISTRO = estilo("#E65100", false);
    private static final SimpleAttributeSet NUMERO = estilo("#C62828", false);
    private static final SimpleAttributeSet TEXTO = estilo("#2E7D32", false);
    private static final SimpleAttributeSet COMENTARIO = estilo("#8A8A8A", false);

    private static final Pattern ETIQUETA_INICIO = Pattern.compile("^\\s*([A-Za-z_.$][\\w.$]*):");
    private static final Pattern PRIMERA_PALABRA = Pattern.compile("^\\s*([.A-Za-z_][\\w.]*)");
    private static final Pattern REGISTROS = Pattern.compile(
            "\\b(zero|ra|sp|gp|tp|fp|t[0-6]|s[0-9]|s1[01]|a[0-7]|x[0-9]|x[12][0-9]|x3[01]|f[0-9]|f[12][0-9]|f3[01]"
            + "|ft[0-9]|ft1[01]|fs[0-9]|fs1[01]|fa[0-7])\\b");
    private static final Pattern NUMEROS = Pattern.compile("(?<![\\w.])-?(0x[0-9A-Fa-f]+|\\d+(\\.\\d+)?)\\b");
    private static final Pattern CADENAS = Pattern.compile("\"(\\\\.|[^\"\\\\])*\"|'(\\\\.|[^'\\\\])'");

    private ColoreadorRiscV() {
    }

    private static SimpleAttributeSet estilo(String hexadecimal, boolean negrita) {
        SimpleAttributeSet atributos = new SimpleAttributeSet();
        StyleConstants.setForeground(atributos, Color.decode(hexadecimal));
        StyleConstants.setFontFamily(atributos, Estilos.FUENTE_CODIGO.getFamily());
        StyleConstants.setFontSize(atributos, Estilos.FUENTE_CODIGO.getSize());
        StyleConstants.setBold(atributos, negrita);
        return atributos;
    }

    public static void colorear(StyledDocument documento, String contenido) {
        documento.setCharacterAttributes(0, documento.getLength(), NORMAL, true);
        int inicioLinea = 0;
        for (String linea : contenido.split("\n", -1)) {
            colorearLinea(documento, inicioLinea, linea);
            inicioLinea += linea.length() + 1;
        }
    }

    private static void colorearLinea(StyledDocument documento, int desplazamiento, String linea) {
        int comentario = buscarComentario(linea);
        String codigo = comentario >= 0 ? linea.substring(0, comentario) : linea;
        if (comentario >= 0) {
            pintar(documento, desplazamiento + comentario, linea.length() - comentario, COMENTARIO);
        }

        int resto = 0;
        Matcher etiqueta = ETIQUETA_INICIO.matcher(codigo);
        if (etiqueta.find()) {
            pintar(documento, desplazamiento + etiqueta.start(1), etiqueta.end() - etiqueta.start(1), ETIQUETA);
            resto = etiqueta.end();
        }
        Matcher palabra = PRIMERA_PALABRA.matcher(codigo.substring(resto));
        if (palabra.find()) {
            pintar(documento, desplazamiento + resto + palabra.start(1), palabra.group(1).length(),
                    palabra.group(1).startsWith(".") ? DIRECTIVA : INSTRUCCION);
            resto += palabra.end();
        }
        String operandos = codigo.substring(resto);
        pintarTodos(documento, desplazamiento + resto, NUMEROS.matcher(operandos), NUMERO);
        pintarTodos(documento, desplazamiento + resto, REGISTROS.matcher(operandos), REGISTRO);
        pintarTodos(documento, desplazamiento + resto, CADENAS.matcher(operandos), TEXTO);
    }

    private static int buscarComentario(String linea) {
        boolean enCadena = false;
        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            if (c == '\\' && enCadena) {
                i++;
            } else if (c == '"') {
                enCadena = !enCadena;
            } else if (c == '#' && !enCadena) {
                return i;
            }
        }
        return -1;
    }

    private static void pintarTodos(StyledDocument documento, int desplazamiento, Matcher coincidencias,
            SimpleAttributeSet estilo) {
        while (coincidencias.find()) {
            pintar(documento, desplazamiento + coincidencias.start(), coincidencias.end() - coincidencias.start(),
                    estilo);
        }
    }

    private static void pintar(StyledDocument documento, int inicio, int largo, SimpleAttributeSet estilo) {
        documento.setCharacterAttributes(inicio, largo, estilo, true);
    }
}
