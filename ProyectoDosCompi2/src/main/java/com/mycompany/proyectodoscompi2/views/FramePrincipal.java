package com.mycompany.proyectodoscompi2.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.mycompany.proyectodoscompi2.analizador.Compilador;
import com.mycompany.proyectodoscompi2.analizador.ResultadoCompilacion;
import com.mycompany.proyectodoscompi2.archivos.GestorArchivos;
import com.mycompany.proyectodoscompi2.cuartetas.TraductorC3D;
import com.mycompany.proyectodoscompi2.riscv.TraductorRiscV;
import com.mycompany.proyectodoscompi2.views.componentes.PestanaEditor;

// Ventana principal del IDE.
//
// @author xavi
public class FramePrincipal extends javax.swing.JFrame {

    private final ArbolDeProyectoPanel arbolProyecto = new ArbolDeProyectoPanel();
    private final EditorDeTexctoPanel editor = new EditorDeTexctoPanel();
    private final ErroresPanel errores = new ErroresPanel();
    private final CodigoRiscVPanel codigoRiscV = new CodigoRiscVPanel();
    private ResultadoCompilacion ultimoResultado;   // null hasta compilar algo

    public FramePrincipal() {
        initComponents();
        this.setTitle("IDE XS2");

        colocar(jPanel1, arbolProyecto, new Dimension(280, 615));
        colocar(jPanel2, editor, new Dimension(1000, 615));
        colocar(jPanel3, errores, new Dimension(1300, 260));
        colocar(jPanel4, codigoRiscV, new Dimension(500, 900));
        codigoRiscV.setCarpetasRars(() -> arbolProyecto.getRaiz().map(List::of).orElse(List.of()));
        configurarMenus();
        conectarPaneles();

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                salir();
            }
        });

        this.setSize(1850, 1000);
        setLocationRelativeTo(null);
    }

    // Reemplaza el contenido vacio de un panel 
    private static void colocar(JPanel contenedor, JComponent contenido, Dimension tamano) {
        contenedor.removeAll();
        contenedor.setLayout(new BorderLayout());
        contenedor.add(contenido, BorderLayout.CENTER);
        contenedor.setPreferredSize(tamano);
    }

    //Todas las operaciones de archivos 
    private void configurarMenus() {
        jMenuItem1.addActionListener(e -> crearProyecto());
        jMenuItem2.addActionListener(e -> abrirProyecto());
        jMenuItem3.addActionListener(e -> crearCarpeta());
        jMenuItem4.addActionListener(e -> crearArchivo());
        jMenuItem5.addActionListener(e -> editor.guardarTodo());
        jMenuItem11.addActionListener(e -> arbolProyecto.eliminarSeleccionado());
        jMenuItem12.addActionListener(e -> arbolProyecto.descargarProyecto());

        // Generado: lo del ultimo programa compilado
        jMenuItem6.addActionListener(e -> mostrarCodigo(resultado ->
                new DialogoC3D(this, TraductorC3D.traducir(resultado.cuartetas()))));
        jMenuItem7.addActionListener(e -> mostrarCodigo(resultado -> new DialogoCuartetas(this, resultado.cuartetas())));
        jMenuItem8.addActionListener(e -> mostrarTablas(resultado ->
                new DialogoTablaSimbolos(this, resultado.tablas().simbolos())));
        jMenuItem9.addActionListener(e -> mostrarTablas(resultado ->
                new DialogoTablaTipos(this, resultado.tablas().tipos())));
        jMenuItem10.addActionListener(e -> new DialogoTablaCompatibilidad(this).setVisible(true));

        // grupos: proyecto | contenido | eliminar y descargar
        jMenu1.insertSeparator(5);
        jMenu1.insertSeparator(2);
    }

    private void conectarPaneles() {
        editor.alCompilar(this::compilar);
        arbolProyecto.alAbrirArchivo(this::abrirEnEditor);
        arbolProyecto.alEliminar(editor::cerrarEliminados);
        editor.alGuardar(ruta -> {
            if (arbolProyecto.getRaiz().isPresent() && ruta.startsWith(arbolProyecto.getRaiz().get())) {
                arbolProyecto.refrescar();
            }
        });
    }

    // Acciones de archivos 

    private void abrirEnEditor(Path archivo) {
        try {
            editor.abrir(archivo);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "No se pudo abrir " + archivo.getFileName() + ":\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void crearProyecto() {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("¿Dónde crear el proyecto?");
        selector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (selector.showDialog(this, "Seleccionar") != JFileChooser.APPROVE_OPTION) {
            return;
        }
        String nombre = JOptionPane.showInputDialog(this, "Nombre del proyecto:", "Crear proyecto",
                JOptionPane.PLAIN_MESSAGE);
        if (nombre == null || nombre.isBlank()) {
            return;
        }
        Path carpeta = selector.getSelectedFile().toPath().resolve(nombre.trim());
        if (Files.exists(carpeta)) {
            JOptionPane.showMessageDialog(this, "Ya existe una carpeta con ese nombre.", "Crear proyecto",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Files.createDirectories(carpeta);
            arbolProyecto.abrirProyecto(carpeta);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "No se pudo crear el proyecto:\n" + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirProyecto() {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Abrir proyecto (carpeta)");
        selector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (selector.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            arbolProyecto.abrirProyecto(selector.getSelectedFile().toPath());
        }
    }

    private void crearArchivo() {
        if (arbolProyecto.getRaiz().isPresent()) {
            arbolProyecto.crearArchivo();
        } else {
            editor.nuevaPestana();
        }
    }

    private void crearCarpeta() {
        if (arbolProyecto.getRaiz().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero abre o crea un proyecto.", "Crear carpeta",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        arbolProyecto.crearCarpeta();
    }

    // Compilacion

    // Los imports se buscan desde la carpeta del .pig, aunque el proyecto se haya abierto en una carpeta superior.
    private void compilar() {
        Optional<Path> raiz = arbolProyecto.getRaiz();
        Path programa = raiz.isPresent() ? elegirPrograma(raiz.get()) : archivoActual();
        if (programa == null) {
            return;
        }
        Path base = programa.getParent();
        try {
            ultimoResultado = Compilador.compilar(base, programa, this::leer);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "No se pudo leer el programa:\n" + e.getMessage(), "Compilar",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        errores.setErrores(ultimoResultado.errores());
        codigoRiscV.setCodigo(ultimoResultado.exitoso() ? TraductorRiscV.traducir(ultimoResultado.cuartetas()) : null);
    }

    // Los archivos abiertos se leen del editor, aunque no esten guardados
    private String leer(Path archivo) throws IOException {
        Optional<PestanaEditor> abierta = editor.buscar(archivo);
        return abierta.isPresent() ? abierta.get().getContenido() : GestorArchivos.leer(archivo);
    }

    private Path elegirPrograma(Path raiz) {
        List<Path> programas;
        try (Stream<Path> archivos = Files.walk(raiz)) {
            programas = archivos.filter(archivo -> archivo.toString().endsWith(".pig")).sorted().toList();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "No se pudo leer el proyecto:\n" + e.getMessage(), "Compilar",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (programas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El proyecto no tiene un archivo .pig (el programa principal).",
                    "Compilar", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        if (programas.size() == 1) {
            return programas.get(0);
        }
        Optional<Path> actual = editor.actual().map(PestanaEditor::getArchivo).filter(programas::contains);
        if (actual.isPresent()) {
            return actual.get();
        }
        Object[] opciones = programas.stream().map(archivo -> raiz.relativize(archivo)).toArray();
        Object elegido = JOptionPane.showInputDialog(this, "El proyecto tiene varios .pig, ¿cuál compilar?",
                "Compilar", JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
        return elegido == null ? null : raiz.resolve(elegido.toString());
    }

    // Archivo de la pestana actual si ya se guardo con una extension del IDE
    private Path archivoActual() {
        Optional<PestanaEditor> actual = editor.actual();
        if (actual.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Abre un proyecto o un archivo para compilar.", "Compilar",
                    JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        if (actual.get().getArchivo() == null || actual.get().getLenguaje().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Guarda el archivo con extensión .pig, .y o .z para compilarlo.",
                    "Compilar", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return actual.get().getArchivo();
    }

    // Codigo 3D y cuartetas: solo existen si el ultimo programa compilo sin errores
    private void mostrarCodigo(Function<ResultadoCompilacion, JDialog> crearDialogo) {
        if (sinCompilar()) {
            return;
        }
        if (!ultimoResultado.exitoso()) {
            JOptionPane.showMessageDialog(this, "El programa tiene errores: corrígelos y vuelve a compilar para "
                    + "generar el código.", "Código generado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        crearDialogo.apply(ultimoResultado).setVisible(true);
    }

    // Las tablas se llenan aunque el programa tenga errores
    private void mostrarTablas(Function<ResultadoCompilacion, JDialog> crearDialogo) {
        if (!sinCompilar()) {
            crearDialogo.apply(ultimoResultado).setVisible(true);
        }
    }

    private boolean sinCompilar() {
        if (ultimoResultado == null) {
            JOptionPane.showMessageDialog(this, "Primero compila el programa (botón Compilar).", "Sin compilar",
                    JOptionPane.INFORMATION_MESSAGE);
            return true;
        }
        return false;
    }

    private void salir() {
        if (editor.cerrarTodo()) {
            dispose();
            System.exit(0);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 279, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1071, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 615, Short.MAX_VALUE)
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jMenu1.setText("Archivo");

        jMenuItem1.setText("Crear proyecto");
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("Abrir proyecto");
        jMenu1.add(jMenuItem2);

        jMenuItem3.setText("Crear carpeta");
        jMenu1.add(jMenuItem3);

        jMenuItem4.setText("Crear archivo");
        jMenu1.add(jMenuItem4);

        jMenuItem5.setText("Guardar todo");
        jMenu1.add(jMenuItem5);

        jMenuItem11.setText("Eliminar seleccionado");
        jMenu1.add(jMenuItem11);

        jMenuItem12.setText("Descarga completa en .zip");
        jMenu1.add(jMenuItem12);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Generado");

        jMenuItem6.setText("Código 3D");
        jMenu2.add(jMenuItem6);

        jMenuItem7.setText("Cuartetas");
        jMenu2.add(jMenuItem7);

        jMenuItem8.setText("Tabla de símbolos");
        jMenu2.add(jMenuItem8);

        jMenuItem9.setText("Tabla de tipos");
        jMenu2.add(jMenuItem9);

        jMenuItem10.setText("Tabla de compatibilidad");
        jMenu2.add(jMenuItem10);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(20, 20, 20)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // @param args the command line arguments
    public static void main(String args[]) {
        // Set the Nimbus look and feel
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        // If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
        // For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FramePrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FramePrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FramePrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FramePrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        

        // Create and display the form
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FramePrincipal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    // End of variables declaration//GEN-END:variables
}
