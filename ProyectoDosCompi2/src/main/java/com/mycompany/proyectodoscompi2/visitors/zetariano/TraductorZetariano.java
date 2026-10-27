package com.mycompany.proyectodoscompi2.visitors.zetariano;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import com.mycompany.ZetarianoParser;
import com.mycompany.ZetarianoVisitor;
import com.mycompany.proyectodoscompi2.analizador.Lenguaje;
import com.mycompany.proyectodoscompi2.cuartetas.Cuarteta;
import com.mycompany.proyectodoscompi2.cuartetas.Direccion;
import com.mycompany.proyectodoscompi2.cuartetas.ListaCuartetas;
import com.mycompany.proyectodoscompi2.cuartetas.Valor;
import com.mycompany.proyectodoscompi2.errores.ManejadorErrores;
import com.mycompany.proyectodoscompi2.tablas.Campo;
import com.mycompany.proyectodoscompi2.tablas.CategoriaSimbolo;
import com.mycompany.proyectodoscompi2.tablas.Simbolo;
import com.mycompany.proyectodoscompi2.tablas.Tablas;
import com.mycompany.proyectodoscompi2.tablas.TipoDefinido;
import com.mycompany.proyectodoscompi2.tipos.Literales;
import com.mycompany.proyectodoscompi2.tipos.OperadorBinario;
import com.mycompany.proyectodoscompi2.tipos.OperadorUnario;
import com.mycompany.proyectodoscompi2.tipos.Posicion;
import com.mycompany.proyectodoscompi2.tipos.Tipo;
import com.mycompany.proyectodoscompi2.visitors.TraductorBase;

// Segundo recorrido de un .z: analisis semantico (herencia, @Override, super, visibilidad, tipos) y
// cuartetas de la clase en un solo paso. Cada clase genera:
//   Clase_init         valores iniciales de sus atributos propios
//   Clase_constructor  uno por constructor: super(...) (o el vacio del padre), Clase_init y el cuerpo
//   Clase_metodo       uno por metodo
// Cada objeto guarda en su celda 0 el numero de su clase para el despacho dinamico.
public class TraductorZetariano extends TraductorBase implements ZetarianoVisitor<Valor> {

    private static final String THIS = "this";

    public TraductorZetariano(Tablas tablas, ListaCuartetas codigo, ManejadorErrores errores) {
        super(tablas, codigo, errores, Lenguaje.ZETARIANO);
    }

    // ==================================================================
    //                          Utilidades
    // ==================================================================

    private List<Valor> argumentos(ZetarianoParser.ArgumentosContext contexto) {
        List<Valor> valores = new ArrayList<>();
        if (contexto != null) {
            contexto.expresion().forEach(expresion -> valores.add(visit(expresion)));
        }
        return valores;
    }

    // Lista { } de un arreglo: sus elementos; una expresion: null
    private static List<ParserRuleContext> elementos(ParserRuleContext contexto) {
        ZetarianoParser.InicializadorArregloContext lista = null;
        if (contexto instanceof ZetarianoParser.InicializadorArregloContext arreglo) {
            lista = arreglo;
        } else if (contexto instanceof ZetarianoParser.InicializadorVariableContext variable) {
            lista = variable.inicializadorArreglo();
        }
        return lista == null ? null : new ArrayList<>(lista.inicializadorVariable());
    }

    private Valor inicializar(Tipo destino, ParserRuleContext valor, List<Valor> tamanos) {
        return inicializar(destino, valor, tamanos, TraductorZetariano::elementos,
                v -> visit(((ZetarianoParser.InicializadorVariableContext) v).expresion()), valor);
    }

    // El objeto actual (parametro 0 de los constructores y metodos)
    private Valor este() {
        Simbolo simbolo = buscarVariable(THIS);
        return simbolo == null ? Valor.error() : cargar(direccionDe(simbolo));
    }

    // this visto como su clase padre (para super.x y super.m())
    private Valor comoPadre(Token token) {
        TipoDefinido padre = tablas.tipos().padre(claseActual);
        if (padre == null || !padre.esClase()) {
            error(token, "super", "La clase '" + claseActual.getNombre() + "' no hereda de otra clase: no se puede usar super");
            return Valor.error();
        }
        Valor objeto = este();
        return objeto.esError() ? objeto : new Valor(objeto.direccion(), Tipo.clase(padre.getNombre()));
    }

    // Abre el marco de un constructor o metodo con this y sus parametros
    private void abrirMarco(String nombre, String etiqueta, Tipo retorno, ZetarianoParser.ParametrosContext parametros,
            Token posicion) {
        entrarMarco(claseActual.getNombre() + "." + nombre);
        inicioFuncion(etiqueta, retorno);
        Simbolo este = Simbolo.variable(THIS, CategoriaSimbolo.PARAMETRO, Tipo.clase(claseActual.getNombre()),
                tablas.simbolos().getActual().getNombre(), errores.getArchivo(), Posicion.de(posicion),
                tablas.simbolos().getActual().reservar());
        tablas.simbolos().getActual().declarar(este);
        if (parametros != null) {
            for (ZetarianoParser.ParametroContext parametro : parametros.parametro()) {
                Tipo tipo = conocido(DeclaracionesZetariano.tipoDe(parametro.tipo()), parametro.tipo().getStart());
                declararVariable(parametro.IDENTIFICADOR().getSymbol(), tipo, CategoriaSimbolo.PARAMETRO);
            }
        }
    }

    private void cerrarMarco() {
        finFuncion(false);
        salirAmbito();
    }

    // El simbolo que registro el primer recorrido para este constructor/metodo (por su posicion)
    private static Simbolo simboloEn(List<Simbolo> opciones, Token nombre) {
        Posicion posicion = Posicion.de(nombre);
        for (Simbolo simbolo : opciones) {
            if (simbolo.posicion().equals(posicion)) {
                return simbolo;
            }
        }
        return null;
    }

    // Llamada al constructor vacio del padre cuando el constructor no escribe super(...)
    private void superImplicito(Token donde) {
        TipoDefinido padre = tablas.tipos().padre(claseActual);
        if (padre == null || !padre.esClase()) {
            return;
        }
        List<Simbolo> constructores = tablas.simbolos().constructoresDe(padre.getNombre());
        Simbolo vacio = null;
        for (Simbolo constructor : constructores) {
            if (constructor.parametros().isEmpty()) {
                vacio = constructor;
            }
        }
        if (!constructores.isEmpty() && vacio == null) {
            error(donde, claseActual.getNombre(), "La clase padre '" + padre.getNombre()
                    + "' no tiene un constructor vacío: el constructor debe llamar a super(...) como primera instrucción");
            return;
        }
        if (vacio != null && !accesible(vacio.visibilidad(), padre.getNombre())) {
            error(donde, claseActual.getNombre(), "El constructor vacío de la clase padre '" + padre.getNombre()
                    + "' es " + vacio.visibilidad() + ": el constructor debe llamar a otro con super(...)");
            return;
        }
        llamarConstructor(padre, vacio, este(), List.of());
    }

    private void llamarInit() {
        llamar(claseActual.getNombre() + "_init", VOID, List.of(este()));
    }

    // Destino de una asignacion o incremento: variable, atributo, super.atributo o elemento de arreglo; null si no lo es
    private Direccion direccion(ZetarianoParser.ExpresionContext contexto) {
        if (contexto instanceof ZetarianoParser.ExpresionPrimariaContext primaria) {
            if (primaria.primario() instanceof ZetarianoParser.PrimarioIdentificadorContext identificador) {
                return direccionNombre(identificador.IDENTIFICADOR().getSymbol());
            }
            if (primaria.primario() instanceof ZetarianoParser.PrimarioSuperAtributoContext atributo) {
                return direccionCampo(comoPadre(atributo.SUPER().getSymbol()), atributo.IDENTIFICADOR().getText(),
                        atributo.IDENTIFICADOR().getSymbol());
            }
            return null;
        }
        if (contexto instanceof ZetarianoParser.ExpresionAtributoContext atributo) {
            return direccionCampo(visit(atributo.expresion()), atributo.IDENTIFICADOR().getText(),
                    atributo.IDENTIFICADOR().getSymbol());
        }
        if (contexto instanceof ZetarianoParser.ExpresionIndiceContext) {
            // a[i][j] llega como Indice(Indice(a, i), j)
            List<ZetarianoParser.ExpresionContext> indices = new ArrayList<>();
            ZetarianoParser.ExpresionContext actual = contexto;
            while (actual instanceof ZetarianoParser.ExpresionIndiceContext indice) {
                indices.add(0, indice.expresion(1));
                actual = indice.expresion(0);
            }
            Valor arreglo = visit(actual);
            List<Valor> valores = new ArrayList<>();
            indices.forEach(indice -> valores.add(visit(indice)));
            return direccionElemento(arreglo, valores, contexto);
        }
        return null;
    }

    // Un nombre suelto: variable local/parametro, o atributo (propio o heredado) de this
    private Direccion direccionNombre(Token nombre) {
        Simbolo variable = buscarVariable(nombre.getText());
        if (variable != null) {
            return direccionDe(variable);
        }
        Campo campo = tablas.tipos().buscarCampo(claseActual, nombre.getText());
        if (campo != null) {
            return direccionCampo(este(), nombre.getText(), nombre);
        }
        error(nombre, nombre.getText(), "La variable '" + nombre.getText()
                + "' no está declarada (o se usa antes de declararla) ni es un atributo de la clase '"
                + claseActual.getNombre() + "'");
        return Direccion.error();
    }

    private Direccion destino(ZetarianoParser.ExpresionContext contexto, ParserRuleContext operacion, String simbolo) {
        Direccion direccion = direccion(contexto);
        if (direccion == null) {
            error(operacion, simbolo, "Solo se puede asignar a una variable, un atributo o una posición de un arreglo");
            visit(contexto);
            return Direccion.error();
        }
        return direccion;
    }

    // Condicion como saltos: && y || en corto circuito, comparaciones directo al if
    private void condicion(ZetarianoParser.ExpresionContext contexto, String verdadero, String falso) {
        if (contexto instanceof ZetarianoParser.ExpresionYContext y) {
            String siguiente = etiqueta();
            condicion(y.expresion(0), siguiente, falso);
            colocar(siguiente);
            condicion(y.expresion(1), verdadero, falso);
        } else if (contexto instanceof ZetarianoParser.ExpresionOContext o) {
            String siguiente = etiqueta();
            condicion(o.expresion(0), verdadero, siguiente);
            colocar(siguiente);
            condicion(o.expresion(1), verdadero, falso);
        } else if (contexto instanceof ZetarianoParser.ExpresionUnariaContext unaria && unaria.NO_LOGICO() != null) {
            condicion(unaria.expresion(), falso, verdadero);
        } else if (contexto instanceof ZetarianoParser.ExpresionPrimariaContext primaria
                && primaria.primario() instanceof ZetarianoParser.PrimarioParentesisContext parentesis) {
            condicion(parentesis.expresion(), verdadero, falso);
        } else if (contexto instanceof ZetarianoParser.ExpresionRelacionalContext relacional) {
            comparar(OperadorBinario.desdeTexto(relacional.operador.getText()), visit(relacional.expresion(0)),
                    visit(relacional.expresion(1)), verdadero, falso, relacional);
        } else if (contexto instanceof ZetarianoParser.ExpresionIgualdadContext igualdad) {
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

    // ==================================================================
    //                          Clase
    // ==================================================================

    @Override
    public Valor visitCompilacion(ZetarianoParser.CompilacionContext contexto) {
        return visit(contexto.declaracionClase());
    }

    @Override
    public Valor visitDeclaracionClase(ZetarianoParser.DeclaracionClaseContext contexto) {
        TipoDefinido clase = tablas.tipos().buscar(contexto.IDENTIFICADOR(0).getText());
        if (clase == null || !errores.getArchivo().equals(clase.getArchivo())) {
            return null;    // no se registro (nombre repetido): ya se reporto
        }
        claseActual = clase;
        verificarHerencia(contexto);

        // valores iniciales de los atributos propios
        List<ZetarianoParser.DeclaracionAtributoContext> atributos = new ArrayList<>();
        List<ZetarianoParser.DeclaracionConstructorContext> constructores = new ArrayList<>();
        List<ZetarianoParser.DeclaracionMetodoContext> metodos = new ArrayList<>();
        for (ZetarianoParser.MiembroContext miembro : contexto.miembro()) {
            if (miembro.declaracionAtributo() != null) {
                atributos.add(miembro.declaracionAtributo());
            } else if (miembro.declaracionConstructor() != null) {
                constructores.add(miembro.declaracionConstructor());
            } else if (miembro.declaracionMetodo() != null) {
                metodos.add(miembro.declaracionMetodo());
            }
        }
        abrirMarco("init", clase.getNombre() + "_init", VOID, null, contexto.IDENTIFICADOR(0).getSymbol());
        atributos.forEach(this::visit);
        cerrarMarco();

        constructores.forEach(this::visit);
        if (constructores.isEmpty()) {
            constructorPorDefecto(contexto.IDENTIFICADOR(0).getSymbol());
        }
        metodos.forEach(this::visit);
        claseActual = null;
        return null;
    }

    // El padre existe, es una clase y no hay ciclos
    private void verificarHerencia(ZetarianoParser.DeclaracionClaseContext contexto) {
        if (claseActual.getPadre() == null) {
            return;
        }
        Token nombrePadre = contexto.IDENTIFICADOR(1).getSymbol();
        TipoDefinido padre = tablas.tipos().padre(claseActual);
        if (padre == null) {
            error(nombrePadre, nombrePadre.getText(), "La clase padre '" + nombrePadre.getText() + "' no está definida");
        } else if (!padre.esClase()) {
            error(nombrePadre, nombrePadre.getText(), "'" + nombrePadre.getText()
                    + "' no es una clase: solo se puede heredar de otra clase");
        } else if (tablas.tipos().tieneCiclo(claseActual)) {
            error(nombrePadre, nombrePadre.getText(), "Herencia circular: la clase '" + claseActual.getNombre()
                    + "' termina heredando de sí misma");
        }
    }

    // Sin constructores declarados: super() del padre y los valores iniciales de los atributos
    private void constructorPorDefecto(Token clase) {
        abrirMarco(claseActual.getNombre(), claseActual.getNombre() + "_constructor", VOID, null, clase);
        superImplicito(clase);
        llamarInit();
        cerrarMarco();
    }

    @Override
    public Valor visitModificador(ZetarianoParser.ModificadorContext contexto) {
        return null;
    }

    @Override
    public Valor visitMiembro(ZetarianoParser.MiembroContext contexto) {
        return visit(contexto.getChild(0));
    }

    @Override
    public Valor visitDeclaracionAtributo(ZetarianoParser.DeclaracionAtributoContext contexto) {
        Tipo tipo = conocido(DeclaracionesZetariano.tipoDe(contexto.tipo()), contexto.tipo().getStart());
        for (ZetarianoParser.DeclaradorVariableContext declarador : contexto.declaradorVariable()) {
            if (declarador.inicializadorVariable() == null) {
                continue;
            }
            Valor valor = inicializar(tipo, declarador.inicializadorVariable(), List.of());
            Token nombre = declarador.IDENTIFICADOR().getSymbol();
            escribir(direccionCampo(este(), nombre.getText(), nombre), valor);
        }
        return null;
    }

    @Override
    public Valor visitDeclaracionConstructor(ZetarianoParser.DeclaracionConstructorContext contexto) {
        Token nombre = contexto.IDENTIFICADOR().getSymbol();
        Simbolo constructor = simboloEn(tablas.simbolos().constructoresDe(claseActual.getNombre()), nombre);
        String etiqueta = constructor != null ? constructor.etiqueta() : claseActual.getNombre() + "_constructor_invalido";
        abrirMarco(claseActual.getNombre(), etiqueta, VOID, contexto.parametros(), nombre);
        ZetarianoParser.CuerpoConstructorContext cuerpo = contexto.cuerpoConstructor();
        if (cuerpo.llamadaSuper() != null) {
            visit(cuerpo.llamadaSuper());
        } else {
            superImplicito(nombre);
        }
        llamarInit();
        cuerpo.sentencia().forEach(this::visit);
        cerrarMarco();
        return null;
    }

    @Override
    public Valor visitCuerpoConstructor(ZetarianoParser.CuerpoConstructorContext contexto) {
        return null;
    }

    // super(argumentos): constructor del padre sobre este mismo objeto
    @Override
    public Valor visitLlamadaSuper(ZetarianoParser.LlamadaSuperContext contexto) {
        Token token = contexto.SUPER().getSymbol();
        List<Valor> valores = argumentos(contexto.argumentos());
        TipoDefinido padre = tablas.tipos().padre(claseActual);
        if (padre == null || !padre.esClase()) {
            error(token, "super", "La clase '" + claseActual.getNombre()
                    + "' no hereda de otra clase: no puede llamar a super(...)");
            return null;
        }
        if (valores.stream().anyMatch(Valor::esError)) {
            return null;
        }
        boolean[] error = {false};
        Simbolo constructor = elegirConstructor(padre, valores, token, error);
        if (!error[0]) {
            llamarConstructor(padre, constructor, este(), valores);
        }
        return null;
    }

    @Override
    public Valor visitDeclaracionMetodo(ZetarianoParser.DeclaracionMetodoContext contexto) {
        Token nombre = contexto.IDENTIFICADOR().getSymbol();
        Simbolo metodo = simboloEn(tablas.simbolos().metodosPropios(claseActual.getNombre(), nombre.getText()), nombre);
        Tipo retorno = DeclaracionesZetariano.tipoRetorno(contexto.tipoRetorno());
        retorno = conocido(retorno, contexto.tipoRetorno().getStart());
        if (metodo != null) {
            verificarSobrescritura(metodo, contexto, nombre);
        }
        String etiqueta = metodo != null ? metodo.etiqueta() : claseActual.getNombre() + "_" + nombre.getText()
                + "_invalido";
        abrirMarco(nombre.getText(), etiqueta, retorno, contexto.parametros(), nombre);
        // el cuerpo comparte el ambito de los parametros: una variable no puede llamarse como un parametro
        contexto.bloque().sentencia().forEach(this::visit);
        cerrarMarco();
        return null;
    }

    // @Override es obligatorio al sobrescribir y es un error si no sobrescribe nada. El retorno puede ser
    // covariante (una subclase) y la visibilidad no se puede reducir.
    private void verificarSobrescritura(Simbolo metodo, ZetarianoParser.DeclaracionMetodoContext contexto,
            Token nombre) {
        Simbolo heredado = tablas.metodoSobrescrito(claseActual, metodo);
        if (heredado == null) {
            if (metodo.sobrescribe()) {
                error(contexto.OVERRIDE().getSymbol(), "@Override", "El método " + describirParametros(metodo)
                        + " tiene @Override pero no sobrescribe ningún método public o protected de una clase padre");
            }
            return;
        }
        String padre = heredado.clase();
        if (!metodo.sobrescribe()) {
            error(nombre, nombre.getText(), "El método " + describirParametros(metodo) + " sobrescribe al de la clase '"
                    + padre + "': debe llevar la anotación @Override");
        }
        Tipo propio = metodo.tipo();
        Tipo delPadre = heredado.tipo();
        boolean covariante = propio.esObjeto() && delPadre.esObjeto()
                && tablas.tipos().esSubclase(propio.nombreClase(), delPadre.nombreClase());
        if (!propio.equals(delPadre) && !covariante && !propio.esError() && !delPadre.esError()) {
            error(contexto.tipoRetorno(), nombre.getText(), "El método " + describirParametros(metodo)
                    + " debe retornar '" + nombre(delPadre) + "' (o una subclase) como el de la clase '" + padre + "'");
        }
        if (metodo.visibilidad().getNivel() < heredado.visibilidad().getNivel()) {
            error(nombre, nombre.getText(), "El método " + describirParametros(metodo)
                    + " no puede reducir la visibilidad: en la clase '" + padre + "' es " + heredado.visibilidad());
        }
    }

    @Override
    public Valor visitTipoRetorno(ZetarianoParser.TipoRetornoContext contexto) {
        return null;
    }

    @Override
    public Valor visitParametros(ZetarianoParser.ParametrosContext contexto) {
        return null;
    }

    @Override
    public Valor visitParametro(ZetarianoParser.ParametroContext contexto) {
        return null;
    }

    @Override
    public Valor visitTipo(ZetarianoParser.TipoContext contexto) {
        return null;
    }

    @Override
    public Valor visitTipoBase(ZetarianoParser.TipoBaseContext contexto) {
        return null;
    }

    @Override
    public Valor visitDeclaradorVariable(ZetarianoParser.DeclaradorVariableContext contexto) {
        return null;
    }

    @Override
    public Valor visitInicializadorVariable(ZetarianoParser.InicializadorVariableContext contexto) {
        return null;
    }

    @Override
    public Valor visitInicializadorArreglo(ZetarianoParser.InicializadorArregloContext contexto) {
        error(contexto, "{", "Una lista { } solo se puede usar para inicializar un arreglo");
        return Valor.error();
    }

    // ==================================================================
    //                          Sentencias
    // ==================================================================

    @Override
    public Valor visitBloque(ZetarianoParser.BloqueContext contexto) {
        entrarBloque("bloque");
        contexto.sentencia().forEach(this::visit);
        salirAmbito();
        return null;
    }

    @Override
    public Valor visitSentenciaBloque(ZetarianoParser.SentenciaBloqueContext contexto) {
        return visit(contexto.bloque());
    }

    @Override
    public Valor visitSentenciaDeclaracion(ZetarianoParser.SentenciaDeclaracionContext contexto) {
        return visit(contexto.declaracionLocal());
    }

    @Override
    public Valor visitDeclaracionLocal(ZetarianoParser.DeclaracionLocalContext contexto) {
        Tipo tipo = conocido(DeclaracionesZetariano.tipoDe(contexto.tipo()), contexto.tipo().getStart());
        for (ZetarianoParser.DeclaradorVariableContext declarador : contexto.declaradorVariable()) {
            Valor valor = declarador.inicializadorVariable() != null
                    ? inicializar(tipo, declarador.inicializadorVariable(), List.of())
                    : valorPorDefecto(tipo, List.of(), declarador);
            Simbolo variable = declararVariable(declarador.IDENTIFICADOR().getSymbol(), tipo, CategoriaSimbolo.VARIABLE);
            escribir(direccionDe(variable), valor);
        }
        return null;
    }

    @Override
    public Valor visitSentenciaIf(ZetarianoParser.SentenciaIfContext contexto) {
        String entonces = etiqueta();
        String sino = etiqueta();
        String fin = etiqueta();
        condicion(contexto.expresion(), entonces, sino);
        colocar(entonces);
        visitarEnBloque(contexto.sentencia(0), "if");
        saltar(fin);
        colocar(sino);
        if (contexto.sentencia().size() > 1) {
            visitarEnBloque(contexto.sentencia(1), "else");
        }
        colocar(fin);
        return null;
    }

    // Una sentencia sola (sin llaves) tambien tiene su propio ambito
    private void visitarEnBloque(ZetarianoParser.SentenciaContext sentencia, String nombre) {
        entrarBloque(nombre);
        visit(sentencia);
        salirAmbito();
    }

    // Se compara el valor con cada case y se salta al primero que coincide
    @Override
    public Valor visitSentenciaSwitch(ZetarianoParser.SentenciaSwitchContext contexto) {
        Valor valor = visit(contexto.expresion());
        String fin = etiqueta();
        String porDefecto = null;
        List<String> etiquetas = new ArrayList<>();
        for (ZetarianoParser.SeccionSwitchContext seccion : contexto.seccionSwitch()) {
            String etiqueta = etiqueta();
            etiquetas.add(etiqueta);
            for (ZetarianoParser.EtiquetaSwitchContext caso : seccion.etiquetaSwitch()) {
                if (caso.DEFAULT() != null) {
                    if (porDefecto != null) {
                        error(caso, "default", "El switch ya tiene un default");
                    }
                    porDefecto = etiqueta;
                } else {
                    comparar(OperadorBinario.IGUAL_QUE, valor, visit(caso.expresion()), etiqueta, null, caso);
                }
            }
        }
        saltar(porDefecto != null ? porDefecto : fin);
        enSeleccion(fin, () -> {
            for (int i = 0; i < contexto.seccionSwitch().size(); i++) {
                colocar(etiquetas.get(i));
                entrarBloque("case");
                contexto.seccionSwitch(i).sentencia().forEach(this::visit);
                salirAmbito();
            }
        });
        colocar(fin);
        return null;
    }

    @Override
    public Valor visitSeccionSwitch(ZetarianoParser.SeccionSwitchContext contexto) {
        return null;
    }

    @Override
    public Valor visitEtiquetaSwitch(ZetarianoParser.EtiquetaSwitchContext contexto) {
        return null;
    }

    @Override
    public Valor visitSentenciaFor(ZetarianoParser.SentenciaForContext contexto) {
        // la variable declarada en el for solo existe dentro del ciclo
        entrarBloque("for");
        if (contexto.inicioFor() != null) {
            visit(contexto.inicioFor());
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
        enCiclo(fin, actualizacion, () -> visitarEnBloque(contexto.sentencia(), "cuerpo"));
        colocar(actualizacion);
        if (contexto.listaExpresiones() != null) {
            visit(contexto.listaExpresiones());
        }
        saltar(evaluar);
        colocar(fin);
        salirAmbito();
        return null;
    }

    @Override
    public Valor visitInicioFor(ZetarianoParser.InicioForContext contexto) {
        return visit(contexto.getChild(0));
    }

    @Override
    public Valor visitListaExpresiones(ZetarianoParser.ListaExpresionesContext contexto) {
        for (ZetarianoParser.ExpresionContext expresion : contexto.expresion()) {
            verificarInstruccion(expresion);
            visit(expresion);
        }
        return null;
    }

    @Override
    public Valor visitSentenciaWhile(ZetarianoParser.SentenciaWhileContext contexto) {
        String inicio = etiqueta();
        String cuerpo = etiqueta();
        String fin = etiqueta();
        colocar(inicio);
        condicion(contexto.expresion(), cuerpo, fin);
        colocar(cuerpo);
        enCiclo(fin, inicio, () -> visitarEnBloque(contexto.sentencia(), "while"));
        saltar(inicio);
        colocar(fin);
        return null;
    }

    @Override
    public Valor visitSentenciaDoWhile(ZetarianoParser.SentenciaDoWhileContext contexto) {
        String inicio = etiqueta();
        String evaluar = etiqueta();
        String fin = etiqueta();
        colocar(inicio);
        enCiclo(fin, evaluar, () -> visitarEnBloque(contexto.sentencia(), "do"));
        colocar(evaluar);
        condicion(contexto.expresion(), inicio, fin);
        colocar(fin);
        return null;
    }

    @Override
    public Valor visitSentenciaBreak(ZetarianoParser.SentenciaBreakContext contexto) {
        romper(contexto, "break", "un ciclo o de un switch");
        return null;
    }

    @Override
    public Valor visitSentenciaContinue(ZetarianoParser.SentenciaContinueContext contexto) {
        continuar(contexto, "continue");
        return null;
    }

    @Override
    public Valor visitSentenciaReturn(ZetarianoParser.SentenciaReturnContext contexto) {
        retornar(contexto.expresion() != null ? visit(contexto.expresion()) : null, contexto);
        return null;
    }

    @Override
    public Valor visitSentenciaPrintln(ZetarianoParser.SentenciaPrintlnContext contexto) {
        if (contexto.expresion() != null) {
            imprimir(visit(contexto.expresion()), contexto.expresion());
        }
        saltoDeLinea();
        return null;
    }

    @Override
    public Valor visitSentenciaPrint(ZetarianoParser.SentenciaPrintContext contexto) {
        imprimir(visit(contexto.expresion()), contexto.expresion());
        return null;
    }

    @Override
    public Valor visitSentenciaExpresion(ZetarianoParser.SentenciaExpresionContext contexto) {
        verificarInstruccion(contexto.expresion());
        visit(contexto.expresion());
        return null;
    }

    // Como en Java, solo las asignaciones, incrementos, llamadas, new y readln() pueden ir solas
    private void verificarInstruccion(ZetarianoParser.ExpresionContext expresion) {
        boolean valida = expresion instanceof ZetarianoParser.ExpresionAsignacionContext
                || expresion instanceof ZetarianoParser.ExpresionPostfijaContext
                || expresion instanceof ZetarianoParser.ExpresionLlamadaMetodoContext
                || (expresion instanceof ZetarianoParser.ExpresionUnariaContext unaria
                        && (unaria.INCREMENTO() != null || unaria.DECREMENTO() != null))
                || (expresion instanceof ZetarianoParser.ExpresionPrimariaContext primaria
                        && (primaria.primario() instanceof ZetarianoParser.PrimarioLlamadaContext
                        || primaria.primario() instanceof ZetarianoParser.PrimarioSuperMetodoContext
                        || primaria.primario() instanceof ZetarianoParser.PrimarioNuevoObjetoContext
                        || primaria.primario() instanceof ZetarianoParser.PrimarioReadlnContext));
        if (!valida) {
            error(expresion, null, "Esta expresión no es una instrucción: solo pueden ir solas las asignaciones, "
                    + "incrementos, llamadas y creaciones de objetos");
        }
    }

    @Override
    public Valor visitSentenciaVacia(ZetarianoParser.SentenciaVaciaContext contexto) {
        return null;
    }

    // ==================================================================
    //                          Expresiones
    // ==================================================================

    @Override
    public Valor visitExpresionPrimaria(ZetarianoParser.ExpresionPrimariaContext contexto) {
        return visit(contexto.primario());
    }

    @Override
    public Valor visitExpresionAtributo(ZetarianoParser.ExpresionAtributoContext contexto) {
        return cargar(direccion(contexto));
    }

    @Override
    public Valor visitExpresionIndice(ZetarianoParser.ExpresionIndiceContext contexto) {
        return cargar(direccion(contexto));
    }

    @Override
    public Valor visitExpresionLlamadaMetodo(ZetarianoParser.ExpresionLlamadaMetodoContext contexto) {
        Valor objeto = visit(contexto.expresion());
        List<Valor> valores = argumentos(contexto.argumentos());
        return llamarMetodo(objeto, contexto.IDENTIFICADOR().getText(), valores, contexto.IDENTIFICADOR().getSymbol(),
                false);
    }

    @Override
    public Valor visitExpresionPostfija(ZetarianoParser.ExpresionPostfijaContext contexto) {
        boolean incremento = contexto.INCREMENTO() != null;
        String simbolo = incremento ? "++" : "--";
        return incrementar(destino(contexto.expresion(), contexto, simbolo), incremento, true, contexto, simbolo);
    }

    @Override
    public Valor visitExpresionUnaria(ZetarianoParser.ExpresionUnariaContext contexto) {
        if (contexto.INCREMENTO() != null || contexto.DECREMENTO() != null) {
            boolean incremento = contexto.INCREMENTO() != null;
            String simbolo = incremento ? "++" : "--";
            return incrementar(destino(contexto.expresion(), contexto, simbolo), incremento, false, contexto, simbolo);
        }
        Valor operando = visit(contexto.expresion());
        String simbolo = contexto.operador.getText();
        OperadorUnario operador = switch (simbolo) {
            case "-" -> OperadorUnario.NEGATIVO;
            case "+" -> OperadorUnario.POSITIVO;
            default -> OperadorUnario.NEGACION;
        };
        return unaria(operador, operando, contexto, simbolo);
    }

    private Valor binaria(ZetarianoParser.ExpresionContext izquierda, Token operador,
            ZetarianoParser.ExpresionContext derecha, ZetarianoParser.ExpresionContext contexto) {
        Valor a = visit(izquierda);
        Valor b = visit(derecha);
        return binaria(OperadorBinario.desdeTexto(operador.getText()), a, b, contexto);
    }

    @Override
    public Valor visitExpresionMultiplicativa(ZetarianoParser.ExpresionMultiplicativaContext contexto) {
        return binaria(contexto.expresion(0), contexto.operador, contexto.expresion(1), contexto);
    }

    @Override
    public Valor visitExpresionAditiva(ZetarianoParser.ExpresionAditivaContext contexto) {
        return binaria(contexto.expresion(0), contexto.operador, contexto.expresion(1), contexto);
    }

    @Override
    public Valor visitExpresionRelacional(ZetarianoParser.ExpresionRelacionalContext contexto) {
        return binaria(contexto.expresion(0), contexto.operador, contexto.expresion(1), contexto);
    }

    @Override
    public Valor visitExpresionIgualdad(ZetarianoParser.ExpresionIgualdadContext contexto) {
        return binaria(contexto.expresion(0), contexto.operador, contexto.expresion(1), contexto);
    }

    @Override
    public Valor visitExpresionY(ZetarianoParser.ExpresionYContext contexto) {
        return valorDeCondicion((verdadero, falso) -> condicion(contexto, verdadero, falso));
    }

    @Override
    public Valor visitExpresionO(ZetarianoParser.ExpresionOContext contexto) {
        return valorDeCondicion((verdadero, falso) -> condicion(contexto, verdadero, falso));
    }

    // c ? a : b. El tipo es el mas general de los dos; si la primera opcion es entera y la segunda
    // decimal, su asignacion se cambia despues por una conversion.
    @Override
    public Valor visitExpresionTernaria(ZetarianoParser.ExpresionTernariaContext contexto) {
        String resultado = temporal();
        String entonces = etiqueta();
        String sino = etiqueta();
        String fin = etiqueta();
        condicion(contexto.expresion(0), entonces, sino);
        colocar(entonces);
        Valor verdadero = visit(contexto.expresion(1));
        int asignacionVerdadero = codigo.tamano();
        emitir("=", verdadero.direccion(), "", resultado);
        saltar(fin);
        colocar(sino);
        Valor falso = visit(contexto.expresion(2));
        if (verdadero.esError() || falso.esError()) {
            colocar(fin);
            return Valor.error();
        }
        Tipo tipo;
        if (esAsignable(verdadero.tipo(), falso.tipo()) && !verdadero.tipo().equals(Tipo.NULO)) {
            tipo = verdadero.tipo();
        } else if (esAsignable(falso.tipo(), verdadero.tipo())) {
            tipo = falso.tipo();
        } else {
            error(contexto, "?", "Las dos opciones del operador ternario deben ser de tipos compatibles ('"
                    + nombre(verdadero.tipo()) + "' y '" + nombre(falso.tipo()) + "')");
            colocar(fin);
            return Valor.error();
        }
        emitir("=", convertir(falso, tipo).direccion(), "", resultado);
        if (tipo.equals(DECIMAL) && !verdadero.tipo().equals(DECIMAL)) {
            codigo.reemplazar(asignacionVerdadero, new Cuarteta("(double)", verdadero.direccion(), "", resultado));
        }
        colocar(fin);
        return new Valor(resultado, tipo);
    }

    @Override
    public Valor visitExpresionAsignacion(ZetarianoParser.ExpresionAsignacionContext contexto) {
        String simbolo = contexto.operador.getText();
        Direccion destino = destino(contexto.expresion(0), contexto, simbolo);
        Valor valor;
        if (simbolo.equals("=")) {
            valor = inicializar(destino.tipo(), contexto.expresion(1), List.of(), e -> null, this::visit, contexto);
            escribir(destino, valor);
        } else {
            // a += b  ->  a = a + b
            OperadorBinario operador = OperadorBinario.desdeTexto(simbolo.substring(0, 1));
            Valor actual = cargar(destino);
            valor = binaria(operador, actual, visit(contexto.expresion(1)), contexto);
            if (!destino.esError() && !valor.esError() && verificarAsignable(destino.tipo(), valor, contexto)) {
                valor = convertir(valor, destino.tipo());
                escribir(destino, valor);
            }
        }
        return destino.esError() || valor.esError() ? Valor.error() : new Valor(valor.direccion(), destino.tipo());
    }

    @Override
    public Valor visitPrimarioParentesis(ZetarianoParser.PrimarioParentesisContext contexto) {
        return visit(contexto.expresion());
    }

    @Override
    public Valor visitPrimarioLiteral(ZetarianoParser.PrimarioLiteralContext contexto) {
        return visit(contexto.literal());
    }

    @Override
    public Valor visitPrimarioThis(ZetarianoParser.PrimarioThisContext contexto) {
        return este();
    }

    @Override
    public Valor visitPrimarioSuperMetodo(ZetarianoParser.PrimarioSuperMetodoContext contexto) {
        Valor padre = comoPadre(contexto.SUPER().getSymbol());
        List<Valor> valores = argumentos(contexto.argumentos());
        // super.m() llama a la version del padre: sin despacho dinamico
        return llamarMetodo(padre, contexto.IDENTIFICADOR().getText(), valores, contexto.IDENTIFICADOR().getSymbol(),
                true);
    }

    @Override
    public Valor visitPrimarioSuperAtributo(ZetarianoParser.PrimarioSuperAtributoContext contexto) {
        return cargar(direccionCampo(comoPadre(contexto.SUPER().getSymbol()), contexto.IDENTIFICADOR().getText(),
                contexto.IDENTIFICADOR().getSymbol()));
    }

    // m(args): metodo de la clase (propio o heredado); si no hay, una funcion de un .y
    @Override
    public Valor visitPrimarioLlamada(ZetarianoParser.PrimarioLlamadaContext contexto) {
        Token nombre = contexto.IDENTIFICADOR().getSymbol();
        List<Valor> valores = argumentos(contexto.argumentos());
        boolean esMetodo = !tablas.buscarMetodos(claseActual.getNombre(), nombre.getText()).isEmpty();
        if (!esMetodo && tablas.simbolos().buscarFuncion(nombre.getText()) != null) {
            return llamarFuncion(nombre.getText(), valores, nombre);
        }
        if (!esMetodo && !tieneMetodoPrivadoHeredado(nombre.getText())) {
            error(nombre, nombre.getText(), "No existe un método '" + nombre.getText() + "' en la clase '"
                    + claseActual.getNombre() + "' ni una función '" + nombre.getText() + "' en los archivos .y");
            return Valor.error();
        }
        return llamarMetodo(este(), nombre.getText(), valores, nombre, false);
    }

    private boolean tieneMetodoPrivadoHeredado(String nombre) {
        for (TipoDefinido ancestro : tablas.tipos().linaje(claseActual)) {
            if (!tablas.simbolos().metodosPropios(ancestro.getNombre(), nombre).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Valor visitPrimarioIdentificador(ZetarianoParser.PrimarioIdentificadorContext contexto) {
        return cargar(direccionNombre(contexto.IDENTIFICADOR().getSymbol()));
    }

    @Override
    public Valor visitPrimarioNuevoObjeto(ZetarianoParser.PrimarioNuevoObjetoContext contexto) {
        List<Valor> valores = argumentos(contexto.argumentos());
        return crearObjeto(contexto.IDENTIFICADOR().getText(), valores, contexto.IDENTIFICADOR().getSymbol());
    }

    // new int[a][b]: se debe dar el tamano de todas las dimensiones
    @Override
    public Valor visitPrimarioNuevoArreglo(ZetarianoParser.PrimarioNuevoArregloContext contexto) {
        int dimensiones = contexto.CORCHETE_IZQUIERDO().size();
        Tipo tipo = conocido(DeclaracionesZetariano.tipoBase(contexto.tipoBase()), contexto.tipoBase().getStart());
        List<Valor> tamanos = new ArrayList<>();
        contexto.expresion().forEach(expresion -> tamanos.add(visit(expresion)));
        if (tipo.esError()) {
            return Valor.error();
        }
        if (tamanos.size() != dimensiones) {
            error(contexto, "new", "Se debe indicar el tamaño de cada dimensión del arreglo");
            return new Valor("0", tipo.conDimensiones(dimensiones));
        }
        return nuevoArreglo(tipo.conDimensiones(dimensiones), tamanos, contexto);
    }

    @Override
    public Valor visitPrimarioNuevoArregloInicializado(ZetarianoParser.PrimarioNuevoArregloInicializadoContext contexto) {
        Tipo tipo = conocido(DeclaracionesZetariano.tipoBase(contexto.tipoBase()), contexto.tipoBase().getStart());
        tipo = tipo.esError() ? tipo : tipo.conDimensiones(contexto.CORCHETE_IZQUIERDO().size());
        return inicializar(tipo, contexto.inicializadorArreglo(), List.of());
    }

    @Override
    public Valor visitPrimarioReadln(ZetarianoParser.PrimarioReadlnContext contexto) {
        return leer();
    }

    @Override
    public Valor visitArgumentos(ZetarianoParser.ArgumentosContext contexto) {
        return null;
    }

    @Override
    public Valor visitLiteral(ZetarianoParser.LiteralContext contexto) {
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
        return booleano(contexto.TRUE() != null);
    }
}
