package com.mycompany.proyectodoscompi2.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

import com.mycompany.proyectodoscompi2.archivos.GestorArchivos;
import com.mycompany.proyectodoscompi2.riscv.Rars;
import com.mycompany.proyectodoscompi2.views.componentes.Estilos;
import com.mycompany.proyectodoscompi2.views.componentes.NumerosDeLinea;

// Muestra el codigo ensamblador RISC-V generado
// @author xavi
public class CodigoRiscVPanel extends javax.swing.JPanel {

    private static final String SIN_CODIGO = "# Todavía no se ha generado código RISC-V.\n";
    private static final int ESPACIOS_POR_TAB = 8;

    // texto sin coloreado: el codigo generado se muestra tal cual
    private final JTextPane codigo = new JTextPane() {
        // sin ajuste de linea: las instrucciones largas usan la barra horizontal
        @Override
        public boolean getScrollableTracksViewportWidth() {
            return getParent() == null || getUI().getPreferredSize(this).width <= getParent().getWidth();
        }
    };
    private final JLabel resumen = new JLabel(" ");
    private final JButton copiar = new JButton("Copiar");
    private final JButton guardar = new JButton("Guardar");
    private final JButton compilar = new JButton("Compilar");
    private boolean hayCodigo;
    private String ensamblador;
    private Supplier<List<Path>> carpetasRars = List::of;
    private static final Preferences PREFERENCIAS = Preferences.userNodeForPackage(CodigoRiscVPanel.class);


    public CodigoRiscVPanel() {
        initComponents();
        construir();
    }

    private void construir() {
        removeAll();
        setLayout(new BorderLayout());
        add(Estilos.encabezado("Código ensamblador RISC-V", Estilos.ENCABEZADO_RISCV), BorderLayout.NORTH);

        codigo.setEditable(false);
        codigo.setFont(Estilos.FUENTE_CODIGO);
        codigo.setBackground(Color.WHITE);
        codigo.setMargin(new java.awt.Insets(4, 10, 4, 10));
        configurarTabs();
        JScrollPane desplazamiento = new JScrollPane(codigo);
        desplazamiento.setRowHeaderView(new NumerosDeLinea(codigo));
        desplazamiento.setBorder(BorderFactory.createEmptyBorder());
        desplazamiento.getViewport().setBackground(Color.WHITE);
        desplazamiento.getVerticalScrollBar().setUnitIncrement(16);
        desplazamiento.getHorizontalScrollBar().setUnitIncrement(16);
        add(desplazamiento, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        botones.setOpaque(false);
        guardar.setFocusable(false);
        compilar.setFocusable(false);  
        copiar.setFocusable(false);
        guardar.addActionListener(e -> guardar());
        copiar.addActionListener(e -> copiar());
        compilar.addActionListener(e -> compilar());
        botones.add(guardar);
        botones.add(compilar);
        botones.add(copiar);

        resumen.setForeground(Color.DARK_GRAY);
        JPanel barraInferior = new JPanel(new BorderLayout());
        barraInferior.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Estilos.BORDE),
                BorderFactory.createEmptyBorder(4, 10, 4, 4)));
        barraInferior.add(resumen, BorderLayout.WEST);
        barraInferior.add(botones, BorderLayout.EAST);
        add(barraInferior, BorderLayout.SOUTH);

        setCodigo(null);
    }

    // Tabulador de 8 espacios para alinear instrucciones y operandos
    private void configurarTabs() {
        int ancho = codigo.getFontMetrics(codigo.getFont()).charWidth(' ') * ESPACIOS_POR_TAB;
        TabStop[] paradas = new TabStop[100];
        for (int i = 0; i < paradas.length; i++) {
            paradas[i] = new TabStop((i + 1) * ancho);
        }
        Style estilo = codigo.getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setTabSet(estilo, new TabSet(paradas));
    }

    // Muestra el codigo generado 
    public void setCodigo(String ensamblador) {
        this.ensamblador = ensamblador;
        hayCodigo = ensamblador != null && !ensamblador.isBlank();
        String texto = hayCodigo ? ensamblador : SIN_CODIGO;
        codigo.setText(texto);
        codigo.setCaretPosition(0);
        guardar.setEnabled(hayCodigo);
        compilar.setEnabled(hayCodigo);
        copiar.setEnabled(hayCodigo);
        int lineas = codigo.getDocument().getDefaultRootElement().getElementCount();
        resumen.setText(hayCodigo ? lineas + (lineas == 1 ? " línea" : " líneas") : " ");
    }

    // Carpetas donde buscar herramientas/rars1_6.jar ademas de la de trabajo y la del jar (la raiz del proyecto)
    public void setCarpetasRars(Supplier<List<Path>> carpetas) {
        carpetasRars = carpetas;
    }

    // Guarda el codigo en un .s y lo abre en la ventana de RARS, donde se ensambla (F3) y se ejecuta (F5)
    private void compilar() {
        Optional<Path> rars = buscarRars();
        if (rars.isEmpty()) {
            return;
        }
        try {
            Path carpeta = Path.of(System.getProperty("java.io.tmpdir"), "ide-xs2");
            Files.createDirectories(carpeta);
            Path programa = carpeta.resolve("programa.s");
            GestorArchivos.escribir(programa, ensamblador);
            Rars.abrir(rars.get(), programa);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "No se pudo abrir RARS:\n" + e.getMessage(), "Compilar",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private Optional<Path> buscarRars() {
        List<Path> carpetas = new ArrayList<>(carpetasRars.get());
        carpetas.addAll(Rars.carpetasPorDefecto());
        Optional<Path> encontrado = Rars.buscar(carpetas);
        if (encontrado.isPresent()) {
            return encontrado;
        }
        String elegido = PREFERENCIAS.get("rars", null);
        if (elegido != null && Files.isRegularFile(Path.of(elegido))) {
            return Optional.of(Path.of(elegido));
        }
        JOptionPane.showMessageDialog(this, "No se encontró RARS en " + Rars.CARPETA + "/" + Rars.ARCHIVO
                + ".\nElige el archivo .jar de RARS (se descarga de github.com/TheThirdOne/rars/releases).",
                "Compilar", JOptionPane.INFORMATION_MESSAGE);
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Elegir RARS");
        selector.setFileFilter(new FileNameExtensionFilter("RARS (*.jar)", "jar"));
        if (selector.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return Optional.empty();
        }
        Path rars = selector.getSelectedFile().toPath();
        PREFERENCIAS.put("rars", rars.toString());
        return Optional.of(rars);
    }

    private void copiar() {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(codigo.getText()), null);
    }

    private void guardar() {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar código RISC-V");
        selector.setFileFilter(new FileNameExtensionFilter("Ensamblador RISC-V (*.s, *.asm)", "s", "asm"));
        selector.setSelectedFile(new File("programa.s"));
        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        Path destino = selector.getSelectedFile().toPath();
        String nombre = destino.getFileName().toString();
        if (!nombre.endsWith(".s") && !nombre.endsWith(".asm")) {
            destino = destino.resolveSibling(nombre + ".s");
        }
        try {
            GestorArchivos.escribir(destino, codigo.getText());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar:\n" + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
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
