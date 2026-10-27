package com.mycompany.proyectodoscompi2.tablas;

import java.util.ArrayList;
import java.util.List;

// Tablas de todo el programa, solo hay consultas que combinan la tabla de tipos  con la de simbolos
public record Tablas(TablaTipos tipos, TablaSimbolos simbolos) {

    public Tablas() {
        this(new TablaTipos(), new TablaSimbolos());
    }

    // Metodos con ese nombre visibles desde la clase
    public List<Simbolo> buscarMetodos(String clase, String nombre) {
        List<Simbolo> encontrados = new ArrayList<>();
        boolean propia = true;
        for (TipoDefinido actual : tipos.linaje(tipos.buscar(clase))) {
            for (Simbolo metodo : simbolos.metodosPropios(actual.getNombre(), nombre)) {
                boolean tapado = encontrados.stream().anyMatch(metodo::mismaFirma);
                if (!tapado && (propia || metodo.visibilidad().seHereda())) {
                    encontrados.add(metodo);
                }
            }
            propia = false;
        }
        return encontrados;
    }

    // Metodo heredable de un ancestro con la misma firma
    public Simbolo metodoSobrescrito(TipoDefinido clase, Simbolo metodo) {
        List<TipoDefinido> linaje = tipos.linaje(clase);
        for (int i = 1; i < linaje.size(); i++) {
            for (Simbolo heredado : simbolos.metodosPropios(linaje.get(i).getNombre(), metodo.nombre())) {
                if (heredado.mismaFirma(metodo) && heredado.visibilidad().seHereda()) {
                    return heredado;
                }
            }
        }
        return null;
    }

    // Tabla de metodos virtuales, cda sobrescritura ocupa la posicion del metodo del padre, asi la llamada no depende de la clase real.
    public List<Simbolo> vtable(TipoDefinido clase) {
        List<TipoDefinido> linaje = tipos.linaje(clase);
        List<Simbolo> tabla = new ArrayList<>();
        for (int i = linaje.size() - 1; i >= 0; i--) {
            for (Simbolo metodo : simbolos.metodosDe(linaje.get(i).getNombre())) {
                if (!metodo.visibilidad().seHereda()) {
                    continue;
                }
                int posicion = posicionEn(tabla, metodo);
                if (posicion >= 0) {
                    tabla.set(posicion, metodo);
                } else {
                    tabla.add(metodo);
                }
            }
        }
        return tabla;
    }

    // Posicion de la firma del metodo en la vtable
    public static int posicionEn(List<Simbolo> vtable, Simbolo metodo) {
        for (int i = 0; i < vtable.size(); i++) {
            if (vtable.get(i).mismaFirma(metodo)) {
                return i;
            }
        }
        return -1;
    }
}
