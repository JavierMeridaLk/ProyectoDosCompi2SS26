package com.mycompany.proyectodoscompi2.analizador;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.proyectodoscompi2.cuartetas.Cuarteta;
import com.mycompany.proyectodoscompi2.errores.ErrorCompilacion;
import com.mycompany.proyectodoscompi2.tablas.Tablas;

// Resultado de compilar un programa
public record ResultadoCompilacion(ArchivoAnalizado principal, List<ArchivoAnalizado> importados, Tablas tablas,
        List<Cuarteta> cuartetas) {

    public List<ArchivoAnalizado> archivos() {
        List<ArchivoAnalizado> archivos = new ArrayList<>(importados);
        archivos.add(principal);
        return archivos;
    }

    // Errores de todos los archivos
    public List<ErrorCompilacion> errores() {
        List<ErrorCompilacion> errores = new ArrayList<>();
        archivos().forEach(archivo -> errores.addAll(archivo.errores()));
        return errores;
    }

    public boolean exitoso() {
        return errores().isEmpty();
    }
}
