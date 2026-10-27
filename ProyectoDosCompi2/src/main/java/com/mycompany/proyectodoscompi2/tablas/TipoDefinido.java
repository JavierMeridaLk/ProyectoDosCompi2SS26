package com.mycompany.proyectodoscompi2.tablas;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.proyectodoscompi2.tipos.Posicion;

// Una entrada de la tabla de tipos: un primitivo, una estructura de Y? o una clase de Zetariano
public class TipoDefinido {

    public enum Categoria {
        PRIMITIVO,
        ESTRUCTURA,
        CLASE
    }

    private final String nombre;
    private final Categoria categoria;
    private final String archivo;
    private final Posicion posicion;
    private final String padre;
    private final List<Campo> campos = new ArrayList<>();

    public TipoDefinido(String nombre, Categoria categoria, String archivo, Posicion posicion, String padre) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.archivo = archivo;
        this.posicion = posicion;
        this.padre = padre;
    }

    // retorna false si ya habia un atributo propio con ese nombre
    public boolean agregarCampo(Campo campo) {
        if (buscarCampoPropio(campo.nombre()) != null) {
            return false;
        }
        campos.add(campo);
        return true;
    }

    public Campo buscarCampoPropio(String nombre) {
        for (Campo campo : campos) {
            if (campo.nombre().equals(nombre)) {
                return campo;
            }
        }
        return null;
    }

    public String getNombre() {
        return nombre;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public boolean esClase() {
        return categoria == Categoria.CLASE;
    }

    public String getArchivo() {
        return archivo;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public String getPadre() {
        return padre;
    }

    public List<Campo> getCampos() {
        return campos;
    }
}
