package com.mycompany.proyectodoscompi2.visitors.piglatin;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.mycompany.pigLatinBaseVisitor;
import com.mycompany.pigLatinParser;
import com.mycompany.proyectodoscompi2.analizador.ArchivoAnalizado;
import com.mycompany.proyectodoscompi2.analizador.CargadorArchivos;
import com.mycompany.proyectodoscompi2.analizador.Lenguaje;
import com.mycompany.proyectodoscompi2.errores.ManejadorErrores;
import com.mycompany.proyectodoscompi2.errores.TipoError;

// Primer recorrido del .pig: valida cada import y le pide al cargador que lea el archivo los archivos importados quedan en getImportados() para registrar sus declaraciones.
public class DeclaracionesPigLatin extends pigLatinBaseVisitor<Void> {

    private final CargadorArchivos cargador;
    private final ManejadorErrores errores;
    private final List<ArchivoAnalizado> importados = new ArrayList<>();

    public DeclaracionesPigLatin(CargadorArchivos cargador, ManejadorErrores errores) {
        this.cargador = cargador;
        this.errores = errores;
    }

    public List<ArchivoAnalizado> getImportados() {
        return importados;
    }

    private void error(pigLatinParser.ImportacionContext contexto, String lexema, String descripcion) {
        errores.agregar(TipoError.SEMANTICO, contexto.getStart().getLine(),
                contexto.getStart().getCharPositionInLine() + 1, lexema, descripcion);
    }

    @Override
    public Void visitPrograma(pigLatinParser.ProgramaContext contexto) {
        // aunque el programa tenga errores de sintaxis se revisan los import bien escritos
        for (pigLatinParser.ImportacionContext importacion : contexto.importacion()) {
            if (importacion.exception == null && importacion.rutaImportacion() != null
                    && importacion.rutaImportacion().exception == null) {
                visit(importacion);
            }
        }
        return null;
    }

    @Override
    public Void visitImportacion(pigLatinParser.ImportacionContext contexto) {
        List<String> partes = new ArrayList<>();
        for (TerminalNode parte : contexto.rutaImportacion().IDENTIFICADOR()) {
            partes.add(parte.getText());
        }
        String texto = String.join(".", partes);
        String extension = partes.get(partes.size() - 1);
        if (!extension.equals("y") && !extension.equals("z")) {
            error(contexto, texto, "Solo se pueden importar archivos .y o .z (se encontró '." + extension + "')");
            return null;
        }
        Path archivo = cargador.resolver(partes);
        String nombre = cargador.nombreVisible(archivo);
        if (cargador.yaCargado(archivo)) {
            error(contexto, texto, "El archivo '" + nombre + "' ya fue importado");
            return null;
        }
        if (!cargador.existe(archivo)) {
            error(contexto, texto, "No se encontró el archivo importado '" + nombre + "'");
            return null;
        }
        try {
            importados.add(cargador.cargar(archivo, extension.equals("y") ? Lenguaje.Y : Lenguaje.ZETARIANO));
        } catch (IOException e) {
            error(contexto, texto, "No se pudo leer el archivo importado '" + nombre + "': " + e.getMessage());
        }
        return null;
    }
}
