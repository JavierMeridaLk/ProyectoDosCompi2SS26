package com.mycompany.proyectodoscompi2.visitors.y;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;

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
import com.mycompany.yParser;
import com.mycompany.yVisitor;

// Segundo recorrido de un .y, analisis semantico y cuartetas de cada funcion
public class TraductorY extends TraductorBase implements yVisitor<Valor> {

    public TraductorY(Tablas tablas, ListaCuartetas codigo, ManejadorErrores errores) {
        super(tablas, codigo, errores, Lenguaje.Y);
    }

    //Utilidades

    private List<Valor> argumentos(yParser.ArgumentosContext contexto) {
        List<Valor> valores = new ArrayList<>();
        if (contexto != null) {
            contexto.expresion().forEach(expresion -> valores.add(visit(expresion)));
        }
        return valores;
    }

    // Valor inicial
    private Valor inicializar(Tipo destino, yParser.InicializadorContext valor, List<Valor> tamanos) {
        return inicializar(destino, valor, tamanos,
                i -> i.listaInicializacion() != null ? i.listaInicializacion().inicializador() : null,
                i -> visit(i.expresion()), valor);
    }

    private void instrucciones(yParser.BloqueContext bloque, String nombre) {
        entrarBloque(nombre);
        bloque.instruccion().forEach(this::visit);
        salirAmbito();
    }

    // variable
    private Direccion direccion(yParser.AccesoContext contexto) {
        Token nombre = contexto.IDENTIFICADOR().getSymbol();
        Simbolo variable = buscarVariable(nombre.getText());
        Direccion actual;
        if (variable == null) {
            error(nombre, nombre.getText(), "La variable '" + nombre.getText()
                    + "' no está declarada (o se usa antes de declararla)");
            actual = Direccion.error();
        } else {
            actual = direccionDe(variable);
        }
        List<yParser.SufijoAccesoContext> sufijos = contexto.sufijoAcceso();
        for (int i = 0; i < sufijos.size(); i++) {
            if (sufijos.get(i) instanceof yParser.SufijoAtributoContext atributo) {
                actual = direccionCampo(cargar(actual), atributo.IDENTIFICADOR().getText(),
                        atributo.IDENTIFICADOR().getSymbol());
                continue;
            }
            // indices seguidos
            List<Valor> indices = new ArrayList<>();
            while (i < sufijos.size() && sufijos.get(i) instanceof yParser.SufijoIndiceContext indice) {
                indices.add(visit(indice.expresion()));
                i++;
            }
            i--;
            actual = direccionElemento(cargar(actual), indices, sufijos.get(i));
        }
        return actual;
    }

    // Condicion como saltos && y || en corto circuito, comparaciones directo al if
    private void condicion(yParser.ExpresionContext contexto, String verdadero, String falso) {
        if (contexto instanceof yParser.ExpresionYContext y) {
            String siguiente = etiqueta();
            condicion(y.expresion(0), siguiente, falso);
            colocar(siguiente);
            condicion(y.expresion(1), verdadero, falso);
        } else if (contexto instanceof yParser.ExpresionOContext o) {
            String siguiente = etiqueta();
            condicion(o.expresion(0), verdadero, siguiente);
            colocar(siguiente);
            condicion(o.expresion(1), verdadero, falso);
        } else if (contexto instanceof yParser.ExpresionUnariaContext unaria && unaria.NO_LOGICO() != null) {
            condicion(unaria.expresion(), falso, verdadero);
        } else if (contexto instanceof yParser.ExpresionParentesisContext parentesis) {
            condicion(parentesis.expresion(), verdadero, falso);
        } else if (contexto instanceof yParser.ExpresionRelacionalContext relacional) {
            comparar(OperadorBinario.desdeTexto(relacional.operador.getText()), visit(relacional.expresion(0)),
                    visit(relacional.expresion(1)), verdadero, falso, relacional);
        } else if (contexto instanceof yParser.ExpresionIgualdadContext igualdad) {
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

    //Programa, estructuras y funciones

    @Override
    public Valor visitPrograma(yParser.ProgramaContext contexto) {
        if (contexto.seccionEstructuras() != null) {
            visit(contexto.seccionEstructuras());
        }
        visit(contexto.seccionFunciones());
        return null;
    }

    @Override
    public Valor visitSeccionEstructuras(yParser.SeccionEstructurasContext contexto) {
        contexto.definicionEstructura().forEach(this::visit);
        return null;
    }

    @Override
    public Valor visitSeccionFunciones(yParser.SeccionFuncionesContext contexto) {
        contexto.definicionFuncion().forEach(this::visit);
        return null;
    }

    // aqui se revisa que existan los tipos de sus atributos
    @Override
    public Valor visitDefinicionEstructura(yParser.DefinicionEstructuraContext contexto) {
        for (yParser.AtributoEstructuraContext atributo : contexto.atributoEstructura()) {
            conocido(DeclaracionesY.tipoDe(atributo.tipo()), atributo.tipo().getStart());
        }
        return null;
    }

    @Override
    public Valor visitAtributoEstructura(yParser.AtributoEstructuraContext contexto) {
        return null;
    }

    @Override
    public Valor visitDefinicionFuncion(yParser.DefinicionFuncionContext contexto) {
        String nombre = contexto.IDENTIFICADOR().getText();
        Tipo retorno = DeclaracionesY.tipoRetorno(contexto);
        retorno = contexto.tipoRetorno() != null ? conocido(retorno, contexto.tipoRetorno().getStart()) : retorno;
        entrarMarco(nombre);
        inicioFuncion("f_" + nombre, retorno);
        if (contexto.parametros() != null) {
            for (yParser.ParametroContext parametro : contexto.parametros().parametro()) {
                Tipo tipo = conocido(DeclaracionesY.tipoDe(parametro), parametro.getStart());
                declararVariable(DeclaracionesY.nombreDe(parametro), tipo, CategoriaSimbolo.PARAMETRO);
            }
        }
        // el cuerpo comparte el ambito de los parametros, una variable no puede llamarse como un parametro
        contexto.bloque().instruccion().forEach(this::visit);
        finFuncion(false);
        salirAmbito();
        return null;
    }

    @Override
    public Valor visitTipoRetorno(yParser.TipoRetornoContext contexto) {
        return null;
    }

    @Override
    public Valor visitParametros(yParser.ParametrosContext contexto) {
        return null;
    }

    @Override
    public Valor visitParametroValor(yParser.ParametroValorContext contexto) {
        return null;
    }

    @Override
    public Valor visitParametroArreglo(yParser.ParametroArregloContext contexto) {
        return null;
    }

    @Override
    public Valor visitParametroEstructura(yParser.ParametroEstructuraContext contexto) {
        return null;
    }

    @Override
    public Valor visitTipo(yParser.TipoContext contexto) {
        return null;
    }

    //Instrucciones

    @Override
    public Valor visitBloque(yParser.BloqueContext contexto) {
        instrucciones(contexto, "bloque");
        return null;
    }

    @Override
    public Valor visitInstruccion(yParser.InstruccionContext contexto) {
        return contexto.instruccionSimple() != null ? visit(contexto.instruccionSimple())
                : visit(contexto.instruccionCompuesta());
    }

    @Override
    public Valor visitInstruccionSimple(yParser.InstruccionSimpleContext contexto) {
        if (contexto.ROMPER() != null) {
            romper(contexto, "romper", "un ciclo o de un elegir");
        } else if (contexto.CONTINUAR() != null) {
            continuar(contexto, "continuar");
        } else {
            visit(contexto.getChild(0));
        }
        return null;
    }

    @Override
    public Valor visitInstruccionCompuesta(yParser.InstruccionCompuestaContext contexto) {
        return visit(contexto.getChild(0));
    }

    @Override
    public Valor visitDeclaracion(yParser.DeclaracionContext contexto) {
        Tipo tipo = DeclaracionesY.tipoDe(contexto.tipo()).conDimensiones(contexto.dimension().size());
        tipo = conocido(tipo, contexto.tipo().getStart());
        List<Valor> tamanos = new ArrayList<>();
        contexto.dimension().forEach(dimension -> tamanos.add(visit(dimension.expresion())));
        Valor valor = contexto.inicializador() != null ? inicializar(tipo, contexto.inicializador(), tamanos)
                : valorPorDefecto(tipo, tamanos, contexto);
        Simbolo variable = declararVariable(contexto.IDENTIFICADOR().getSymbol(), tipo, CategoriaSimbolo.VARIABLE);
        escribir(direccionDe(variable), valor);
        return null;
    }

    @Override
    public Valor visitDimension(yParser.DimensionContext contexto) {
        return visit(contexto.expresion());
    }

    @Override
    public Valor visitInicializador(yParser.InicializadorContext contexto) {
        return null;
    }

    @Override
    public Valor visitListaInicializacion(yParser.ListaInicializacionContext contexto) {
        return null;
    }

    @Override
    public Valor visitAsignacion(yParser.AsignacionContext contexto) {
        Direccion destino = direccion(contexto.acceso());
        Valor valor = inicializar(destino.tipo(), contexto.inicializador(), List.of());
        escribir(destino, valor);
        return null;
    }

    @Override
    public Valor visitIncremento(yParser.IncrementoContext contexto) {
        boolean incremento = contexto.INCREMENTO() != null;
        incrementar(direccion(contexto.acceso()), incremento, true, contexto, incremento ? "++" : "--");
        return null;
    }

    @Override
    public Valor visitRetornar(yParser.RetornarContext contexto) {
        retornar(contexto.expresion() != null ? visit(contexto.expresion()) : null, contexto);
        return null;
    }

    // imprimir(a, b, c) muestra los valores seguidos y termina la linea
    @Override
    public Valor visitImprimir(yParser.ImprimirContext contexto) {
        if (contexto.argumentos() != null) {
            for (yParser.ExpresionContext expresion : contexto.argumentos().expresion()) {
                imprimir(visit(expresion), expresion);
            }
        }
        saltoDeLinea();
        return null;
    }

    @Override
    public Valor visitLeer(yParser.LeerContext contexto) {
        return leer();
    }

    @Override
    public Valor visitLlamadaFuncion(yParser.LlamadaFuncionContext contexto) {
        return llamarFuncion(contexto.IDENTIFICADOR().getText(), argumentos(contexto.argumentos()),
                contexto.IDENTIFICADOR().getSymbol());
    }

    @Override
    public Valor visitArgumentos(yParser.ArgumentosContext contexto) {
        return null;
    }

    // si ... sino ... contrario
    @Override
    public Valor visitCondicional(yParser.CondicionalContext contexto) {
        String fin = etiqueta();
        rama(contexto.expresion(), contexto.bloque(), fin, "si");
        for (yParser.SinoSiContext sinoSi : contexto.sinoSi()) {
            rama(sinoSi.expresion(), sinoSi.bloque(), fin, "sino");
        }
        if (contexto.contrario() != null) {
            instrucciones(contexto.contrario().bloque(), "contrario");
        }
        colocar(fin);
        return null;
    }

    private void rama(yParser.ExpresionContext condicion, yParser.BloqueContext bloque, String fin, String nombre) {
        String verdadero = etiqueta();
        String falso = etiqueta();
        condicion(condicion, verdadero, falso);
        colocar(verdadero);
        instrucciones(bloque, nombre);
        saltar(fin);
        colocar(falso);
    }

    @Override
    public Valor visitSinoSi(yParser.SinoSiContext contexto) {
        return null;
    }

    @Override
    public Valor visitContrario(yParser.ContrarioContext contexto) {
        return null;
    }

    // Se compara el valor con cada caso y se salta al primero que coincide
    @Override
    public Valor visitElegir(yParser.ElegirContext contexto) {
        Valor valor = visit(contexto.expresion());
        String fin = etiqueta();
        List<String> etiquetas = new ArrayList<>();
        for (yParser.CasoContext caso : contexto.caso()) {
            String etiqueta = etiqueta();
            etiquetas.add(etiqueta);
            comparar(OperadorBinario.IGUAL_QUE, valor, visit(caso.expresion()), etiqueta, null, caso.expresion());
        }
        String siempre = contexto.casoSiempre() != null ? etiqueta() : fin;
        saltar(siempre);
        enSeleccion(fin, () -> {
            for (int i = 0; i < contexto.caso().size(); i++) {
                colocar(etiquetas.get(i));
                instrucciones(contexto.caso(i).bloque(), "caso");
            }
            if (contexto.casoSiempre() != null) {
                colocar(siempre);
                instrucciones(contexto.casoSiempre().bloque(), "siempre");
            }
        });
        colocar(fin);
        return null;
    }

    @Override
    public Valor visitCaso(yParser.CasoContext contexto) {
        return null;
    }

    @Override
    public Valor visitCasoSiempre(yParser.CasoSiempreContext contexto) {
        return null;
    }

    @Override
    public Valor visitCicloPara(yParser.CicloParaContext contexto) {
        // la variable declarada en el inicio solo existe dentro del ciclo
        entrarBloque("para");
        if (contexto.inicioPara() != null) {
            visit(contexto.inicioPara().getChild(0));
        }
        String evaluar = etiqueta();
        String cuerpo = etiqueta();
        String actualizacion = etiqueta();
        String fin = etiqueta();
        colocar(evaluar);
        if (contexto.expresion() != null) {
            condicion(contexto.expresion(), cuerpo, fin);
        }
        colocar(cuerpo);
        enCiclo(fin, actualizacion, () -> instrucciones(contexto.bloque(), "cuerpo"));
        colocar(actualizacion);
        if (contexto.actualizacionPara() != null) {
            visit(contexto.actualizacionPara().getChild(0));
        }
        saltar(evaluar);
        colocar(fin);
        salirAmbito();
        return null;
    }

    @Override
    public Valor visitInicioPara(yParser.InicioParaContext contexto) {
        return null;
    }

    @Override
    public Valor visitActualizacionPara(yParser.ActualizacionParaContext contexto) {
        return null;
    }

    @Override
    public Valor visitCicloMientras(yParser.CicloMientrasContext contexto) {
        String inicio = etiqueta();
        String cuerpo = etiqueta();
        String fin = etiqueta();
        colocar(inicio);
        condicion(contexto.expresion(), cuerpo, fin);
        colocar(cuerpo);
        enCiclo(fin, inicio, () -> instrucciones(contexto.bloque(), "mientras"));
        saltar(inicio);
        colocar(fin);
        return null;
    }

    @Override
    public Valor visitCicloHacer(yParser.CicloHacerContext contexto) {
        String inicio = etiqueta();
        String evaluar = etiqueta();
        String fin = etiqueta();
        colocar(inicio);
        enCiclo(fin, evaluar, () -> instrucciones(contexto.bloque(), "hacer"));
        colocar(evaluar);
        condicion(contexto.expresion(), inicio, fin);
        colocar(fin);
        return null;
    }

    //Accesos

    @Override
    public Valor visitAcceso(yParser.AccesoContext contexto) {
        return cargar(direccion(contexto));
    }

    @Override
    public Valor visitSufijoIndice(yParser.SufijoIndiceContext contexto) {
        return null;
    }

    @Override
    public Valor visitSufijoAtributo(yParser.SufijoAtributoContext contexto) {
        return null;
    }

    // Expresiones

    @Override
    public Valor visitExpresionLeer(yParser.ExpresionLeerContext contexto) {
        return leer();
    }

    @Override
    public Valor visitExpresionUnaria(yParser.ExpresionUnariaContext contexto) {
        Valor operando = visit(contexto.expresion());
        boolean negacion = contexto.NO_LOGICO() != null;
        return unaria(negacion ? OperadorUnario.NEGACION : OperadorUnario.NEGATIVO, operando, contexto,
                negacion ? "!" : "-");
    }

    private Valor binaria(yParser.ExpresionContext izquierda, Token operador, yParser.ExpresionContext derecha,
            yParser.ExpresionContext contexto) {
        Valor a = visit(izquierda);
        Valor b = visit(derecha);
        return binaria(OperadorBinario.desdeTexto(operador.getText()), a, b, contexto);
    }

    @Override
    public Valor visitExpresionIgualdad(yParser.ExpresionIgualdadContext contexto) {
        return binaria(contexto.expresion(0), contexto.operador, contexto.expresion(1), contexto);
    }

    @Override
    public Valor visitExpresionMultiplicativa(yParser.ExpresionMultiplicativaContext contexto) {
        return binaria(contexto.expresion(0), contexto.operador, contexto.expresion(1), contexto);
    }

    @Override
    public Valor visitExpresionRelacional(yParser.ExpresionRelacionalContext contexto) {
        return binaria(contexto.expresion(0), contexto.operador, contexto.expresion(1), contexto);
    }

    @Override
    public Valor visitExpresionAditiva(yParser.ExpresionAditivaContext contexto) {
        return binaria(contexto.expresion(0), contexto.operador, contexto.expresion(1), contexto);
    }

    @Override
    public Valor visitExpresionY(yParser.ExpresionYContext contexto) {
        return valorDeCondicion((verdadero, falso) -> condicion(contexto, verdadero, falso));
    }

    @Override
    public Valor visitExpresionO(yParser.ExpresionOContext contexto) {
        return valorDeCondicion((verdadero, falso) -> condicion(contexto, verdadero, falso));
    }

    @Override
    public Valor visitExpresionLiteral(yParser.ExpresionLiteralContext contexto) {
        return visit(contexto.literal());
    }

    @Override
    public Valor visitExpresionParentesis(yParser.ExpresionParentesisContext contexto) {
        return visit(contexto.expresion());
    }

    @Override
    public Valor visitExpresionLlamada(yParser.ExpresionLlamadaContext contexto) {
        return visit(contexto.llamadaFuncion());
    }

    @Override
    public Valor visitExpresionAcceso(yParser.ExpresionAccesoContext contexto) {
        return visit(contexto.acceso());
    }

    @Override
    public Valor visitLiteral(yParser.LiteralContext contexto) {
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
        return booleano(contexto.VERDADERO() != null);
    }
}
