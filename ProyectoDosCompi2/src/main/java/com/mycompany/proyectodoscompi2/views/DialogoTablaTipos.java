package com.mycompany.proyectodoscompi2.views;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import com.mycompany.proyectodoscompi2.tablas.Campo;
import com.mycompany.proyectodoscompi2.tablas.TablaTipos;
import com.mycompany.proyectodoscompi2.tablas.TipoDefinido;
import com.mycompany.proyectodoscompi2.views.componentes.Estilos;
import com.mycompany.proyectodoscompi2.views.componentes.TablaSoloLectura;

// Muestra la tabla de tipos del ultimo programa compilado:
public class DialogoTablaTipos extends JDialog {

    private static final String[] COLUMNAS = {"Tipo", "Categoría", "Hereda de", "Tamaño", "Archivo", "Atributo",
        "Tipo del atributo", "Visibilidad", "Declarado en", "Desplazamiento"};

    public DialogoTablaTipos(Frame propietario, TablaTipos tabla) {
        super(propietario, "Tabla de tipos", false);
        List<Object[]> filas = new ArrayList<>();
        for (TipoDefinido tipo : tabla.todos()) {
            String archivo = tipo.getArchivo() != null ? tipo.getArchivo() : "-";
            String padre = tipo.getPadre() != null ? tipo.getPadre() : "-";
            List<Campo> campos = tabla.camposCompletos(tipo);
            if (campos.isEmpty()) {
                filas.add(new Object[] {tipo.getNombre(), tipo.getCategoria(), padre, tabla.tamano(tipo), archivo,
                    "-", "-", "-", "-", "-"});
            }
            for (Campo campo : campos) {
                filas.add(new Object[] {tipo.getNombre(), tipo.getCategoria(), padre, tabla.tamano(tipo), archivo,
                    campo.nombre(), campo.tipo(), campo.visibilidad(), campo.clase(),
                    tabla.desplazamiento(tipo, campo)});
            }
        }
        setLayout(new BorderLayout());
        add(Estilos.encabezado("Tabla de tipos", Estilos.ENCABEZADO_EDITOR,
                new JLabel(tabla.todos().size() + " tipo(s)")), BorderLayout.NORTH);
        add(new JScrollPane(new TablaSoloLectura(COLUMNAS, filas)), BorderLayout.CENTER);
        setSize(1100, 520);
        setLocationRelativeTo(propietario);
    }
}
