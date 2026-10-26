package com.mycompany.proyectodoscompi2.views.coloreado;

import java.awt.Color;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.mycompany.proyectodoscompi2.views.componentes.Estilos;

// Categorias del coloreado con su color (sin cursivas ni fondos)
public enum EstiloToken {

    CONTROL("#8E24AA", true),
    DECLARACION("#283593", true),       
    TIPO("#E65100", false),             
    VALOR_ESPECIAL("#AD1457", false),   
    ANOTACION("#9E7C00", true),         
    ENTRADA_SALIDA("#7B1E3A", true),   
    SECCION("#00ACC1", true),           
    CLASE("#00695C", false),           
    FUNCION("#1565C0", false),          
    NUMERO("#C62828", false),          
    TEXTO("#388E3C", false),            
    COMENTARIO("#8A8A8A", false),      
    OPERADOR("#546E7A", false),        
    AGRUPACION("#455A64", false),      
    PUNTUACION("#616161", false),     
    IDENTIFICADOR("#1E1E1E", false),  
    ERROR("#D50000", true),            
    NORMAL("#1E1E1E", false);

    private final SimpleAttributeSet atributos = new SimpleAttributeSet();

    EstiloToken(String hexadecimal, boolean negrita) {
        StyleConstants.setForeground(atributos, Color.decode(hexadecimal));
        StyleConstants.setFontFamily(atributos, Estilos.FUENTE_CODIGO.getFamily());
        StyleConstants.setFontSize(atributos, Estilos.FUENTE_CODIGO.getSize());
        StyleConstants.setBold(atributos, negrita);
        StyleConstants.setItalic(atributos, false);
        StyleConstants.setUnderline(atributos, name().equals("ERROR"));
    }

    public SimpleAttributeSet getAtributos() {
        return atributos;
    }
}
