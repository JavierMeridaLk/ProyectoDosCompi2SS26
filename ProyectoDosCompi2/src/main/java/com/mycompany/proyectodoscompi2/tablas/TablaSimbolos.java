package com.mycompany.proyectodoscompi2.tablas;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Tabla de simbolos de todo el programa
public class TablaSimbolos {

    private final Ambito global = new Ambito("global", null, true);
    private final List<Ambito> ambitos = new ArrayList<>(List.of(global));
    private Ambito actual = global;
    private final TablaHash<String, Simbolo> funciones = new TablaHash<>();
    private final TablaHash<String, List<Simbolo>> metodos = new TablaHash<>();
    private final TablaHash<String, List<Simbolo>> constructores = new TablaHash<>();

    //Ambitos 

    public void entrarAmbito(String nombre, boolean esMarco) {
        actual = new Ambito(nombre, actual, esMarco);
        ambitos.add(actual);
    }

    public void salirAmbito() {
        if (actual.getPadre() != null) {
            actual = actual.getPadre();
        }
    }

    public Ambito getActual() {
        return actual;
    }

    // Busca una variable desde el ambito actual hacia afuera
    public Simbolo buscarVariable(String nombre) {
        for (Ambito ambito = actual; ambito != null; ambito = ambito.getPadre()) {
            Simbolo simbolo = ambito.buscar(nombre);
            if (simbolo != null) {
                return simbolo;
            }
            if (ambito.esMarco()) {
                break;
            }
        }
        return null;
    }

    // Funciones, metodos y constructores 

    public Simbolo buscarFuncion(String nombre) {
        return funciones.obtener(nombre);
    }

    public boolean declararFuncion(Simbolo funcion) {
        if (funciones.contiene(funcion.nombre())) {
            return false;
        }
        funciones.insertar(funcion.nombre(), funcion);
        return true;
    }

    // Metodos propios de la clase con ese nombre 
    public List<Simbolo> metodosPropios(String clase, String nombre) {
        List<Simbolo> sobrecargas = metodos.obtener(clase + "." + nombre);
        return sobrecargas != null ? sobrecargas : List.of();
    }

    // Todos los metodos propios de una clase, en el orden en que se declararon
    public List<Simbolo> metodosDe(String clase) {
        List<Simbolo> lista = new ArrayList<>();
        for (List<Simbolo> sobrecargas : metodos.valores()) {
            for (Simbolo metodo : sobrecargas) {
                if (metodo.clase().equals(clase)) {
                    lista.add(metodo);
                }
            }
        }
        lista.sort(Comparator.comparingInt((Simbolo s) -> s.posicion().linea())
                .thenComparingInt(s -> s.posicion().columna()));
        return lista;
    }

    public List<Simbolo> constructoresDe(String clase) {
        List<Simbolo> sobrecargas = constructores.obtener(clase);
        return sobrecargas != null ? sobrecargas : List.of();
    }

    // retorna false si la clase ya tiene un metodo con la misma firma
    public boolean declararMetodo(Simbolo metodo) {
        return agregarSobrecarga(metodos, metodo.clase() + "." + metodo.nombre(), metodo);
    }

    // retorna false si la clase ya tiene un constructor con los mismos tipos de parametros
    public boolean declararConstructor(Simbolo constructor) {
        return agregarSobrecarga(constructores, constructor.clase(), constructor);
    }

    private static boolean agregarSobrecarga(TablaHash<String, List<Simbolo>> tabla, String clave, Simbolo simbolo) {
        List<Simbolo> sobrecargas = tabla.obtener(clave);
        if (sobrecargas == null) {
            sobrecargas = new ArrayList<>();
            tabla.insertar(clave, sobrecargas);
        }
        for (Simbolo existente : sobrecargas) {
            if (existente.parametros().equals(simbolo.parametros())) {
                return false;
            }
        }
        sobrecargas.add(simbolo);
        return true;
    }

    // Todo lo registrado
    public List<Simbolo> todos() {
        Comparator<Simbolo> porPosicion = Comparator.comparing(Simbolo::archivo)
                .thenComparingInt(s -> s.posicion().linea())
                .thenComparingInt(s -> s.posicion().columna());
        List<Simbolo> resultado = new ArrayList<>(funciones.valores());
        constructores.valores().forEach(resultado::addAll);
        metodos.valores().forEach(resultado::addAll);
        resultado.sort(porPosicion);
        for (Ambito ambito : ambitos) {
            List<Simbolo> simbolos = new ArrayList<>(ambito.getSimbolos());
            simbolos.sort(porPosicion);
            resultado.addAll(simbolos);
        }
        return resultado;
    }
}
