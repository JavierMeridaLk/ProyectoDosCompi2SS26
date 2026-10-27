package com.mycompany.proyectodoscompi2.tablas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mycompany.proyectodoscompi2.tipos.Tipo;
import com.mycompany.proyectodoscompi2.tipos.TipoPrimitivo;

// Tabla de tipos, los primitivos, las estructuras de los .y y las clases de los .z.
public class TablaTipos {

    private final TablaHash<String, TipoDefinido> tipos = new TablaHash<>();
    private final List<TipoDefinido> enOrden = new ArrayList<>();

    public TablaTipos() {
        for (TipoPrimitivo primitivo : List.of(TipoPrimitivo.ENTERO, TipoPrimitivo.DECIMAL, TipoPrimitivo.CADENA,
                TipoPrimitivo.CARACTER, TipoPrimitivo.BOOLEANO, TipoPrimitivo.VOID)) {
            agregar(new TipoDefinido(Tipo.primitivo(primitivo).toString(), TipoDefinido.Categoria.PRIMITIVO, null,
                    null, null));
        }
    }

    // retorna false si ya existia un tipo con ese nombre
    public boolean agregar(TipoDefinido tipo) {
        if (tipos.contiene(tipo.getNombre())) {
            return false;
        }
        tipos.insertar(tipo.getNombre(), tipo);
        enOrden.add(tipo);
        return true;
    }

    public TipoDefinido buscar(String nombre) {
        return nombre == null ? null : tipos.obtener(nombre);
    }

    // Estructura o clase de un tipo objeto
    public TipoDefinido definicion(Tipo tipo) {
        return tipo != null && tipo.esObjeto() ? buscar(tipo.nombreClase()) : null;
    }

    // Un tipo existe si es primitivo o si su estructura/clase esta definida
    public boolean existe(Tipo tipo) {
        return tipo.esPrimitivo() || tipos.contiene(tipo.nombreClase());
    }

    public List<TipoDefinido> todos() {
        return enOrden;
    }

    // Herencia 

    public TipoDefinido padre(TipoDefinido clase) {
        return clase == null ? null : buscar(clase.getPadre());
    }

    // La clase y sus ancestros, de la clase hacia arriba 
    public List<TipoDefinido> linaje(TipoDefinido clase) {
        List<TipoDefinido> linaje = new ArrayList<>();
        Set<String> vistos = new HashSet<>();
        for (TipoDefinido actual = clase; actual != null && vistos.add(actual.getNombre()); actual = padre(actual)) {
            linaje.add(actual);
        }
        return linaje;
    }

    // La cadena de herencia vuelve a pasar por la misma clase
    public boolean tieneCiclo(TipoDefinido clase) {
        Set<String> vistos = new HashSet<>();
        for (TipoDefinido actual = clase; actual != null; actual = padre(actual)) {
            if (!vistos.add(actual.getNombre())) {
                return true;
            }
        }
        return false;
    }

    // hija es la misma clase que ancestro o hereda de ella
    public boolean esSubclase(String hija, String ancestro) {
        for (TipoDefinido clase : linaje(buscar(hija))) {
            if (clase.getNombre().equals(ancestro)) {
                return true;
            }
        }
        return false;
    }

    // Atributo propio o heredado
    public Campo buscarCampo(TipoDefinido tipo, String nombre) {
        for (TipoDefinido clase : linaje(tipo)) {
            Campo campo = clase.buscarCampoPropio(nombre);
            if (campo != null) {
                return campo;
            }
        }
        return null;
    }

    // Todos los atributos en el orden de memoria
    public List<Campo> camposCompletos(TipoDefinido tipo) {
        List<TipoDefinido> linaje = linaje(tipo);
        List<Campo> campos = new ArrayList<>();
        for (int i = linaje.size() - 1; i >= 0; i--) {
            campos.addAll(linaje.get(i).getCampos());
        }
        return campos;
    }

    // Las clases guardan en la celda 0 su tabla de metodos virtuales;
    public int primeraCelda(TipoDefinido tipo) {
        return tipo.esClase() ? 1 : 0;
    }

    // Celda del heap onde esta el atributo
    public int desplazamiento(TipoDefinido tipo, Campo campo) {
        return primeraCelda(tipo) + camposCompletos(tipo).indexOf(campo);
    }

    // Celdas que ocupa un objeto en el heap
    public int tamano(TipoDefinido tipo) {
        return tipo.getCategoria() == TipoDefinido.Categoria.PRIMITIVO ? 1
                : primeraCelda(tipo) + camposCompletos(tipo).size();
    }
}
