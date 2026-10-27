package com.mycompany.proyectodoscompi2.cuartetas;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

// Las cuartetas de todo el programa y los contadores de temporales y etiquetas
public class ListaCuartetas {

    // Tamano del marco de la funcion actual, mientras no se conoce
    public static final String MARCO = "@marco";

    private final List<Cuarteta> cuartetas = new ArrayList<>();
    private int temporales;
    private int etiquetas;

    public void agregar(String operador, String argumento1, String argumento2, String resultado) {
        cuartetas.add(new Cuarteta(operador, argumento1, argumento2, resultado));
    }

    public String nuevoTemporal() {
        return "t" + (++temporales);
    }

    public String nuevaEtiqueta() {
        return "L" + (++etiquetas);
    }

    // Posicion donde se agregara la siguiente cuarteta
    public int tamano() {
        return cuartetas.size();
    }

    // Completa el tamano del marco de una funcion, que solo se conoce al terminarla 
    public void completarMarco(int desde, int marco) {
        for (int i = desde; i < cuartetas.size(); i++) {
            Cuarteta c = cuartetas.get(i);
            cuartetas.set(i, new Cuarteta(c.operador(), conMarco(c.argumento1(), marco),
                    conMarco(c.argumento2(), marco), conMarco(c.resultado(), marco)));
        }
    }

    private static String conMarco(String operando, int marco) {
        if (operando.equals(MARCO)) {
            return String.valueOf(marco);
        }
        if (operando.startsWith(MARCO + "+")) {
            return String.valueOf(marco + Integer.parseInt(operando.substring(MARCO.length() + 1)));
        }
        return operando;
    }

    // Cambia una cuarteta ya emitida 
    public void reemplazar(int indice, Cuarteta cuarteta) {
        cuartetas.set(indice, cuarteta);
    }

    // Temporales distintos que se usan desde la posicion indicada
    public Set<String> temporalesDesde(int desde) {
        Set<String> usados = new LinkedHashSet<>();
        for (int i = desde; i < cuartetas.size(); i++) {
            Cuarteta c = cuartetas.get(i);
            for (String operando : new String[] {c.argumento1(), c.argumento2(), c.resultado()}) {
                if (operando.matches("t\\d+")) {
                    usados.add(operando);
                }
            }
        }
        return usados;
    }

    public List<Cuarteta> getCuartetas() {
        return cuartetas;
    }
}
