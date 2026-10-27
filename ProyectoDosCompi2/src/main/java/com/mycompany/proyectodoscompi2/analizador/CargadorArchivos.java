package com.mycompany.proyectodoscompi2.analizador;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Lee y analiza los archivos que le piden el visitor de Pig Latin valida cada import 
public class CargadorArchivos {

    // Lee el contenido de un archivo
    public interface LectorArchivos {
        String leer(Path archivo) throws IOException;

        default boolean existe(Path archivo) {
            return Files.isRegularFile(archivo);
        }
    }

    private final Path raiz;
    private final LectorArchivos lector;
    private final Map<Path, ArchivoAnalizado> cargados = new LinkedHashMap<>();

    public CargadorArchivos(Path raiz, LectorArchivos lector) {
        this.raiz = raiz.toAbsolutePath().normalize();
        this.lector = lector;
    }

    // Ruta de un import
    public Path resolver(List<String> partes) {
        Path archivo = raiz;
        for (int i = 0; i < partes.size() - 2; i++) {
            archivo = archivo.resolve(partes.get(i));
        }
        return archivo.resolve(partes.get(partes.size() - 2) + "." + partes.get(partes.size() - 1)).normalize();
    }

    public boolean existe(Path archivo) {
        return lector.existe(archivo);
    }

    public boolean yaCargado(Path archivo) {
        return cargados.containsKey(archivo.toAbsolutePath().normalize());
    }

    // Analiza el archivo 
    public ArchivoAnalizado cargar(Path archivo, Lenguaje lenguaje) throws IOException {
        Path ruta = archivo.toAbsolutePath().normalize();
        ArchivoAnalizado analizado = cargados.get(ruta);
        if (analizado == null) {
            analizado = Analizador.analizar(lector.leer(ruta), lenguaje, nombreVisible(ruta), ruta);
            cargados.put(ruta, analizado);
        }
        return analizado;
    }

    // Nombre que se muestra en la tabla de errores
    public String nombreVisible(Path archivo) {
        Path relativa = archivo.startsWith(raiz) ? raiz.relativize(archivo) : archivo.getFileName();
        return relativa.toString().replace('\\', '/');
    }
}
