package com.mycompany.proyectodoscompi2.views;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.mycompany.proyectodoscompi2.views.componentes.Estilos;
import com.mycompany.proyectodoscompi2.views.componentes.NumerosDeLinea;

// Muestra el codigo de tres direcciones del ultimo programa compilado
public class DialogoC3D extends JDialog {

    public DialogoC3D(Frame propietario, List<String> c3d) {
        super(propietario, "Código de tres direcciones", false);
        JTextArea codigo = new JTextArea(String.join("\n", c3d));
        codigo.setEditable(false);
        codigo.setFont(Estilos.FUENTE_CODIGO);
        codigo.setTabSize(4);
        codigo.setCaretPosition(0);
        JScrollPane desplazamiento = new JScrollPane(codigo);
        desplazamiento.setRowHeaderView(new NumerosDeLinea(codigo));
        setLayout(new BorderLayout());
        add(Estilos.encabezado("Código de tres direcciones", Estilos.ENCABEZADO_RISCV,
                new JLabel(c3d.size() + " línea(s)")), BorderLayout.NORTH);
        add(desplazamiento, BorderLayout.CENTER);
        setSize(720, 680);
        setLocationRelativeTo(propietario);
    }
}
