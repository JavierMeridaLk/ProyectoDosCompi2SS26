package com.mycompany.proyectodoscompi2.analizador;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.mycompany.proyectodoscompi2.cuartetas.ListaCuartetas;
import com.mycompany.proyectodoscompi2.tablas.Tablas;
import com.mycompany.proyectodoscompi2.visitors.piglatin.DeclaracionesPigLatin;
import com.mycompany.proyectodoscompi2.visitors.piglatin.TraductorPigLatin;
import com.mycompany.proyectodoscompi2.visitors.y.DeclaracionesY;
import com.mycompany.proyectodoscompi2.visitors.y.TraductorY;
import com.mycompany.proyectodoscompi2.visitors.zetariano.DeclaracionesZetariano;
import com.mycompany.proyectodoscompi2.visitors.zetariano.TraductorZetariano;

//Ordena los dos recorridos de todos los archivos del programa:
//Declaraciones
//Traduccion cada archivo hace su analisis semantico y sus cuartetas
//El segundo recorrido empieza cuando todos registraron lo suyo, asi una clase puede usar a otra aunque este en otro archivo
public final class Compilador {

    private Compilador() {
    }

    // el pig principal o un .y/.z suelto
    public static ResultadoCompilacion compilar(Path raiz, Path archivo, CargadorArchivos.LectorArchivos lector)
            throws IOException {
        CargadorArchivos cargador = new CargadorArchivos(raiz, lector);
        Lenguaje lenguaje = Lenguaje.desdeArchivo(archivo.getFileName().toString())
                .orElseThrow(() -> new IllegalArgumentException("Extensión no soportada: " + archivo.getFileName()));
        ArchivoAnalizado principal = cargador.cargar(archivo, lenguaje);
        Tablas tablas = new Tablas();

        //Declaraciones
        List<ArchivoAnalizado> importados = List.of();
        if (lenguaje == Lenguaje.PIG_LATIN) {
            DeclaracionesPigLatin declaraciones = new DeclaracionesPigLatin(cargador, principal.manejador());
            declaraciones.visit(principal.arbol());
            importados = declaraciones.getImportados();
            importados.forEach(importado -> declarar(importado, tablas));
        } else {
            declarar(principal, tablas);
        }

        //Traduccion
        ListaCuartetas codigo = new ListaCuartetas();
        importados.forEach(importado -> traducir(importado, tablas, codigo));
        traducir(principal, tablas, codigo);

        ResultadoCompilacion resultado = new ResultadoCompilacion(principal, importados, tablas, codigo.getCuartetas());
        return resultado.exitoso() ? resultado
                : new ResultadoCompilacion(principal, importados, tablas, List.of());
    }

    private static void declarar(ArchivoAnalizado archivo, Tablas tablas) {
        if (!archivo.sintaxisCorrecta()) {
            return;
        }
        switch (archivo.lenguaje()) {
            case Y -> new DeclaracionesY(tablas, archivo.manejador()).visit(archivo.arbol());
            case ZETARIANO -> new DeclaracionesZetariano(tablas, archivo.manejador()).visit(archivo.arbol());
            default -> {
            }
        }
    }

    private static void traducir(ArchivoAnalizado archivo, Tablas tablas, ListaCuartetas codigo) {
        if (!archivo.sintaxisCorrecta()) {
            return;
        }
        switch (archivo.lenguaje()) {
            case Y -> new TraductorY(tablas, codigo, archivo.manejador()).visit(archivo.arbol());
            case ZETARIANO -> new TraductorZetariano(tablas, codigo, archivo.manejador()).visit(archivo.arbol());
            case PIG_LATIN -> new TraductorPigLatin(tablas, codigo, archivo.manejador()).visit(archivo.arbol());
        }
    }
}
