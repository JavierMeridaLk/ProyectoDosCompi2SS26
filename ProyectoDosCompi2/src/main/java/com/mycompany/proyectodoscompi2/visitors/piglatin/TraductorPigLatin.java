package com.mycompany.proyectodoscompi2.visitors.piglatin;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import com.mycompany.pigLatinParser;
import com.mycompany.pigLatinVisitor;
import com.mycompany.proyectodoscompi2.analizador.Lenguaje;
import com.mycompany.proyectodoscompi2.cuartetas.Direccion;
import com.mycompany.proyectodoscompi2.cuartetas.ListaCuartetas;
import com.mycompany.proyectodoscompi2.cuartetas.Valor;
import com.mycompany.proyectodoscompi2.errores.ManejadorErrores;
import com.mycompany.proyectodoscompi2.tablas.CategoriaSimbolo;
import com.mycompany.proyectodoscompi2.tablas.Simbolo;
import com.mycompany.proyectodoscompi2.tablas.Tablas;
import com.mycompany.proyectodoscompi2.tipos.Literales;
import com.mycompany.proyectodoscompi2.tipos.OperadorBinario;
import com.mycompany.proyectodoscompi2.tipos.OperadorUnario;
import com.mycompany.proyectodoscompi2.tipos.Tipo;
import com.mycompany.proyectodoscompi2.visitors.TraductorBase;

// Segundo recorrido del .pig, analisis semantico y cuartetas del programa principal
public class TraductorPigLatin extends TraductorBase implements pigLatinVisitor<Valor> {

    public TraductorPigLatin(Tablas tablas, ListaCuartetas codigo, ManejadorErrores errores) {
        super(tablas, codigo, errores, Lenguaje.PIG_LATIN);
    }

    //Utilidades

    public static Tipo tipoDe(pigLatinParser.TipoContext contexto) {
        if (contexto.NUMERUS() != null) {
            return Tipo.ENTERO;
        }
        if (contexto.DECIMALIS() != null) {
            return Tipo.DECIMAL;
        }
        if (contexto.TEXTUM() != null) {
            return Tipo.CADENA;
        }
        if (contexto.LITTERA() != null) {
            return Tipo.CARACTER;
        }
        if (contexto.BOOL() != null) {
            return Tipo.BOOLEANO;
        }
        return Tipo.clase(contexto.IDENTIFICADOR().getText());
    }

    private List<Valor> argumentos(pigLatinParser.ArgumentosContext contexto) {
        List<Valor> valores = new ArrayList<>();
        if (contexto != null) {
            contexto.expresion().forEach(expresion -> valores.add(visit(expresion)));
        }
        return valores;
    }

    // Valor inicial
    private Valor inicializar(Tipo destino, pigLatinParser.InicializadorContext valor, List<Valor> tamanos) {
        return inicializar(destino, valor, tamanos,
                i -> i.listaInicializacion() != null ? i.listaInicializacion().inicializador() : null,
                i -> visit(i.expresion()), valor);
    }

    // Valor de una expresion guardado en un destino de tipo conocido
    private Valor asignar(Tipo destino, pigLatinParser.ExpresionContext expresion) {
        return inicializar(destino, expresion, List.of(), e -> null, this::visit, expresion);
    }

    private void instrucciones(pigLatinParser.BloqueContext bloque, String nombre) {
        entrarBloque(nombre);
        bloque.instruccion().forEach(this::visit);
        salirAmbito();
    }
   
    private record Recorrido(Valor valor, Direccion direccion, boolean llamada) {
    }

    // Aplica los sufijos desde 'desde' sobre el valor actual
    private Recorrido recorrer(Valor actual, List<pigLatinParser.SufijoContext> sufijos, int desde, boolean destino,
            Direccion direccionInicial) {
        Direccion direccion = direccionInicial;
        boolean llamada = false;
        int i = desde;
        while (i < sufijos.size()) {
            pigLatinParser.SufijoContext sufijo = sufijos.get(i);
            if (sufijo instanceof pigLatinParser.SufijoAtributoContext atributo && i + 1 < sufijos.size()
                    && sufijos.get(i + 1) instanceof pigLatinParser.SufijoLlamadaContext llamadaMetodo) {
                Valor objeto = cargarSiHaceFalta(actual, direccion);
                List<Valor> valores = argumentos(llamadaMetodo.argumentos());
                actual = llamarMetodo(objeto, atributo.IDENTIFICADOR().getText(), valores,
                        atributo.IDENTIFICADOR().getSymbol(), false);
                direccion = null;
                llamada = true;
                i += 2;
                continue;
            }
            if (sufijo instanceof pigLatinParser.SufijoAtributoContext atributo) {
                Valor objeto = cargarSiHaceFalta(actual, direccion);
                direccion = direccionCampo(objeto, atributo.IDENTIFICADOR().getText(), atributo.IDENTIFICADOR().getSymbol());
                actual = null;
                llamada = false;
                i++;
                continue;
            }
            if (sufijo instanceof pigLatinParser.SufijoIndiceContext) {
                Valor arreglo = cargarSiHaceFalta(actual, direccion);
                List<Valor> indices = new ArrayList<>();
                pigLatinParser.SufijoContext primero = sufijo;
                while (i < sufijos.size() && sufijos.get(i) instanceof pigLatinParser.SufijoIndiceContext indice) {
                    indices.add(visit(indice.expresion()));
                    i++;
                }
                direccion = direccionElemento(arreglo, indices, primero);
                actual = null;
                llamada = false;
                continue;
            }
            error(sufijo, "(", "Solo se pueden llamar funciones y métodos");
            argumentos(((pigLatinParser.SufijoLlamadaContext) sufijo).argumentos());
            return new Recorrido(Valor.error(), Direccion.error(), true);
        }
        if (destino) {
            return new Recorrido(null, llamada ? null : direccion, llamada);
        }
        return new Recorrido(cargarSiHaceFalta(actual, direccion), direccion, llamada);
    }

    // El valor actual, o el que esta en la direccion del paso anterior
    private Valor cargarSiHaceFalta(Valor actual, Direccion direccion) {
        return actual != null ? actual : cargar(direccion);
    }

    // Acceso completo desde un nombre
    private Recorrido acceso(pigLatinParser.AccesoContext contexto, boolean destino) {
        Token nombre = contexto.IDENTIFICADOR().getSymbol();
        List<pigLatinParser.SufijoContext> sufijos = contexto.sufijo();
        if (!sufijos.isEmpty() && sufijos.get(0) instanceof pigLatinParser.SufijoLlamadaContext llamada) {
            List<Valor> valores = argumentos(llamada.argumentos());
            Valor resultado = llamarFuncion(nombre.getText(), valores, nombre);
            if (sufijos.size() == 1) {
                return new Recorrido(resultado, null, true);
            }
            return recorrer(resultado, sufijos, 1, destino, null);
        }
        Simbolo variable = buscarVariable(nombre.getText());
        Direccion direccion;
        if (variable == null) {
            error(nombre, nombre.getText(), "La variable '" + nombre.getText()
                    + "' no está declarada (o se usa antes de declararla)");
            direccion = Direccion.error();
        } else {
            direccion = direccionDe(variable);
        }
        return recorrer(null, sufijos, 0, destino, direccion);
    }

    // Destino de una asignacion, incremento o lectura
    private Direccion destino(pigLatinParser.AccesoContext contexto) {
        Recorrido recorrido = acceso(contexto, true);
        if (recorrido.llamada()) {
            error(contexto, contexto.getText(), "No se puede asignar al resultado de una llamada");
            return Direccion.error();
        }
        return recorrido.direccion();
    }

    // Condicion como saltos en corto circuito, comparaciones directo al if
    private void condicion(pigLatinParser.ExpresionContext contexto, String verdadero, String falso) {
        if (contexto instanceof pigLatinParser.ExpresionYContext y) {
            String siguiente = etiqueta();
            condicion(y.expresion(0), siguiente, falso);
            colocar(siguiente);
            condicion(y.expresion(1), verdadero, falso);
        } else if (contexto instanceof pigLatinParser.ExpresionOContext o) {
            String siguiente = etiqueta();
            condicion(o.expresion(0), verdadero, siguiente);
            colocar(siguiente);
            condicion(o.expresion(1), verdadero, falso);
        } else if (contexto instanceof pigLatinParser.ExpresionUnariaContext unaria && unaria.RESTA() == null) {
            condicion(unaria.expresion(), falso, verdadero);
        } else if (contexto instanceof pigLatinParser.ExpresionParentesisContext parentesis) {
            condicion(parentesis.expresion(), verdadero, falso);
        } else if (contexto instanceof pigLatinParser.ExpresionRelacionalContext relacional) {
            comparar(OperadorBinario.desdeTexto(relacional.operador.getText()), visit(relacional.expresion(0)),
                    visit(relacional.expresion(1)), verdadero, falso, relacional);
        } else if (contexto instanceof pigLatinParser.ExpresionIgualdadContext igualdad) {
            comparar(OperadorBinario.desdeTexto(igualdad.operador.getText()), visit(igualdad.expresion(0)),
                    visit(igualdad.expresion(1)), verdadero, falso, igualdad);
        } else {
            Valor valor = visit(contexto);
            if (verificarCondicion(valor, contexto)) {
                saltarSegun(valor, verdadero, falso);
            } else {
                saltar(falso);
            }
        }
    }

    //Programa

    @Override
    public Valor visitPrograma(pigLatinParser.ProgramaContext contexto) {
        entrarMarco("main");
        inicioFuncion("main", VOID);
        // el programa empieza con el marco de main al inicio del stack y el heap vacio
        emitir("=", "0", "", "P");
        emitir("=", "1", "", "H");
        if (contexto.seccionVariables() != null) {
            visit(contexto.seccionVariables());
        }
        visit(contexto.seccionPrincipal());
        finFuncion(true);
        salirAmbito();
        return null;
    }

    // los import se procesan en DeclaracionesPigLatin
    @Override
    public Valor visitImportacion(pigLatinParser.ImportacionContext contexto) {
        return null;
    }

    @Override
    public Valor visitRutaImportacion(pigLatinParser.RutaImportacionContext contexto) {
        return null;
    }

    @Override
    public Valor visitSeccionVariables(pigLatinParser.SeccionVariablesContext contexto) {
        contexto.declaracion().forEach(this::visit);
        return null;
    }

    @Override
    public Valor visitSeccionPrincipal(pigLatinParser.SeccionPrincipalContext contexto) {
        contexto.instruccion().forEach(this::visit);
        return null;
    }

    //Declaraciones

    @Override
    public Valor visitDeclaracion(pigLatinParser.DeclaracionContext contexto) {
        return visit(contexto.getChild(0));
    }

    @Override
    public Valor visitDeclaracionVariable(pigLatinParser.DeclaracionVariableContext contexto) {
        Token nombre = contexto.IDENTIFICADOR().getSymbol();
        pigLatinParser.CuerpoDeclaracionContext cuerpo = contexto.cuerpoDeclaracion();
        Tipo tipo;
        Valor valor;
        if (cuerpo instanceof pigLatinParser.DeclaracionConTipoContext conTipo) {
            // esto x : numerus 5;   esto p : Punto {1, 2};
            tipo = conocido(tipoDe(conTipo.tipo()), conTipo.tipo().getStart());
            valor = conTipo.inicializador() != null ? inicializar(tipo, conTipo.inicializador(), List.of())
                    : valorPorDefecto(tipo, List.of(), conTipo);
        } else if (cuerpo instanceof pigLatinParser.DeclaracionBooleanaContext booleana) {
            // esto activo : verum;
            tipo = BOOLEANO;
            valor = booleano(booleana.VERUM() != null);
        } else if (cuerpo instanceof pigLatinParser.DeclaracionObjetoContext objeto) {
            Token nombreTipo = objeto.IDENTIFICADOR().getSymbol();
            tipo = conocido(Tipo.clase(nombreTipo.getText()), nombreTipo);
            valor = asignar(tipo, objeto.expresion());
        } else {
            // esto d : novus Diccionario();
            valor = visit(((pigLatinParser.DeclaracionObjetoSinTipoContext) cuerpo).creacionObjeto());
            tipo = valor.tipo().equals(VOID) ? Tipo.ERROR : valor.tipo();
        }
        // se declara despues de evaluar el valor se usa una x que todavia no existe
        Simbolo variable = declararVariable(nombre, tipo, CategoriaSimbolo.VARIABLE);
        escribir(direccionDe(variable), valor);
        return null;
    }

    @Override
    public Valor visitDeclaracionConTipo(pigLatinParser.DeclaracionConTipoContext contexto) {
        return null;
    }

    @Override
    public Valor visitDeclaracionBooleana(pigLatinParser.DeclaracionBooleanaContext contexto) {
        return null;
    }

    @Override
    public Valor visitDeclaracionObjeto(pigLatinParser.DeclaracionObjetoContext contexto) {
        return null;
    }

    @Override
    public Valor visitDeclaracionObjetoSinTipo(pigLatinParser.DeclaracionObjetoSinTipoContext contexto) {
        return null;
    }

    // series 
    @Override
    public Valor visitDeclaracionArreglo(pigLatinParser.DeclaracionArregloContext contexto) {
        Tipo tipo = conocido(tipoDe(contexto.tipo()), contexto.tipo().getStart());
        tipo = tipo.esError() ? tipo : tipo.conDimensiones(contexto.dimension().size());
        List<Valor> tamanos = new ArrayList<>();
        contexto.dimension().forEach(dimension -> tamanos.add(visit(dimension.expresion())));
        Valor valor = contexto.inicializador() != null ? inicializar(tipo, contexto.inicializador(), tamanos)
                : valorPorDefecto(tipo, tamanos, contexto);
        Simbolo variable = declararVariable(contexto.IDENTIFICADOR().getSymbol(), tipo, CategoriaSimbolo.VARIABLE);
        escribir(direccionDe(variable), valor);
        return null;
    }

    @Override
    public Valor visitDimension(pigLatinParser.DimensionContext contexto) {
        return visit(contexto.expresion());
    }

    @Override
    public Valor visitInicializador(pigLatinParser.InicializadorContext contexto) {
        return null;
    }

    @Override
    public Valor visitListaInicializacion(pigLatinParser.ListaInicializacionContext contexto) {
        return null;
    }

    @Override
    public Valor visitTipo(pigLatinParser.TipoContext contexto) {
        return null;
    }


    //Instrucciones
    
    @Override
    public Valor visitBloque(pigLatinParser.BloqueContext contexto) {
        instrucciones(contexto, "bloque");
        return null;
    }

    @Override
    public Valor visitInstruccion(pigLatinParser.InstruccionContext contexto) {
        return visit(contexto.getChild(0));
    }

    @Override
    public Valor visitAsignacion(pigLatinParser.AsignacionContext contexto) {
        Direccion destino = destino(contexto.acceso());
        Valor valor = asignar(destino.tipo(), contexto.expresion());
        escribir(destino, valor);
        return null;
    }

    @Override
    public Valor visitIncremento(pigLatinParser.IncrementoContext contexto) {
        boolean incremento = contexto.INCREMENTO() != null;
        incrementar(destino(contexto.acceso()), incremento, true, contexto, incremento ? "++" : "--");
        return null;
    }

    // Una instruccion que es solo un acceso debe ser una llamada
    @Override
    public Valor visitLlamada(pigLatinParser.LlamadaContext contexto) {
        Recorrido recorrido = acceso(contexto.acceso(), false);
        if (!recorrido.llamada() && !recorrido.valor().esError()) {
            error(contexto, contexto.acceso().getText(), "Esta instrucción no hace nada: solo una llamada a una "
                    + "función o método puede ir sola");
        }
        return null;
    }

    @Override
    public Valor visitContinuar(pigLatinParser.ContinuarContext contexto) {
        continuar(contexto, "perge");
        return null;
    }

    @Override
    public Valor visitInterrumpir(pigLatinParser.InterrumpirContext contexto) {
        romper(contexto, "interrumpe", "un ciclo");
        return null;
    }

    // si aliter aliter finis;
    @Override
    public Valor visitCondicional(pigLatinParser.CondicionalContext contexto) {
        String fin = etiqueta();
        rama(contexto.expresion(), contexto.bloque(), fin, "si");
        for (pigLatinParser.SinoSiContext sinoSi : contexto.sinoSi()) {
            rama(sinoSi.expresion(), sinoSi.bloque(), fin, "aliter si");
        }
        if (contexto.sino() != null) {
            instrucciones(contexto.sino().bloque(), "aliter");
        }
        colocar(fin);
        return null;
    }

    private void rama(pigLatinParser.ExpresionContext condicion, pigLatinParser.BloqueContext bloque, String fin,
            String nombre) {
        String verdadero = etiqueta();
        String falso = etiqueta();
        condicion(condicion, verdadero, falso);
        colocar(verdadero);
        instrucciones(bloque, nombre);
        saltar(fin);
        colocar(falso);
    }

    @Override
    public Valor visitSinoSi(pigLatinParser.SinoSiContext contexto) {
        return null;
    }

    @Override
    public Valor visitSino(pigLatinParser.SinoContext contexto) {
        return null;
    }

    @Override
    public Valor visitCicloDum(pigLatinParser.CicloDumContext contexto) {
        String inicio = etiqueta();
        String cuerpo = etiqueta();
        String fin = etiqueta();
        colocar(inicio);
        condicion(contexto.expresion(), cuerpo, fin);
        colocar(cuerpo);
        enCiclo(fin, inicio, () -> instrucciones(contexto.bloque(), "dum"));
        saltar(inicio);
        colocar(fin);
        return null;
    }

    @Override
    public Valor visitCicloFacere(pigLatinParser.CicloFacereContext contexto) {
        String inicio = etiqueta();
        String evaluar = etiqueta();
        String fin = etiqueta();
        colocar(inicio);
        enCiclo(fin, evaluar, () -> instrucciones(contexto.bloque(), "facere"));
        colocar(evaluar);
        condicion(contexto.expresion(), inicio, fin);
        colocar(fin);
        return null;
    }

    @Override
    public Valor visitCicloPer(pigLatinParser.CicloPerContext contexto) {
        // la variable declarada en el inicio solo existe dentro del ciclo
        entrarBloque("per");
        visit(contexto.inicioPer());
        String evaluar = etiqueta();
        String cuerpo = etiqueta();
        String actualizacion = etiqueta();
        String fin = etiqueta();
        colocar(evaluar);
        condicion(contexto.expresion(), cuerpo, fin);
        colocar(cuerpo);
        enCiclo(fin, actualizacion, () -> instrucciones(contexto.bloque(), "cuerpo"));
        colocar(actualizacion);
        visit(contexto.actualizacionPer());
        saltar(evaluar);
        colocar(fin);
        salirAmbito();
        return null;
    }

    // per
    @Override
    public Valor visitInicioPerDeclaracion(pigLatinParser.InicioPerDeclaracionContext contexto) {
        Tipo tipo = conocido(tipoDe(contexto.tipo()), contexto.tipo().getStart());
        Valor valor = asignar(tipo, contexto.expresion());
        Simbolo variable = declararVariable(contexto.IDENTIFICADOR().getSymbol(), tipo, CategoriaSimbolo.VARIABLE);
        escribir(direccionDe(variable), valor);
        return null;
    }

    @Override
    public Valor visitInicioPerAsignacion(pigLatinParser.InicioPerAsignacionContext contexto) {
        Direccion destino = destino(contexto.acceso());
        escribir(destino, asignar(destino.tipo(), contexto.expresion()));
        return null;
    }

    @Override
    public Valor visitActualizacionPer(pigLatinParser.ActualizacionPerContext contexto) {
        Direccion destino = destino(contexto.acceso());
        if (contexto.expresion() != null) {
            escribir(destino, asignar(destino.tipo(), contexto.expresion()));
        } else {
            boolean incremento = contexto.INCREMENTO() != null;
            incrementar(destino, incremento, true, contexto, incremento ? "++" : "--");
        }
        return null;
    }

    // x << lee una linea y la convierte al tipo de x
    @Override
    public Valor visitLectura(pigLatinParser.LecturaContext contexto) {
        if (contexto.acceso() != null) {
            leerEn(destino(contexto.acceso()), contexto);
        } else {
            leer();
        }
        return null;
    }

    // >> a >> b: imprime los valores seguidos, sin salto de linea
    @Override
    public Valor visitEscritura(pigLatinParser.EscrituraContext contexto) {
        for (pigLatinParser.ExpresionContext expresion : contexto.expresion()) {
            imprimir(visit(expresion), expresion);
        }
        return null;
    }

    //Accesos y objetos

    @Override
    public Valor visitAcceso(pigLatinParser.AccesoContext contexto) {
        return acceso(contexto, false).valor();
    }

    @Override
    public Valor visitSufijoIndice(pigLatinParser.SufijoIndiceContext contexto) {
        return null;
    }

    @Override
    public Valor visitSufijoAtributo(pigLatinParser.SufijoAtributoContext contexto) {
        return null;
    }

    @Override
    public Valor visitSufijoLlamada(pigLatinParser.SufijoLlamadaContext contexto) {
        return null;
    }

    @Override
    public Valor visitArgumentos(pigLatinParser.ArgumentosContext contexto) {
        return null;
    }

    @Override
    public Valor visitCreacionObjeto(pigLatinParser.CreacionObjetoContext contexto) {
        List<Valor> valores = argumentos(contexto.argumentos());
        return crearObjeto(contexto.IDENTIFICADOR().getText(), valores, contexto.IDENTIFICADOR().getSymbol());
    }

    // novus Persona(...).getNombre()
    @Override
    public Valor visitExpresionNuevoObjeto(pigLatinParser.ExpresionNuevoObjetoContext contexto) {
        Valor objeto = visit(contexto.creacionObjeto());
        if (contexto.sufijo().isEmpty()) {
            return objeto;
        }
        return recorrer(objeto, contexto.sufijo(), 0, false, null).valor();
    }

    //Expresiones

    @Override
    public Valor visitExpresionUnaria(pigLatinParser.ExpresionUnariaContext contexto) {
        Valor operando = visit(contexto.expresion());
        if (contexto.RESTA() != null) {
            return unaria(OperadorUnario.NEGATIVO, operando, contexto, "-");
        }
        return unaria(OperadorUnario.NEGACION, operando, contexto, contexto.operador.getText());
    }

    private Valor binaria(pigLatinParser.ExpresionContext izquierda, Token operador,
            pigLatinParser.ExpresionContext derecha, ParserRuleContext contexto) {
        Valor a = visit(izquierda);
        Valor b = visit(derecha);
        return binaria(OperadorBinario.desdeTexto(operador.getText()), a, b, contexto);
    }

    @Override
    public Valor visitExpresionIgualdad(pigLatinParser.ExpresionIgualdadContext contexto) {
        return binaria(contexto.expresion(0), contexto.operador, contexto.expresion(1), contexto);
    }

    @Override
    public Valor visitExpresionMultiplicativa(pigLatinParser.ExpresionMultiplicativaContext contexto) {
        return binaria(contexto.expresion(0), contexto.operador, contexto.expresion(1), contexto);
    }

    @Override
    public Valor visitExpresionRelacional(pigLatinParser.ExpresionRelacionalContext contexto) {
        return binaria(contexto.expresion(0), contexto.operador, contexto.expresion(1), contexto);
    }

    @Override
    public Valor visitExpresionAditiva(pigLatinParser.ExpresionAditivaContext contexto) {
        return binaria(contexto.expresion(0), contexto.operador, contexto.expresion(1), contexto);
    }

    @Override
    public Valor visitExpresionY(pigLatinParser.ExpresionYContext contexto) {
        return valorDeCondicion((verdadero, falso) -> condicion(contexto, verdadero, falso));
    }

    @Override
    public Valor visitExpresionO(pigLatinParser.ExpresionOContext contexto) {
        return valorDeCondicion((verdadero, falso) -> condicion(contexto, verdadero, falso));
    }

    @Override
    public Valor visitExpresionLiteral(pigLatinParser.ExpresionLiteralContext contexto) {
        return visit(contexto.literal());
    }

    @Override
    public Valor visitExpresionParentesis(pigLatinParser.ExpresionParentesisContext contexto) {
        return visit(contexto.expresion());
    }

    @Override
    public Valor visitExpresionAcceso(pigLatinParser.ExpresionAccesoContext contexto) {
        return visit(contexto.acceso());
    }

    @Override
    public Valor visitLiteral(pigLatinParser.LiteralContext contexto) {
        String texto = contexto.getText();
        if (contexto.ENTERO() != null) {
            return entero(texto, contexto);
        }
        if (contexto.DECIMAL() != null) {
            return decimal(texto);
        }
        if (contexto.TEXTO() != null) {
            return cadena(Literales.cadena(texto));
        }
        if (contexto.CARACTER() != null) {
            return caracter(Literales.caracter(texto));
        }
        if (contexto.NULL() != null) {
            return nulo();
        }
        return booleano(contexto.VERUM() != null);
    }
}
