package com.mycompany.proyectodoscompi2.visitors;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

import com.mycompany.proyectodoscompi2.analizador.Lenguaje;
import com.mycompany.proyectodoscompi2.cuartetas.Direccion;
import com.mycompany.proyectodoscompi2.cuartetas.ListaCuartetas;
import com.mycompany.proyectodoscompi2.cuartetas.Valor;
import com.mycompany.proyectodoscompi2.errores.ManejadorErrores;
import com.mycompany.proyectodoscompi2.errores.TipoError;
import com.mycompany.proyectodoscompi2.tablas.Ambito;
import com.mycompany.proyectodoscompi2.tablas.Campo;
import com.mycompany.proyectodoscompi2.tablas.CategoriaSimbolo;
import com.mycompany.proyectodoscompi2.tablas.Simbolo;
import com.mycompany.proyectodoscompi2.tablas.TablaCompatibilidad;
import com.mycompany.proyectodoscompi2.tablas.Tablas;
import com.mycompany.proyectodoscompi2.tablas.TipoDefinido;
import com.mycompany.proyectodoscompi2.tipos.OperadorBinario;
import com.mycompany.proyectodoscompi2.tipos.OperadorUnario;
import com.mycompany.proyectodoscompi2.tipos.Posicion;
import com.mycompany.proyectodoscompi2.tipos.Tipo;
import com.mycompany.proyectodoscompi2.tipos.TipoPrimitivo;
import com.mycompany.proyectodoscompi2.tipos.Visibilidad;

// Base de los visitors que traducen (TraductorZetariano, TraductorPigLatin, TraductorY).
// Cada visitor recorre su arbol una sola vez: en cada nodo valida la semantica con estas reglas y
// en el mismo paso emite sus cuartetas. Si hay un error se reporta y se devuelve un valor de tipo
// ERROR para no reportar errores en cascada; el codigo de un programa con errores se descarta.
public abstract class TraductorBase extends AbstractParseTreeVisitor<Valor> {

    protected static final Tipo ENTERO = Tipo.ENTERO;
    protected static final Tipo DECIMAL = Tipo.DECIMAL;
    protected static final Tipo CADENA = Tipo.CADENA;
    protected static final Tipo CARACTER = Tipo.CARACTER;
    protected static final Tipo BOOLEANO = Tipo.BOOLEANO;
    protected static final Tipo VOID = Tipo.VOID;
    protected static final String MARCO = ListaCuartetas.MARCO;

    protected final Tablas tablas;
    protected final ListaCuartetas codigo;
    protected final ManejadorErrores errores;
    protected final Lenguaje lenguaje;

    // Clase que se esta traduciendo (null en Pig Latin y Y?): decide que atributos y metodos son visibles
    protected TipoDefinido claseActual;

    private Tipo retorno;               // tipo de retorno de la funcion actual (null fuera de funciones)
    private String etiquetaFin;         // donde salta un return
    private int inicioFuncion;          // primera cuarteta de la funcion actual
    private final Deque<String[]> saltos = new ArrayDeque<>();  // {salida, continuar} de ciclos y selecciones

    protected TraductorBase(Tablas tablas, ListaCuartetas codigo, ManejadorErrores errores, Lenguaje lenguaje) {
        this.tablas = tablas;
        this.codigo = codigo;
        this.errores = errores;
        this.lenguaje = lenguaje;
    }

    // ==================================================================
    //                          Errores y tipos
    // ==================================================================

    protected void error(ParserRuleContext contexto, String lexema, String descripcion) {
        error(contexto.getStart(), lexema, descripcion);
    }

    protected void error(Token token, String lexema, String descripcion) {
        errores.agregar(TipoError.SEMANTICO, token.getLine(), token.getCharPositionInLine() + 1, lexema, descripcion);
    }

    // Nombre del tipo con las palabras del lenguaje
    protected String nombre(Tipo tipo) {
        return lenguaje.nombreDe(tipo);
    }

    // (entero, cadena)
    protected String describir(List<Valor> valores) {
        List<String> nombres = new ArrayList<>();
        valores.forEach(valor -> nombres.add(nombre(valor.tipo())));
        return "(" + String.join(", ", nombres) + ")";
    }

    // El tipo si esta definido; si es una estructura/clase que no existe se reporta y queda como ERROR
    protected Tipo conocido(Tipo tipo, Token token) {
        if (tipo.esError() || tablas.tipos().existe(tipo)) {
            return tipo;
        }
        error(token, tipo.nombreClase(), "El tipo '" + tipo.nombreClase() + "' no está definido");
        return Tipo.ERROR;
    }

    // Un texto leido participa en las operaciones como cadena
    private static Tipo comoValor(Tipo tipo) {
        return tipo.primitivo() == TipoPrimitivo.LEIDO ? CADENA : tipo;
    }

    // Se puede guardar un valor de tipo origen donde se espera uno de tipo destino.
    // Las clases aceptan objetos de sus subclases y null; los arreglos deben ser del mismo tipo.
    protected boolean esAsignable(Tipo destino, Tipo origen) {
        if (destino.esError() || origen.esError() || destino.equals(origen)) {
            return true;
        }
        if (origen.primitivo() == TipoPrimitivo.LEIDO) {
            return destino.esSimple() && destino.primitivo() != TipoPrimitivo.VOID
                    && destino.primitivo() != TipoPrimitivo.NULO;
        }
        if (origen.primitivo() == TipoPrimitivo.NULO) {
            return !destino.esPrimitivo() || destino.dimensiones() > 0;
        }
        if (destino.dimensiones() > 0 || origen.dimensiones() > 0) {
            return false;
        }
        if (destino.esPrimitivo() && origen.esPrimitivo()) {
            return TablaCompatibilidad.esAsignable(destino.primitivo(), origen.primitivo());
        }
        if (!destino.esPrimitivo() && !origen.esPrimitivo()) {
            return tablas.tipos().esSubclase(origen.nombreClase(), destino.nombreClase());
        }
        return false;
    }

    // Reporta si el valor no se puede guardar en el destino
    protected boolean verificarAsignable(Tipo destino, Valor valor, ParserRuleContext contexto) {
        if (esAsignable(destino, valor.tipo())) {
            return true;
        }
        error(contexto, null, "No se puede asignar un valor de tipo '" + nombre(valor.tipo()) + "' a uno de tipo '"
                + nombre(destino) + "'");
        return false;
    }

    protected boolean verificarCondicion(Valor condicion, ParserRuleContext contexto) {
        if (condicion.esError() || condicion.tipo().equals(BOOLEANO)) {
            return true;
        }
        error(contexto, null, "La condición debe ser de tipo booleano (se encontró '" + nombre(condicion.tipo()) + "')");
        return false;
    }

    protected boolean verificarEntero(Valor valor, ParserRuleContext contexto, String que) {
        if (valor.esError() || (valor.tipo().esSimple() && TablaCompatibilidad.esEntero(valor.tipo().primitivo()))) {
            return true;
        }
        error(contexto, null, que + " debe ser de tipo entero (se encontró '" + nombre(valor.tipo()) + "')");
        return false;
    }

    // ==================================================================
    //                          Visibilidad
    // ==================================================================

    // Un miembro declarado en la clase duenia se puede usar desde la clase actual.
    // No hay paquetes: default es como private y protected es para la clase y sus hijas.
    protected boolean accesible(Visibilidad visibilidad, String duenia) {
        TipoDefinido tipo = tablas.tipos().buscar(duenia);
        if (tipo == null || !tipo.esClase()) {
            return true;    // los atributos de las estructuras de Y? son publicos
        }
        return switch (visibilidad) {
            case PUBLIC -> true;
            case PROTECTED -> claseActual != null && tablas.tipos().esSubclase(claseActual.getNombre(), duenia);
            default -> claseActual != null && claseActual.getNombre().equals(duenia);
        };
    }

    private String desde() {
        return claseActual == null ? "fuera de una clase" : "desde la clase '" + claseActual.getNombre() + "'";
    }

    // ==================================================================
    //                          Ambitos y variables
    // ==================================================================

    protected void entrarMarco(String nombre) {
        tablas.simbolos().entrarAmbito(nombre, true);
    }

    protected void entrarBloque(String nombre) {
        tablas.simbolos().entrarAmbito(tablas.simbolos().getActual().getNombre() + "/" + nombre, false);
    }

    protected void salirAmbito() {
        tablas.simbolos().salirAmbito();
    }

    // Declara una variable o parametro en el ambito actual y le reserva una celda del marco
    protected Simbolo declararVariable(Token nombre, Tipo tipo, CategoriaSimbolo categoria) {
        Ambito ambito = tablas.simbolos().getActual();
        Simbolo existente = ambito.buscar(nombre.getText());
        if (existente != null) {
            error(nombre, nombre.getText(), "'" + nombre.getText() + "' ya fue declarada en este ámbito");
            return existente;
        }
        Simbolo simbolo = Simbolo.variable(nombre.getText(), categoria, tipo, ambito.getNombre(), errores.getArchivo(),
                Posicion.de(nombre), ambito.reservar());
        ambito.declarar(simbolo);
        return simbolo;
    }

    protected Simbolo buscarVariable(String nombre) {
        return tablas.simbolos().buscarVariable(nombre);
    }

    protected Direccion direccionDe(Simbolo variable) {
        return new Direccion("P", variable.desplazamiento(), false, variable.tipo());
    }

    // ==================================================================
    //                          Emision
    // ==================================================================

    protected void emitir(String operador, String argumento1, String argumento2, String resultado) {
        codigo.agregar(operador, argumento1, argumento2, resultado);
    }

    protected String temporal() {
        return codigo.nuevoTemporal();
    }

    protected String etiqueta() {
        return codigo.nuevaEtiqueta();
    }

    protected void colocar(String etiqueta) {
        emitir("label", "", "", etiqueta);
    }

    protected void saltar(String etiqueta) {
        emitir("goto", "", "", etiqueta);
    }

    // Operacion que deja su resultado en un temporal
    protected Valor operar(String operador, String argumento1, String argumento2, Tipo tipo) {
        String resultado = temporal();
        emitir(operador, argumento1, argumento2, resultado);
        return new Valor(resultado, tipo);
    }

    // t = memoria[base + desplazamiento]
    private Valor leerMemoria(String memoria, String base, String desplazamiento, Tipo tipo) {
        return operar("leer_" + memoria, base, desplazamiento, tipo);
    }

    // memoria[base + desplazamiento] = valor
    private void escribirMemoria(String memoria, String base, String desplazamiento, Valor valor) {
        emitir("escribir_" + memoria, valor.direccion(), desplazamiento, base);
    }

    // H = H + celdas o P = P +/- celdas
    private void actualizar(String registro, String operador, String cantidad) {
        String nuevo = operar(operador, registro, cantidad, ENTERO).direccion();
        emitir("=", nuevo, "", registro);
    }

    // ==================================================================
    //                          Funciones
    // ==================================================================

    // Marco de una funcion (P es el frame pointer: apunta a su primera celda):
    //   stack[P + 0]  valor de retorno
    //   stack[P + 1]  this (constructores y metodos) o el primer parametro
    //   ...           parametros, variables locales y temporales
    // El tamano del marco se completa en finFuncion
    protected void inicioFuncion(String etiqueta, Tipo tipoRetorno) {
        retorno = tipoRetorno;
        etiquetaFin = etiqueta();
        inicioFuncion = codigo.tamano();
        tablas.simbolos().getActual().reservar();   // celda 0: valor de retorno
        emitir("func", MARCO, "", etiqueta);
    }

    // Se llama todavia dentro del marco de la funcion: el marco son sus variables mas sus temporales
    protected void finFuncion(boolean esPrograma) {
        colocar(etiquetaFin);
        if (esPrograma) {
            emitir("halt", "", "", "");
        }
        emitir("end", "", "", "");
        int marco = tablas.simbolos().getActual().getTamanoMarco() + codigo.temporalesDesde(inicioFuncion).size();
        codigo.completarMarco(inicioFuncion, marco);
        retorno = null;
    }

    // return / retornar con o sin valor
    protected void retornar(Valor valor, ParserRuleContext contexto) {
        if (retorno == null) {
            error(contexto, null, "Solo se puede retornar dentro de una función o método");
            return;
        }
        boolean sinTipo = retorno.equals(VOID);
        if (valor == null) {
            if (!sinTipo && !retorno.esError()) {
                error(contexto, null, "Se debe retornar un valor de tipo '" + nombre(retorno) + "'");
            }
        } else if (sinTipo) {
            error(contexto, null, "No se puede retornar un valor en una función o método sin tipo de retorno");
        } else if (!valor.esError() && !esAsignable(retorno, valor.tipo())) {
            error(contexto, null, "Se debe retornar un valor de tipo '" + nombre(retorno) + "' (se encontró '"
                    + nombre(valor.tipo()) + "')");
        } else if (!valor.esError()) {
            escribirMemoria("stack", "P", "0", convertir(valor, retorno));
        }
        saltar(etiquetaFin);
    }

    // ==================================================================
    //                          Ciclos y selecciones
    // ==================================================================

    protected void enCiclo(String salida, String continuar, Runnable cuerpo) {
        saltos.push(new String[] {salida, continuar});
        cuerpo.run();
        saltos.pop();
    }

    // En un switch/elegir el continuar sigue siendo el del ciclo que lo contiene
    protected void enSeleccion(String salida, Runnable cuerpo) {
        saltos.push(new String[] {salida, saltos.isEmpty() ? null : saltos.peek()[1]});
        cuerpo.run();
        saltos.pop();
    }

    // break, romper, interrumpe. donde: "un ciclo o un switch"
    protected void romper(ParserRuleContext contexto, String palabra, String donde) {
        if (saltos.isEmpty()) {
            error(contexto, palabra, "'" + palabra + "' solo se puede usar dentro de " + donde);
            return;
        }
        saltar(saltos.peek()[0]);
    }

    // continue, continuar, perge
    protected void continuar(ParserRuleContext contexto, String palabra) {
        if (saltos.isEmpty() || saltos.peek()[1] == null) {
            error(contexto, palabra, "'" + palabra + "' solo se puede usar dentro de un ciclo");
            return;
        }
        saltar(saltos.peek()[1]);
    }

    // ==================================================================
    //                          Condiciones
    // ==================================================================

    protected static boolean esRelacional(OperadorBinario operador) {
        return switch (operador) {
            case IGUAL_QUE, DIFERENTE_DE, MENOR_QUE, MENOR_O_IGUAL_QUE, MAYOR_QUE, MAYOR_O_IGUAL_QUE -> true;
            default -> false;
        };
    }

    // if condicion == 1 goto verdadero; goto falso
    protected void saltarSegun(Valor condicion, String verdadero, String falso) {
        emitir("if ==", condicion.direccion(), "1", verdadero);
        saltar(falso);
    }

    // a op b como salto: if a op b goto verdadero; goto falso (sin falso sigue con la siguiente cuarteta)
    protected void comparar(OperadorBinario operador, Valor izquierda, Valor derecha, String verdadero, String falso,
            ParserRuleContext contexto) {
        Tipo tipo = tipoBinario(operador, izquierda.tipo(), derecha.tipo(), contexto);
        if (tipo.esError()) {
            if (falso != null) {
                saltar(falso);
            }
            return;
        }
        if (esCadena(izquierda) && esCadena(derecha)) {
            Valor iguales = cadenasIguales(izquierda, derecha);
            emitir("if ==", iguales.direccion(), operador == OperadorBinario.IGUAL_QUE ? "1" : "0", verdadero);
        } else {
            Valor[] operandos = promover(izquierda, derecha);
            emitir("if " + operador.getSimbolo(), operandos[0].direccion(), operandos[1].direccion(), verdadero);
        }
        if (falso != null) {
            saltar(falso);
        }
    }

    // Valor (1 o 0) de una condicion que se genera con saltos (&&, ||)
    protected Valor valorDeCondicion(BiConsumer<String, String> condicion) {
        String resultado = temporal();
        String verdadero = etiqueta();
        String falso = etiqueta();
        String fin = etiqueta();
        condicion.accept(verdadero, falso);
        colocar(verdadero);
        emitir("=", "1", "", resultado);
        saltar(fin);
        colocar(falso);
        emitir("=", "0", "", resultado);
        colocar(fin);
        return new Valor(resultado, BOOLEANO);
    }

    // ==================================================================
    //                          Operaciones
    // ==================================================================

    // Tipo del resultado, o ERROR (reportado) si la operacion no es valida
    protected Tipo tipoBinario(OperadorBinario operador, Tipo izquierda, Tipo derecha, ParserRuleContext contexto) {
        if (izquierda.esError() || derecha.esError()) {
            return Tipo.ERROR;
        }
        Tipo a = comoValor(izquierda);
        Tipo b = comoValor(derecha);
        boolean igualdad = operador == OperadorBinario.IGUAL_QUE || operador == OperadorBinario.DIFERENTE_DE;
        if (a.esSimple() && b.esSimple()) {
            TipoPrimitivo resultado = TablaCompatibilidad.resultado(operador, a.primitivo(), b.primitivo());
            if (resultado != null) {
                return Tipo.primitivo(resultado);
            }
        } else if (igualdad && (esAsignable(a, b) || esAsignable(b, a))) {
            return BOOLEANO;    // objetos, arreglos y null se comparan por referencia
        }
        error(contexto, operador.getSimbolo(), "El operador '" + operador.getSimbolo() + "' no se puede aplicar a '"
                + nombre(izquierda) + "' y '" + nombre(derecha) + "'");
        return Tipo.ERROR;
    }

    // Operacion binaria que no es && ni || (esas se generan con saltos)
    protected Valor binaria(OperadorBinario operador, Valor izquierda, Valor derecha, ParserRuleContext contexto) {
        Tipo tipo = tipoBinario(operador, izquierda.tipo(), derecha.tipo(), contexto);
        if (tipo.esError()) {
            return Valor.error();
        }
        if (operador == OperadorBinario.SUMA && tipo.equals(CADENA)) {
            Valor a = aCadena(izquierda);
            return concatenar(a, aCadena(derecha));
        }
        boolean igualdad = operador == OperadorBinario.IGUAL_QUE || operador == OperadorBinario.DIFERENTE_DE;
        if (igualdad && esCadena(izquierda) && esCadena(derecha)) {
            Valor iguales = cadenasIguales(izquierda, derecha);
            return operador == OperadorBinario.IGUAL_QUE ? iguales : operar("!", iguales.direccion(), "", BOOLEANO);
        }
        Valor[] operandos = promover(izquierda, derecha);
        return operar(operador.getSimbolo(), operandos[0].direccion(), operandos[1].direccion(), tipo);
    }

    // Si un operando es decimal el otro (entero o caracter) se convierte a decimal
    private Valor[] promover(Valor izquierda, Valor derecha) {
        if (izquierda.tipo().equals(DECIMAL) || derecha.tipo().equals(DECIMAL)) {
            return new Valor[] {aDecimal(izquierda), aDecimal(derecha)};
        }
        return new Valor[] {izquierda, derecha};
    }

    private Valor aDecimal(Valor valor) {
        if (!valor.tipo().esSimple() || !TablaCompatibilidad.esEntero(valor.tipo().primitivo())) {
            return valor;
        }
        if (valor.direccion().matches("-?\\d+")) {
            return new Valor(valor.direccion() + ".0", DECIMAL);
        }
        return operar("(double)", valor.direccion(), "", DECIMAL);
    }

    private static boolean esCadena(Valor valor) {
        return comoValor(valor.tipo()).equals(CADENA);
    }

    protected Valor unaria(OperadorUnario operador, Valor operando, ParserRuleContext contexto, String simbolo) {
        if (operando.esError()) {
            return Valor.error();
        }
        Tipo tipo = comoValor(operando.tipo());
        TipoPrimitivo resultado = tipo.esSimple() ? TablaCompatibilidad.resultado(operador, tipo.primitivo()) : null;
        if (resultado == null) {
            error(contexto, simbolo, "El operador '" + simbolo + "' no se puede aplicar a '" + nombre(operando.tipo()) + "'");
            return Valor.error();
        }
        Tipo tipoResultado = Tipo.primitivo(resultado);
        return switch (operador) {
            case NEGATIVO -> operando.direccion().matches("\\d+(\\.\\d+)?")
                    ? new Valor("-" + operando.direccion(), tipoResultado)
                    : operar("neg", operando.direccion(), "", tipoResultado);
            case NEGACION -> operar("!", operando.direccion(), "", BOOLEANO);
            default -> new Valor(operando.direccion(), tipoResultado);
        };
    }

    // x++ / x-- / ++x / --x: guarda el nuevo valor y devuelve el anterior o el nuevo
    protected Valor incrementar(Direccion direccion, boolean incremento, boolean devolverAnterior,
            ParserRuleContext contexto, String simbolo) {
        if (direccion.esError()) {
            return Valor.error();
        }
        Tipo tipo = direccion.tipo();
        if (!tipo.esSimple() || !TablaCompatibilidad.esNumerico(tipo.primitivo())) {
            error(contexto, simbolo, "Solo se puede incrementar o decrementar un valor numérico (se encontró '"
                    + nombre(tipo) + "')");
            return Valor.error();
        }
        Valor anterior = cargar(direccion);
        Valor nuevo = operar(incremento ? "+" : "-", anterior.direccion(), tipo.equals(DECIMAL) ? "1.0" : "1", tipo);
        escribir(direccion, nuevo);
        return devolverAnterior ? anterior : nuevo;
    }

    // ==================================================================
    //                          Memoria
    // ==================================================================

    protected Valor cargar(Direccion direccion) {
        if (direccion.esError()) {
            return Valor.error();
        }
        return leerMemoria(direccion.enHeap() ? "heap" : "stack", direccion.base(),
                String.valueOf(direccion.desplazamiento()), direccion.tipo());
    }

    // Asignacion: valida el tipo, convierte y guarda
    protected void guardar(Direccion direccion, Valor valor, ParserRuleContext contexto) {
        if (direccion.esError() || valor.esError()) {
            return;
        }
        if (verificarAsignable(direccion.tipo(), valor, contexto)) {
            escribir(direccion, convertir(valor, direccion.tipo()));
        }
    }

    // Guarda sin validar (el valor ya es del tipo del destino)
    protected void escribir(Direccion direccion, Valor valor) {
        if (direccion.esError() || valor.esError()) {
            return;
        }
        escribirMemoria(direccion.enHeap() ? "heap" : "stack", direccion.base(),
                String.valueOf(direccion.desplazamiento()), valor);
    }

    // objeto.atributo (propio o heredado) respetando la visibilidad
    protected Direccion direccionCampo(Valor objeto, String nombre, Token token) {
        if (objeto.esError()) {
            return Direccion.error();
        }
        TipoDefinido tipo = tablas.tipos().definicion(objeto.tipo());
        if (tipo == null || tipo.getCategoria() == TipoDefinido.Categoria.PRIMITIVO) {
            error(token, nombre, "Un valor de tipo '" + nombre(objeto.tipo()) + "' no tiene atributos");
            return Direccion.error();
        }
        Campo campo = tablas.tipos().buscarCampo(tipo, nombre);
        if (campo == null) {
            error(token, nombre, "El tipo '" + tipo.getNombre() + "' no tiene un atributo '" + nombre + "'");
            return Direccion.error();
        }
        if (!accesible(campo.visibilidad(), campo.clase())) {
            error(token, nombre, "El atributo '" + nombre + "' de la clase '" + campo.clase() + "' es "
                    + campo.visibilidad() + " y no se puede usar " + desde());
        }
        return new Direccion(objeto.direccion(), tablas.tipos().desplazamiento(tipo, campo), true, campo.tipo());
    }

    // arreglo[i1][i2]...: base + dimensiones + ((i1 * d2 + i2) * d3 + i3)
    protected Direccion direccionElemento(Valor arreglo, List<Valor> indices, ParserRuleContext contexto) {
        boolean valido = true;
        for (Valor indice : indices) {
            valido &= verificarEntero(indice, contexto, "El índice");
        }
        if (arreglo.esError() || !valido) {
            return Direccion.error();
        }
        int dimensiones = arreglo.tipo().dimensiones();
        if (dimensiones == 0) {
            error(contexto, null, "Un valor de tipo '" + nombre(arreglo.tipo()) + "' no es un arreglo");
            return Direccion.error();
        }
        if (indices.size() != dimensiones) {
            error(contexto, null, "Se debe indicar un índice por cada dimensión del arreglo (tiene " + dimensiones
                    + " y se dieron " + indices.size() + ")");
            return Direccion.error();
        }
        String desplazamiento = indices.get(0).direccion();
        for (int j = 1; j < dimensiones; j++) {
            Valor tamano = leerMemoria("heap", arreglo.direccion(), String.valueOf(j), ENTERO);
            Valor fila = operar("*", desplazamiento, tamano.direccion(), ENTERO);
            desplazamiento = operar("+", fila.direccion(), indices.get(j).direccion(), ENTERO).direccion();
        }
        Valor base = operar("+", arreglo.direccion(), desplazamiento, ENTERO);
        return new Direccion(base.direccion(), dimensiones, true, arreglo.tipo().conDimensiones(0));
    }

    // Reserva celdas en el heap y devuelve la primera
    protected Valor reservar(String celdas, Tipo tipo) {
        Valor inicio = operar("=", "H", "", tipo);
        actualizar("H", "+", celdas);
        return inicio;
    }

    // Arreglo nuevo: guarda el tamano de cada dimension y luego los elementos
    protected Valor nuevoArreglo(Tipo tipo, List<Valor> tamanos, ParserRuleContext contexto) {
        boolean valido = true;
        for (Valor tamano : tamanos) {
            valido &= verificarEntero(tamano, contexto, "El tamaño del arreglo");
        }
        if (!valido || tipo.esError()) {
            return Valor.error();
        }
        Valor inicio = operar("=", "H", "", tipo);
        String elementos = tamanos.get(0).direccion();
        for (int j = 0; j < tamanos.size(); j++) {
            escribirMemoria("heap", inicio.direccion(), String.valueOf(j), tamanos.get(j));
            if (j > 0) {
                elementos = operar("*", elementos, tamanos.get(j).direccion(), ENTERO).direccion();
            }
        }
        String celdas = operar("+", elementos, String.valueOf(tamanos.size()), ENTERO).direccion();
        actualizar("H", "+", celdas);
        return inicio;
    }

    // Valor de una variable declarada sin valor: un arreglo o estructura nuevos si se conoce su forma
    protected Valor valorPorDefecto(Tipo tipo, List<Valor> tamanos, ParserRuleContext contexto) {
        if (tipo.esError()) {
            return Valor.error();
        }
        if (tipo.dimensiones() > 0) {
            return tamanos.isEmpty() ? new Valor("0", tipo) : nuevoArreglo(tipo, tamanos, contexto);
        }
        TipoDefinido definido = tablas.tipos().definicion(tipo);
        if (definido != null && definido.getCategoria() == TipoDefinido.Categoria.ESTRUCTURA) {
            return estructuraPorDefecto(definido, new HashSet<>(), contexto);
        }
        return new Valor(tipo.equals(DECIMAL) ? "0.0" : "0", tipo);
    }

    // Estructura nueva con sus arreglos de tamano fijo y sus estructuras anidadas
    private Valor estructuraPorDefecto(TipoDefinido estructura, Set<String> enCurso, ParserRuleContext contexto) {
        enCurso.add(estructura.getNombre());
        Valor objeto = reservar(String.valueOf(tablas.tipos().tamano(estructura)), Tipo.clase(estructura.getNombre()));
        for (Campo campo : estructura.getCampos()) {
            Tipo tipo = campo.tipo();
            TipoDefinido anidada = tablas.tipos().definicion(tipo);
            Valor valor = null;
            if (tipo.dimensiones() > 0 && !campo.tamanos().isEmpty()) {
                valor = nuevoArreglo(tipo, constantes(campo.tamanos()), contexto);
            } else if (anidada != null && anidada.getCategoria() == TipoDefinido.Categoria.ESTRUCTURA
                    && !enCurso.contains(anidada.getNombre())) {
                valor = estructuraPorDefecto(anidada, enCurso, contexto);
            }
            if (valor != null) {
                escribir(new Direccion(objeto.direccion(), tablas.tipos().desplazamiento(estructura, campo), true,
                        tipo), valor);
            }
        }
        enCurso.remove(estructura.getNombre());
        return objeto;
    }

    private static List<Valor> constantes(List<Integer> numeros) {
        List<Valor> valores = new ArrayList<>();
        numeros.forEach(numero -> valores.add(new Valor(String.valueOf(numero), ENTERO)));
        return valores;
    }

    // Valor inicial de una declaracion o asignacion: una expresion o una lista { } para un arreglo o estructura.
    //   elementos: los elementos si el valor es una lista, o null si es una expresion
    //   evaluar:   traduce una expresion
    protected <E> Valor inicializar(Tipo destino, E valor, List<Valor> tamanos, Function<E, List<E>> elementos,
            Function<E, Valor> evaluar, ParserRuleContext contexto) {
        List<E> lista = elementos.apply(valor);
        if (lista == null) {
            Valor resultado = evaluar.apply(valor);
            if (resultado.esError() || destino.esError()) {
                return Valor.error();
            }
            return verificarAsignable(destino, resultado, contexto) ? convertir(resultado, destino) : Valor.error();
        }
        if (destino.esError()) {
            lista.forEach(elemento -> inicializar(Tipo.ERROR, elemento, List.of(), elementos, evaluar, contexto));
            return Valor.error();
        }
        if (destino.dimensiones() > 0) {
            return inicializarArreglo(destino, lista, tamanos, elementos, evaluar, contexto);
        }
        TipoDefinido estructura = tablas.tipos().definicion(destino);
        if (estructura == null || estructura.getCategoria() != TipoDefinido.Categoria.ESTRUCTURA) {
            error(contexto, "{", "Un valor de tipo '" + nombre(destino) + "' no se puede inicializar con una lista { }");
            return Valor.error();
        }
        List<Campo> campos = estructura.getCampos();
        if (lista.size() != campos.size()) {
            error(contexto, "{", "La estructura '" + estructura.getNombre() + "' tiene " + campos.size()
                    + " atributo(s) y se dieron " + lista.size() + " valor(es)");
            return Valor.error();
        }
        Valor objeto = reservar(String.valueOf(tablas.tipos().tamano(estructura)), destino);
        for (int i = 0; i < campos.size(); i++) {
            Campo campo = campos.get(i);
            Valor dato = inicializar(campo.tipo(), lista.get(i), constantes(campo.tamanos()), elementos, evaluar,
                    contexto);
            escribir(new Direccion(objeto.direccion(), tablas.tipos().desplazamiento(estructura, campo), true,
                    campo.tipo()), dato);
        }
        return objeto;
    }

    private <E> Valor inicializarArreglo(Tipo destino, List<E> lista, List<Valor> tamanos,
            Function<E, List<E>> elementos, Function<E, Valor> evaluar, ParserRuleContext contexto) {
        int dimensiones = destino.dimensiones();
        List<E> hojas = new ArrayList<>();
        if (!aplanar(lista, dimensiones, elementos, hojas, contexto)) {
            return Valor.error();
        }
        List<Integer> forma = forma(lista, dimensiones, elementos);
        // con tamanos constantes la lista debe tener exactamente esa cantidad de elementos
        for (int j = 0; j < tamanos.size() && j < forma.size(); j++) {
            String tamano = tamanos.get(j).direccion();
            if (tamano.matches("\\d+") && Integer.parseInt(tamano) != forma.get(j)) {
                error(contexto, "{", "La dimensión " + (j + 1) + " del arreglo es de tamaño " + tamano
                        + " y la lista tiene " + forma.get(j) + " elemento(s)");
                return Valor.error();
            }
        }
        List<Valor> dimensionesFinales = tamanos.isEmpty() ? constantes(forma) : tamanos;
        Valor arreglo = nuevoArreglo(destino, dimensionesFinales, contexto);
        Tipo tipoElemento = destino.conDimensiones(0);
        for (int i = 0; i < hojas.size(); i++) {
            Valor elemento = inicializar(tipoElemento, hojas.get(i), List.of(), elementos, evaluar, contexto);
            if (!arreglo.esError()) {
                escribir(new Direccion(arreglo.direccion(), dimensiones + i, true, tipoElemento), elemento);
            }
        }
        return arreglo;
    }

    // Elementos de una lista anidada en orden por filas; todas las filas deben tener el mismo largo
    private <E> boolean aplanar(List<E> lista, int dimensiones, Function<E, List<E>> elementos, List<E> hojas,
            ParserRuleContext contexto) {
        if (dimensiones == 1) {
            hojas.addAll(lista);
            return true;
        }
        int largo = -1;
        for (E elemento : lista) {
            List<E> fila = elementos.apply(elemento);
            if (fila == null) {
                error(contexto, "{", "Cada elemento de un arreglo de " + dimensiones
                        + " dimensiones debe ser una lista { }");
                return false;
            }
            if (largo >= 0 && fila.size() != largo) {
                error(contexto, "{", "Todas las filas del arreglo deben tener la misma cantidad de elementos");
                return false;
            }
            largo = fila.size();
            if (!aplanar(fila, dimensiones - 1, elementos, hojas, contexto)) {
                return false;
            }
        }
        return true;
    }

    // Tamano de cada dimension segun la lista (ya validada por aplanar); una lista vacia deja tamanos 0
    private static <E> List<Integer> forma(List<E> lista, int dimensiones, Function<E, List<E>> elementos) {
        List<Integer> forma = new ArrayList<>();
        List<E> nivel = lista;
        for (int j = 0; j < dimensiones; j++) {
            forma.add(nivel == null ? 0 : nivel.size());
            nivel = nivel == null || nivel.isEmpty() ? null : elementos.apply(nivel.get(0));
        }
        return forma;
    }

    // Un texto leido se convierte al tipo de donde se guarda; un entero que va a un decimal se convierte
    protected Valor convertir(Valor valor, Tipo destino) {
        if (valor.tipo().primitivo() == TipoPrimitivo.LEIDO) {
            if (!destino.esSimple()) {
                return new Valor(valor.direccion(), CADENA);
            }
            return switch (destino.primitivo()) {
                case ENTERO -> cadenaAEntero(valor);
                case DECIMAL -> cadenaADecimal(valor);
                case CARACTER -> cadenaACaracter(valor);
                case BOOLEANO -> cadenaABooleano(valor);
                default -> new Valor(valor.direccion(), CADENA);
            };
        }
        if (destino.equals(DECIMAL)) {
            return aDecimal(valor);
        }
        return valor;
    }

    // ==================================================================
    //                          Literales y cadenas
    // ==================================================================

    protected Valor entero(String texto, ParserRuleContext contexto) {
        try {
            long numero = Long.parseLong(texto);
            if (numero > Integer.MAX_VALUE) {
                error(contexto, texto, "El número " + texto + " es demasiado grande (máximo " + Integer.MAX_VALUE + ")");
                return Valor.error();
            }
            return new Valor(String.valueOf(numero), ENTERO);
        } catch (NumberFormatException e) {
            error(contexto, texto, "El número " + texto + " es demasiado grande");
            return Valor.error();
        }
    }

    protected Valor decimal(String texto) {
        return new Valor(String.valueOf(Double.parseDouble(texto)), DECIMAL);
    }

    protected Valor caracter(char valor) {
        return new Valor(String.valueOf((int) valor), CARACTER);
    }

    protected Valor booleano(boolean valor) {
        return new Valor(valor ? "1" : "0", BOOLEANO);
    }

    protected Valor nulo() {
        return new Valor("0", Tipo.NULO);
    }

    // ---------- Cadenas en el heap ----------
    // Una cadena ocupa celdas seguidas del heap: [largo, c1, c2, ..., cn] (cada caracter con su codigo).
    // No hay funciones auxiliares: concatenar, comparar, convertir e imprimir se generan en linea con
    // ciclos de tres direcciones sobre el heap.

    // Literal: se reserva en el heap y se escribe caracter por caracter
    protected Valor cadena(String texto) {
        int[] codigos = texto.codePoints().toArray();
        Valor inicio = reservar(String.valueOf(codigos.length + 1), CADENA);
        escribirMemoria("heap", inicio.direccion(), "0", constante(codigos.length));
        for (int i = 0; i < codigos.length; i++) {
            escribirMemoria("heap", inicio.direccion(), String.valueOf(i + 1), constante(codigos[i]));
        }
        return inicio;
    }

    private static Valor constante(int numero) {
        return new Valor(String.valueOf(numero), ENTERO);
    }

    private Valor largo(Valor cadena) {
        return leerMemoria("heap", cadena.direccion(), "0", ENTERO);
    }

    // cadena[i] (i desde 0): heap[cadena + i + 1]
    private Valor caracterEn(Valor cadena, String indice) {
        Valor posicion = operar("+", cadena.direccion(), indice, ENTERO);
        return leerMemoria("heap", posicion.direccion(), "1", CARACTER);
    }

    // cadena[i] = caracter
    private void escribirCaracter(Valor cadena, String indice, String caracter) {
        Valor posicion = operar("+", cadena.direccion(), indice, ENTERO);
        escribirMemoria("heap", posicion.direccion(), "1", new Valor(caracter, CARACTER));
    }

    // destino[inicio + i] = origen[i] para cada caracter de origen
    private void copiarCaracteres(Valor origen, Valor destino, String inicio) {
        Valor n = largo(origen);
        String i = temporal();
        String ciclo = etiqueta();
        String fin = etiqueta();
        emitir("=", "0", "", i);
        colocar(ciclo);
        emitir("if >=", i, n.direccion(), fin);
        Valor caracter = caracterEn(origen, i);
        Valor posicion = operar("+", inicio, i, ENTERO);
        escribirCaracter(destino, posicion.direccion(), caracter.direccion());
        emitir("+", i, "1", i);
        saltar(ciclo);
        colocar(fin);
    }

    // a + b: cadena nueva con los caracteres de las dos
    protected Valor concatenar(Valor a, Valor b) {
        Valor largoA = largo(a);
        Valor largoB = largo(b);
        Valor n = operar("+", largoA.direccion(), largoB.direccion(), ENTERO);
        Valor celdas = operar("+", n.direccion(), "1", ENTERO);
        Valor resultado = reservar(celdas.direccion(), CADENA);
        escribirMemoria("heap", resultado.direccion(), "0", n);
        copiarCaracteres(a, resultado, "0");
        copiarCaracteres(b, resultado, largoA.direccion());
        return resultado;
    }

    // 1 si las dos cadenas tienen los mismos caracteres, 0 si no
    protected Valor cadenasIguales(Valor a, Valor b) {
        String resultado = temporal();
        String i = temporal();
        String ciclo = etiqueta();
        String iguales = etiqueta();
        String fin = etiqueta();
        emitir("=", "0", "", resultado);
        Valor largoA = largo(a);
        Valor largoB = largo(b);
        emitir("if !=", largoA.direccion(), largoB.direccion(), fin);
        emitir("=", "0", "", i);
        colocar(ciclo);
        emitir("if >=", i, largoA.direccion(), iguales);
        Valor caracterA = caracterEn(a, i);
        Valor caracterB = caracterEn(b, i);
        emitir("if !=", caracterA.direccion(), caracterB.direccion(), fin);
        emitir("+", i, "1", i);
        saltar(ciclo);
        colocar(iguales);
        emitir("=", "1", "", resultado);
        colocar(fin);
        return new Valor(resultado, BOOLEANO);
    }

    // ---------- Conversion a cadena (para concatenar) ----------

    protected Valor aCadena(Valor valor) {
        Tipo tipo = comoValor(valor.tipo());
        if (tipo.equals(CADENA)) {
            return new Valor(valor.direccion(), CADENA);
        }
        if (tipo.equals(BOOLEANO)) {
            return booleanoACadena(valor);
        }
        if (tipo.equals(DECIMAL)) {
            return decimalACadena(valor);
        }
        if (tipo.equals(CARACTER)) {
            return caracterACadena(valor);
        }
        return enteroACadena(valor);
    }

    private Valor caracterACadena(Valor caracter) {
        Valor resultado = reservar("2", CADENA);
        escribirMemoria("heap", resultado.direccion(), "0", constante(1));
        escribirMemoria("heap", resultado.direccion(), "1", caracter);
        return resultado;
    }

    // verum/falsus, verdadero/falso o true/false segun el lenguaje
    private Valor booleanoACadena(Valor valor) {
        String resultado = temporal();
        String verdadero = etiqueta();
        String falso = etiqueta();
        String fin = etiqueta();
        saltarSegun(valor, verdadero, falso);
        colocar(verdadero);
        emitir("=", cadena(lenguaje.booleano(true)).direccion(), "", resultado);
        saltar(fin);
        colocar(falso);
        emitir("=", cadena(lenguaje.booleano(false)).direccion(), "", resultado);
        colocar(fin);
        return new Valor(resultado, CADENA);
    }

    // Se cuentan los digitos, se reserva la cadena y los digitos se escriben de derecha a izquierda
    private Valor enteroACadena(Valor valor) {
        String n = temporal();
        String negativo = temporal();
        String positivo = etiqueta();
        emitir("=", valor.direccion(), "", n);
        emitir("=", "0", "", negativo);
        emitir("if >=", n, "0", positivo);
        emitir("=", "1", "", negativo);
        emitir("-", "0", n, n);
        colocar(positivo);

        String digitos = temporal();
        String resto = temporal();
        String contar = etiqueta();
        String contado = etiqueta();
        emitir("=", "1", "", digitos);
        emitir("=", n, "", resto);
        colocar(contar);
        emitir("if <", resto, "10", contado);
        emitir("/", resto, "10", resto);
        emitir("+", digitos, "1", digitos);
        saltar(contar);
        colocar(contado);

        Valor total = operar("+", digitos, negativo, ENTERO);
        Valor celdas = operar("+", total.direccion(), "1", ENTERO);
        Valor resultado = reservar(celdas.direccion(), CADENA);
        escribirMemoria("heap", resultado.direccion(), "0", total);
        String sinSigno = etiqueta();
        emitir("if ==", negativo, "0", sinSigno);
        escribirMemoria("heap", resultado.direccion(), "1", constante('-'));
        colocar(sinSigno);

        // el ultimo digito va en la celda resultado + total
        String posicion = temporal();
        String otroDigito = etiqueta();
        emitir("+", resultado.direccion(), total.direccion(), posicion);
        colocar(otroDigito);
        Valor digito = operar("%", n, "10", ENTERO);
        Valor caracter = operar("+", digito.direccion(), "48", CARACTER);
        escribirMemoria("heap", posicion, "0", caracter);
        emitir("-", posicion, "1", posicion);
        emitir("/", n, "10", n);
        emitir("if >", n, "0", otroDigito);
        return resultado;
    }

    // Decimal redondeado a dos cifras: [signo] parte entera, punto y dos digitos
    private Valor decimalACadena(Valor valor) {
        String x = temporal();
        String negativo = temporal();
        String positivo = etiqueta();
        emitir("=", valor.direccion(), "", x);
        emitir("=", "0", "", negativo);
        emitir("if >=", x, "0.0", positivo);
        emitir("=", "1", "", negativo);
        emitir("-", "0.0", x, x);
        colocar(positivo);
        Valor centesimas = operar("*", x, "100.0", DECIMAL);
        Valor redondeado = operar("+", centesimas.direccion(), "0.5", DECIMAL);
        Valor entero = operar("(int)", redondeado.direccion(), "", ENTERO);
        Valor parteEntera = operar("/", entero.direccion(), "100", ENTERO);
        Valor fraccion = operar("%", entero.direccion(), "100", ENTERO);
        Valor texto = enteroACadena(parteEntera);

        Valor largoEntera = largo(texto);
        Valor antesDelPunto = operar("+", largoEntera.direccion(), negativo, ENTERO);
        Valor total = operar("+", antesDelPunto.direccion(), "3", ENTERO);
        Valor celdas = operar("+", total.direccion(), "1", ENTERO);
        Valor resultado = reservar(celdas.direccion(), CADENA);
        escribirMemoria("heap", resultado.direccion(), "0", total);
        String sinSigno = etiqueta();
        emitir("if ==", negativo, "0", sinSigno);
        escribirMemoria("heap", resultado.direccion(), "1", constante('-'));
        colocar(sinSigno);
        copiarCaracteres(texto, resultado, negativo);
        Valor punto = operar("+", resultado.direccion(), antesDelPunto.direccion(), ENTERO);
        escribirMemoria("heap", punto.direccion(), "1", constante('.'));
        Valor decenas = operar("/", fraccion.direccion(), "10", ENTERO);
        escribirMemoria("heap", punto.direccion(), "2", operar("+", decenas.direccion(), "48", CARACTER));
        Valor unidades = operar("%", fraccion.direccion(), "10", ENTERO);
        escribirMemoria("heap", punto.direccion(), "3", operar("+", unidades.direccion(), "48", CARACTER));
        return resultado;
    }

    // ---------- De texto leido a otros tipos ----------

    // Signo opcional y digitos; se detiene en el primer caracter que no es digito
    private Valor cadenaAEntero(Valor texto) {
        String resultado = temporal();
        String negativo = temporal();
        String i = temporal();
        String ciclo = etiqueta();
        String signo = etiqueta();
        String fin = etiqueta();
        emitir("=", "0", "", resultado);
        emitir("=", "0", "", negativo);
        emitir("=", "0", "", i);
        Valor n = largo(texto);
        emitir("if ==", n.direccion(), "0", fin);
        Valor primero = caracterEn(texto, "0");
        emitir("if !=", primero.direccion(), String.valueOf((int) '-'), ciclo);
        emitir("=", "1", "", negativo);
        emitir("=", "1", "", i);
        colocar(ciclo);
        emitir("if >=", i, n.direccion(), signo);
        Valor caracter = caracterEn(texto, i);
        emitir("if <", caracter.direccion(), "48", signo);
        emitir("if >", caracter.direccion(), "57", signo);
        emitir("*", resultado, "10", resultado);
        Valor digito = operar("-", caracter.direccion(), "48", ENTERO);
        emitir("+", resultado, digito.direccion(), resultado);
        emitir("+", i, "1", i);
        saltar(ciclo);
        colocar(signo);
        emitir("if ==", negativo, "0", fin);
        emitir("-", "0", resultado, resultado);
        colocar(fin);
        return new Valor(resultado, ENTERO);
    }

    // Signo opcional, parte entera y parte decimal despues del punto
    private Valor cadenaADecimal(Valor texto) {
        String resultado = temporal();
        String negativo = temporal();
        String i = temporal();
        String factor = temporal();
        String entera = etiqueta();
        String fraccion = etiqueta();
        String cicloFraccion = etiqueta();
        String signo = etiqueta();
        String fin = etiqueta();
        emitir("=", "0.0", "", resultado);
        emitir("=", "0", "", negativo);
        emitir("=", "0", "", i);
        Valor n = largo(texto);
        emitir("if ==", n.direccion(), "0", fin);
        Valor primero = caracterEn(texto, "0");
        emitir("if !=", primero.direccion(), String.valueOf((int) '-'), entera);
        emitir("=", "1", "", negativo);
        emitir("=", "1", "", i);
        colocar(entera);
        emitir("if >=", i, n.direccion(), signo);
        Valor caracter = caracterEn(texto, i);
        emitir("if ==", caracter.direccion(), String.valueOf((int) '.'), fraccion);
        emitir("if <", caracter.direccion(), "48", signo);
        emitir("if >", caracter.direccion(), "57", signo);
        emitir("*", resultado, "10.0", resultado);
        Valor digito = operar("-", caracter.direccion(), "48", ENTERO);
        Valor digitoDecimal = operar("(double)", digito.direccion(), "", DECIMAL);
        emitir("+", resultado, digitoDecimal.direccion(), resultado);
        emitir("+", i, "1", i);
        saltar(entera);
        colocar(fraccion);
        emitir("+", i, "1", i);
        emitir("=", "0.1", "", factor);
        colocar(cicloFraccion);
        emitir("if >=", i, n.direccion(), signo);
        Valor decimal = caracterEn(texto, i);
        emitir("if <", decimal.direccion(), "48", signo);
        emitir("if >", decimal.direccion(), "57", signo);
        Valor cifra = operar("-", decimal.direccion(), "48", ENTERO);
        Valor cifraDecimal = operar("(double)", cifra.direccion(), "", DECIMAL);
        Valor parte = operar("*", cifraDecimal.direccion(), factor, DECIMAL);
        emitir("+", resultado, parte.direccion(), resultado);
        emitir("/", factor, "10.0", factor);
        emitir("+", i, "1", i);
        saltar(cicloFraccion);
        colocar(signo);
        emitir("if ==", negativo, "0", fin);
        emitir("-", "0.0", resultado, resultado);
        colocar(fin);
        return new Valor(resultado, DECIMAL);
    }

    // El primer caracter (0 si la linea esta vacia)
    private Valor cadenaACaracter(Valor texto) {
        String resultado = temporal();
        String fin = etiqueta();
        emitir("=", "0", "", resultado);
        Valor n = largo(texto);
        emitir("if ==", n.direccion(), "0", fin);
        Valor primero = caracterEn(texto, "0");
        emitir("=", primero.direccion(), "", resultado);
        colocar(fin);
        return new Valor(resultado, CARACTER);
    }

    // Verdadero si empieza con v (verum, verdadero), t (true) o 1
    private Valor cadenaABooleano(Valor texto) {
        String resultado = temporal();
        String verdadero = etiqueta();
        String fin = etiqueta();
        emitir("=", "0", "", resultado);
        Valor n = largo(texto);
        emitir("if ==", n.direccion(), "0", fin);
        Valor primero = caracterEn(texto, "0");
        for (char inicial : new char[] {'v', 't', '1'}) {
            emitir("if ==", primero.direccion(), String.valueOf((int) inicial), verdadero);
        }
        saltar(fin);
        colocar(verdadero);
        emitir("=", "1", "", resultado);
        colocar(fin);
        return new Valor(resultado, BOOLEANO);
    }

    // ==================================================================
    //                          Entrada y salida
    // ==================================================================

    protected void imprimir(Valor valor, ParserRuleContext contexto) {
        if (valor.esError()) {
            return;
        }
        Tipo tipo = comoValor(valor.tipo());
        if (tipo.equals(VOID)) {
            error(contexto, null, "No se puede imprimir una llamada que no retorna valor");
            return;
        }
        if (!tipo.esSimple() || tipo.primitivo() == TipoPrimitivo.NULO) {
            error(contexto, null, "Solo se pueden imprimir valores primitivos (se encontró '" + nombre(tipo) + "')");
            return;
        }
        if (tipo.equals(BOOLEANO)) {
            // la palabra del lenguaje, caracter por caracter
            String verdadero = etiqueta();
            String falso = etiqueta();
            String fin = etiqueta();
            saltarSegun(valor, verdadero, falso);
            colocar(verdadero);
            imprimirTexto(lenguaje.booleano(true));
            saltar(fin);
            colocar(falso);
            imprimirTexto(lenguaje.booleano(false));
            colocar(fin);
            return;
        }
        if (tipo.equals(CADENA)) {
            imprimirCadena(valor);
            return;
        }
        String como = tipo.equals(DECIMAL) ? "decimal" : tipo.equals(CARACTER) ? "caracter" : "entero";
        emitir("print", valor.direccion(), como, "");
    }

    // Recorre la cadena del heap e imprime caracter por caracter
    private void imprimirCadena(Valor cadena) {
        Valor n = largo(cadena);
        String i = temporal();
        String ciclo = etiqueta();
        String fin = etiqueta();
        emitir("=", "0", "", i);
        colocar(ciclo);
        emitir("if >=", i, n.direccion(), fin);
        Valor caracter = caracterEn(cadena, i);
        emitir("print", caracter.direccion(), "caracter", "");
        emitir("+", i, "1", i);
        saltar(ciclo);
        colocar(fin);
    }

    private void imprimirTexto(String texto) {
        texto.codePoints().forEach(caracter -> emitir("print", String.valueOf(caracter), "caracter", ""));
    }

    protected void saltoDeLinea() {
        emitir("print", "10", "caracter", "");
    }

    // Lee una linea del teclado caracter por caracter (hasta el salto de linea) y la deja en el heap
    protected Valor leer() {
        Valor resultado = operar("=", "H", "", Tipo.LEIDO);
        String n = temporal();
        String ciclo = etiqueta();
        String fin = etiqueta();
        emitir("=", "0", "", n);
        colocar(ciclo);
        Valor caracter = operar("leer_caracter", "", "", CARACTER);
        emitir("if ==", caracter.direccion(), "10", fin);
        escribirCaracter(resultado, n, caracter.direccion());
        emitir("+", n, "1", n);
        saltar(ciclo);
        colocar(fin);
        escribirMemoria("heap", resultado.direccion(), "0", new Valor(n, ENTERO));
        Valor ocupado = operar("+", n, "1", ENTERO);
        actualizar("H", "+", ocupado.direccion());
        return resultado;
    }

    // Lee en un destino primitivo (Pig Latin: x <<)
    protected void leerEn(Direccion destino, ParserRuleContext contexto) {
        if (destino.esError()) {
            return;
        }
        if (!destino.tipo().esSimple()) {
            error(contexto, null, "Solo se puede leer en un valor de tipo primitivo (se encontró '"
                    + nombre(destino.tipo()) + "')");
            return;
        }
        escribir(destino, convertir(leer(), destino.tipo()));
    }

    // ==================================================================
    //                          Llamadas
    // ==================================================================

    // Primer metodo/constructor cuyos parametros aceptan los argumentos
    protected Simbolo elegirSobrecarga(List<Simbolo> opciones, List<Valor> argumentos) {
        for (Simbolo opcion : opciones) {
            if (opcion.parametros().size() != argumentos.size()) {
                continue;
            }
            boolean acepta = true;
            for (int i = 0; i < argumentos.size() && acepta; i++) {
                acepta = esAsignable(opcion.parametros().get(i), argumentos.get(i).tipo());
            }
            if (acepta) {
                return opcion;
            }
        }
        return null;
    }

    private static boolean hayErrores(List<Valor> valores) {
        return valores.stream().anyMatch(Valor::esError);
    }

    private List<Valor> convertirArgumentos(List<Tipo> parametros, List<Valor> argumentos) {
        List<Valor> convertidos = new ArrayList<>();
        for (int i = 0; i < argumentos.size(); i++) {
            convertidos.add(convertir(argumentos.get(i), parametros.get(i)));
        }
        return convertidos;
    }

    // Los argumentos van en el marco siguiente desde la celda 1 (la 0 es para el retorno)
    private void escribirArgumentos(List<Valor> argumentos) {
        for (int i = 0; i < argumentos.size(); i++) {
            escribirMemoria("stack", "P", MARCO + "+" + (i + 1), argumentos.get(i));
        }
    }

    // Despues de volver, el retorno esta en la celda 0 del marco de la funcion llamada
    private Valor resultadoDeLlamada(Tipo tipoRetorno) {
        if (tipoRetorno.equals(VOID)) {
            return new Valor("0", VOID);
        }
        return leerMemoria("stack", "P", MARCO + "+0", tipoRetorno);
    }

    // Copia los argumentos al marco siguiente, mueve P, llama, regresa P y lee el retorno
    protected Valor llamar(String etiqueta, Tipo tipoRetorno, List<Valor> argumentos) {
        escribirArgumentos(argumentos);
        actualizar("P", "+", MARCO);
        emitir("call", "", "", etiqueta);
        actualizar("P", "-", MARCO);
        return resultadoDeLlamada(tipoRetorno);
    }

    // Llamada a una funcion de un .y
    protected Valor llamarFuncion(String nombre, List<Valor> argumentos, Token token) {
        Simbolo funcion = tablas.simbolos().buscarFuncion(nombre);
        if (funcion == null) {
            error(token, nombre, "La función '" + nombre + "' no está definida");
            return Valor.error();
        }
        if (funcion.parametros().size() != argumentos.size()) {
            error(token, nombre, "La función '" + nombre + "' espera " + funcion.parametros().size()
                    + " argumento(s) y se dieron " + argumentos.size());
            return Valor.error();
        }
        boolean valido = true;
        for (int i = 0; i < argumentos.size(); i++) {
            Tipo parametro = funcion.parametros().get(i);
            if (!esAsignable(parametro, argumentos.get(i).tipo())) {
                error(token, nombre, "El argumento " + (i + 1) + " de '" + nombre + "' debe ser de tipo '"
                        + nombre(parametro) + "' (se encontró '" + nombre(argumentos.get(i).tipo()) + "')");
                valido = false;
            }
        }
        if (!valido || hayErrores(argumentos)) {
            return Valor.error();
        }
        return llamar(funcion.etiqueta(), funcion.tipo(), convertirArgumentos(funcion.parametros(), argumentos));
    }

    // objeto.metodo(argumentos). Los metodos public/protected tienen despacho dinamico: la celda 0 del
    // objeto guarda el numero de su clase y se salta a la implementacion de esa clase. Los private/default
    // y las llamadas con super van directo al metodo.
    protected Valor llamarMetodo(Valor objeto, String nombre, List<Valor> argumentos, Token token,
            boolean despachoEstatico) {
        if (objeto.esError()) {
            return Valor.error();
        }
        TipoDefinido clase = tablas.tipos().definicion(objeto.tipo());
        if (clase == null || !clase.esClase()) {
            error(token, nombre, "Un valor de tipo '" + nombre(objeto.tipo()) + "' no tiene métodos");
            return Valor.error();
        }
        List<Simbolo> candidatos = tablas.buscarMetodos(clase.getNombre(), nombre);
        if (candidatos.isEmpty()) {
            error(token, nombre, metodoNoEncontrado(clase, nombre));
            return Valor.error();
        }
        if (hayErrores(argumentos)) {
            return Valor.error();
        }
        Simbolo metodo = elegirSobrecarga(candidatos, argumentos);
        if (metodo == null) {
            error(token, nombre, "Ningún método '" + nombre + "' de la clase '" + clase.getNombre()
                    + "' acepta los argumentos " + describir(argumentos));
            return Valor.error();
        }
        if (!accesible(metodo.visibilidad(), metodo.clase())) {
            error(token, nombre, "El método '" + nombre + "' de la clase '" + metodo.clase() + "' es "
                    + metodo.visibilidad() + " y no se puede usar " + desde());
            return Valor.error();
        }
        List<Valor> conThis = new ArrayList<>(List.of(objeto));
        conThis.addAll(convertirArgumentos(metodo.parametros(), argumentos));
        if (despachoEstatico || !metodo.visibilidad().seHereda()) {
            return llamar(metodo.etiqueta(), metodo.tipo(), conThis);
        }
        // implementacion de cada subclase que sobrescribe el metodo -> numeros de esas clases
        String propia = implementacion(clase, metodo);
        Map<String, List<Integer>> sobrescrituras = new LinkedHashMap<>();
        for (TipoDefinido tipo : tablas.tipos().todos()) {
            if (tipo.esClase() && tipo != clase && tablas.tipos().esSubclase(tipo.getNombre(), clase.getNombre())) {
                String otra = implementacion(tipo, metodo);
                if (!otra.equals(propia)) {
                    sobrescrituras.computeIfAbsent(otra, k -> new ArrayList<>()).add(numeroDeClase(tipo));
                }
            }
        }
        if (sobrescrituras.isEmpty()) {
            return llamar(propia, metodo.tipo(), conThis);
        }
        Valor numero = leerMemoria("heap", objeto.direccion(), "0", ENTERO);
        escribirArgumentos(conThis);
        actualizar("P", "+", MARCO);
        String fin = etiqueta();
        Map<String, String> saltosPorImplementacion = new LinkedHashMap<>();
        for (Map.Entry<String, List<Integer>> sobrescritura : sobrescrituras.entrySet()) {
            String destino = etiqueta();
            saltosPorImplementacion.put(sobrescritura.getKey(), destino);
            for (int numeroClase : sobrescritura.getValue()) {
                emitir("if ==", numero.direccion(), String.valueOf(numeroClase), destino);
            }
        }
        emitir("call", "", "", propia);
        saltar(fin);
        for (Map.Entry<String, String> salto : saltosPorImplementacion.entrySet()) {
            colocar(salto.getValue());
            emitir("call", "", "", salto.getKey());
            saltar(fin);
        }
        colocar(fin);
        actualizar("P", "-", MARCO);
        return resultadoDeLlamada(metodo.tipo());
    }

    // Etiqueta del metodo que ejecuta un objeto de esa clase (el propio o el heredado mas cercano)
    private String implementacion(TipoDefinido clase, Simbolo metodo) {
        for (Simbolo candidato : tablas.vtable(clase)) {
            if (candidato.mismaFirma(metodo)) {
                return candidato.etiqueta();
            }
        }
        return metodo.etiqueta();
    }

    // Numero de la clase que se guarda en la celda 0 de sus objetos (desde 1, en el orden de la tabla de tipos)
    protected int numeroDeClase(TipoDefinido clase) {
        int numero = 0;
        for (TipoDefinido tipo : tablas.tipos().todos()) {
            if (tipo.esClase()) {
                numero++;
                if (tipo == clase) {
                    return numero;
                }
            }
        }
        return 0;
    }

    // Mensaje cuando la clase no tiene un metodo visible con ese nombre
    private String metodoNoEncontrado(TipoDefinido clase, String nombre) {
        for (TipoDefinido ancestro : tablas.tipos().linaje(clase)) {
            if (!tablas.simbolos().metodosPropios(ancestro.getNombre(), nombre).isEmpty()) {
                return "El método '" + nombre + "' de la clase '" + ancestro.getNombre()
                        + "' es private y no se hereda a '" + clase.getNombre() + "'";
            }
        }
        return "La clase '" + clase.getNombre() + "' no tiene un método '" + nombre + "'";
    }

    // Constructor que aceptan los argumentos (null si la clase no declara constructores y no hay argumentos).
    // Reporta si ninguno acepta o si no es accesible; en ese caso devuelve null y error queda en true.
    protected Simbolo elegirConstructor(TipoDefinido clase, List<Valor> argumentos, Token token, boolean[] error) {
        List<Simbolo> constructores = tablas.simbolos().constructoresDe(clase.getNombre());
        if (constructores.isEmpty()) {
            if (!argumentos.isEmpty()) {
                error(token, clase.getNombre(), "La clase '" + clase.getNombre()
                        + "' no declara constructores: solo se puede crear sin argumentos");
                error[0] = true;
            }
            return null;
        }
        Simbolo constructor = elegirSobrecarga(constructores, argumentos);
        if (constructor == null) {
            error(token, clase.getNombre(), "Ningún constructor de la clase '" + clase.getNombre()
                    + "' acepta los argumentos " + describir(argumentos));
            error[0] = true;
            return null;
        }
        if (!accesible(constructor.visibilidad(), clase.getNombre())) {
            error(token, clase.getNombre(), "El constructor " + describirParametros(constructor) + " de la clase '"
                    + clase.getNombre() + "' es " + constructor.visibilidad() + " y no se puede usar " + desde());
            error[0] = true;
        }
        return constructor;
    }

    protected String describirParametros(Simbolo funcion) {
        List<String> nombres = new ArrayList<>();
        funcion.parametros().forEach(tipo -> nombres.add(nombre(tipo)));
        return funcion.nombre() + "(" + String.join(", ", nombres) + ")";
    }

    // Llama a un constructor sobre un objeto ya creado (new, o super(...) en un constructor hijo)
    protected void llamarConstructor(TipoDefinido clase, Simbolo constructor, Valor objeto, List<Valor> argumentos) {
        List<Valor> conThis = new ArrayList<>(List.of(objeto));
        if (constructor != null) {
            conThis.addAll(convertirArgumentos(constructor.parametros(), argumentos));
        }
        llamar(etiquetaConstructor(clase, constructor), VOID, conThis);
    }

    // Etiqueta del constructor elegido; sin constructores declarados se usa el que genera el traductor
    protected static String etiquetaConstructor(TipoDefinido clase, Simbolo constructor) {
        return constructor != null ? constructor.etiqueta() : clase.getNombre() + "_constructor";
    }

    // new Clase(args) / novus Clase(args): reserva el objeto, guarda el numero de su clase y llama al constructor
    protected Valor crearObjeto(String nombre, List<Valor> argumentos, Token token) {
        TipoDefinido clase = tablas.tipos().buscar(nombre);
        if (clase == null) {
            error(token, nombre, "La clase '" + nombre + "' no está definida");
            return Valor.error();
        }
        if (!clase.esClase()) {
            error(token, nombre, "'" + nombre + "' no es una clase, no se puede crear con new/novus");
            return Valor.error();
        }
        Tipo tipo = Tipo.clase(nombre);
        if (hayErrores(argumentos)) {
            return new Valor("0", tipo);
        }
        boolean[] error = {false};
        Simbolo constructor = elegirConstructor(clase, argumentos, token, error);
        if (error[0]) {
            return new Valor("0", tipo);
        }
        Valor objeto = reservar(String.valueOf(tablas.tipos().tamano(clase)), tipo);
        escribirMemoria("heap", objeto.direccion(), "0", constante(numeroDeClase(clase)));
        llamarConstructor(clase, constructor, objeto, argumentos);
        return objeto;
    }
}
