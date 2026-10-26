package com.mycompany.proyectodoscompi2.views;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.mycompany.proyectodoscompi2.views.componentes.Estilos;

// Tabla de errores lexicos, sintacticos y semanticos.
//
// @author xavi
public class ErroresPanel extends javax.swing.JPanel {

    private static final String[] COLUMNAS = {"Tipo", "Archivo", "Línea", "Columna", "Lexema", "Descripción"};
    private static final int[] ANCHOS = {90, 120, 55, 65, 120, 600};

    private final DefaultTableModel modelo = new DefaultTableModel(COLUMNAS, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }

        @Override
        public Class<?> getColumnClass(int columna) {
            return columna == 2 || columna == 3 ? Integer.class : String.class;
        }
    };
    private final JTable tabla = new JTable(modelo);


    public ErroresPanel() {
        initComponents();
        construir();
    }

    private void construir() {
        removeAll();
        setLayout(new BorderLayout());
        add(Estilos.encabezado("Errores léxicos, sintácticos y semánticos", Estilos.ENCABEZADO_ERRORES),
                BorderLayout.NORTH);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        tabla.setFillsViewportHeight(true);
        tabla.setRowHeight(22);
        tabla.getTableHeader().setReorderingAllowed(false);
        for (int i = 0; i < ANCHOS.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(ANCHOS[i]);
        }
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }
}
