package com.mycompany.proyectodoscompi2.views;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import com.mycompany.proyectodoscompi2.tablas.CategoriaSimbolo;
import com.mycompany.proyectodoscompi2.tablas.Simbolo;
import com.mycompany.proyectodoscompi2.tablas.TablaSimbolos;
import com.mycompany.proyectodoscompi2.tipos.Tipo;
import com.mycompany.proyectodoscompi2.views.componentes.Estilos;
import com.mycompany.proyectodoscompi2.views.componentes.TablaSoloLectura;

// Muestra la tabla de simbolos del ultimo programa compilado
public class DialogoTablaSimbolos extends JDialog {

    private static final String[] COLUMNAS = {"Nombre", "Categoría", "Tipo", "Parámetros", "Clase", "Visibilidad",
        "@Override", "Ámbito", "Archivo", "Línea", "Desplazamiento"};

    public DialogoTablaSimbolos(Frame propietario, TablaSimbolos tabla) {
        super(propietario, "Tabla de símbolos", false);
        List<Object[]> filas = new ArrayList<>();
        for (Simbolo simbolo : tabla.todos()) {
            boolean esVariable = simbolo.categoria() == CategoriaSimbolo.VARIABLE
                    || simbolo.categoria() == CategoriaSimbolo.PARAMETRO;
            boolean esMiembro = simbolo.categoria() == CategoriaSimbolo.METODO
                    || simbolo.categoria() == CategoriaSimbolo.CONSTRUCTOR;
            filas.add(new Object[] {simbolo.nombre(), simbolo.categoria(), simbolo.tipo(),
                esVariable ? "-" : parametros(simbolo.parametros()), simbolo.clase() != null ? simbolo.clase() : "-",
                esMiembro ? simbolo.visibilidad() : "-",
                simbolo.categoria() == CategoriaSimbolo.METODO ? (simbolo.sobrescribe() ? "sí" : "no") : "-",
                simbolo.ambito(), simbolo.archivo(), simbolo.posicion().linea(),
                esVariable ? simbolo.desplazamiento() : "-"});
        }
        setLayout(new BorderLayout());
        add(Estilos.encabezado("Tabla de símbolos", Estilos.ENCABEZADO_EDITOR,
                new JLabel(filas.size() + " símbolo(s)")), BorderLayout.NORTH);
        add(new JScrollPane(new TablaSoloLectura(COLUMNAS, filas)), BorderLayout.CENTER);
        setSize(1250, 560);
        setLocationRelativeTo(propietario);
    }

    private static String parametros(List<Tipo> tipos) {
        List<String> nombres = new ArrayList<>();
        tipos.forEach(tipo -> nombres.add(tipo.toString()));
        return "(" + String.join(", ", nombres) + ")";
    }
}
