package com.mycompany.proyectodoscompi2.visitors.zetariano;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.Token;

import com.mycompany.ZetarianoBaseVisitor;
import com.mycompany.ZetarianoParser;
import com.mycompany.proyectodoscompi2.errores.ManejadorErrores;
import com.mycompany.proyectodoscompi2.errores.TipoError;
import com.mycompany.proyectodoscompi2.tablas.Campo;
import com.mycompany.proyectodoscompi2.tablas.CategoriaSimbolo;
import com.mycompany.proyectodoscompi2.tablas.Simbolo;
import com.mycompany.proyectodoscompi2.tablas.Tablas;
import com.mycompany.proyectodoscompi2.tablas.TipoDefinido;
import com.mycompany.proyectodoscompi2.tipos.Posicion;
import com.mycompany.proyectodoscompi2.tipos.Tipo;
import com.mycompany.proyectodoscompi2.tipos.Visibilidad;

// Primer recorrido de un .z: registra la clase con su padre, sus atributos y las firmas de sus
// constructores y metodos, sin entrar a los cuerpos. Asi todos los archivos pueden usar la clase
// antes de que se traduzca. Que los tipos existan se revisa en el TraductorZetariano.
public class DeclaracionesZetariano extends ZetarianoBaseVisitor<Void> {

    private final Tablas tablas;
    private final ManejadorErrores errores;
    private TipoDefinido clase;

    public DeclaracionesZetariano(Tablas tablas, ManejadorErrores errores) {
        this.tablas = tablas;
        this.errores = errores;
    }

    private void error(Token token, String lexema, String descripcion) {
        errores.agregar(TipoError.SEMANTICO, token.getLine(), token.getCharPositionInLine() + 1, lexema, descripcion);
    }

    // ---------- Tipos (tambien los usa el TraductorZetariano) ----------

    public static Tipo tipoDe(ZetarianoParser.TipoContext contexto) {
        return tipoBase(contexto.tipoBase()).conDimensiones(contexto.CORCHETE_IZQUIERDO().size());
    }

    public static Tipo tipoBase(ZetarianoParser.TipoBaseContext contexto) {
        if (contexto.INT() != null) {
            return Tipo.ENTERO;
        }
        if (contexto.DOUBLE() != null) {
            return Tipo.DECIMAL;
        }
        if (contexto.CHAR() != null) {
            return Tipo.CARACTER;
        }
        if (contexto.BOOLEAN() != null) {
            return Tipo.BOOLEANO;
        }
        if (contexto.STRING() != null) {
            return Tipo.CADENA;
        }
        return Tipo.clase(contexto.IDENTIFICADOR().getText());
    }

    public static Tipo tipoRetorno(ZetarianoParser.TipoRetornoContext contexto) {
        return contexto.VOID() != null ? Tipo.VOID : tipoDe(contexto.tipo());
    }

    public static List<Tipo> tiposParametros(ZetarianoParser.ParametrosContext contexto) {
        List<Tipo> tipos = new ArrayList<>();
        if (contexto != null) {
            contexto.parametro().forEach(parametro -> tipos.add(tipoDe(parametro.tipo())));
        }
        return tipos;
    }

    // Visibilidad de los modificadores; reporta repetidos y mas de uno de acceso
    private Visibilidad visibilidad(List<ZetarianoParser.ModificadorContext> modificadores) {
        Visibilidad visibilidad = Visibilidad.DEFAULT;
        Set<String> vistos = new HashSet<>();
        boolean conAcceso = false;
        for (ZetarianoParser.ModificadorContext modificador : modificadores) {
            String texto = modificador.getText();
            if (!vistos.add(texto)) {
                error(modificador.getStart(), texto, "El modificador '" + texto + "' está repetido");
                continue;
            }
            if (modificador.STATIC() != null) {
                continue;
            }
            if (conAcceso) {
                error(modificador.getStart(), texto, "Solo se puede usar un modificador de acceso "
                        + "(public, private o protected)");
                continue;
            }
            conAcceso = true;
            visibilidad = modificador.PUBLIC() != null ? Visibilidad.PUBLIC
                    : modificador.PRIVATE() != null ? Visibilidad.PRIVATE : Visibilidad.PROTECTED;
        }
        return visibilidad;
    }

    // ---------- Clase ----------

    @Override
    public Void visitDeclaracionClase(ZetarianoParser.DeclaracionClaseContext contexto) {
        Token nombre = contexto.IDENTIFICADOR(0).getSymbol();
        String padre = contexto.EXTENDS() != null && contexto.IDENTIFICADOR().size() > 1
                ? contexto.IDENTIFICADOR(1).getText() : null;
        for (ZetarianoParser.ModificadorContext modificador : contexto.modificador()) {
            if (modificador.PRIVATE() != null || modificador.PROTECTED() != null) {
                error(modificador.getStart(), modificador.getText(),
                        "Una clase solo puede ser public o no llevar modificador de acceso");
            }
        }
        visibilidad(contexto.modificador());

        String archivo = Path.of(errores.getArchivo()).getFileName().toString();
        String esperado = nombre.getText() + ".z";
        if (!archivo.equals(esperado)) {
            error(nombre, nombre.getText(), "La clase '" + nombre.getText() + "' debe estar en un archivo llamado '"
                    + esperado + "' (el archivo se llama '" + archivo + "')");
        }
        clase = new TipoDefinido(nombre.getText(), TipoDefinido.Categoria.CLASE, errores.getArchivo(),
                Posicion.de(nombre), padre);
        if (!tablas.tipos().agregar(clase)) {
            error(nombre, nombre.getText(), "Ya existe una estructura o clase llamada '" + nombre.getText() + "'");
            clase = null;
            return null;
        }
        contexto.miembro().forEach(this::visit);
        return null;
    }

    @Override
    public Void visitDeclaracionAtributo(ZetarianoParser.DeclaracionAtributoContext contexto) {
        Visibilidad visibilidad = visibilidad(contexto.modificador());
        Tipo tipo = tipoDe(contexto.tipo());
        for (ZetarianoParser.DeclaradorVariableContext declarador : contexto.declaradorVariable()) {
            Token nombre = declarador.IDENTIFICADOR().getSymbol();
            Campo campo = new Campo(nombre.getText(), tipo, List.of(), Posicion.de(nombre), visibilidad,
                    clase.getNombre());
            if (!clase.agregarCampo(campo)) {
                error(nombre, nombre.getText(), "La clase '" + clase.getNombre() + "' ya tiene un atributo '"
                        + nombre.getText() + "'");
            }
        }
        return null;
    }

    @Override
    public Void visitDeclaracionConstructor(ZetarianoParser.DeclaracionConstructorContext contexto) {
        Token nombre = contexto.IDENTIFICADOR().getSymbol();
        if (!nombre.getText().equals(clase.getNombre())) {
            error(nombre, nombre.getText(), "El constructor '" + nombre.getText()
                    + "' debe llamarse igual que la clase '" + clase.getNombre()
                    + "' (si es un método le falta el tipo de retorno)");
            return null;
        }
        Simbolo constructor = new Simbolo(clase.getNombre(), CategoriaSimbolo.CONSTRUCTOR, Tipo.VOID,
                tiposParametros(contexto.parametros()), clase.getNombre(), errores.getArchivo(), Posicion.de(nombre),
                -1, visibilidad(contexto.modificador()), clase.getNombre(), false);
        if (!tablas.simbolos().declararConstructor(constructor)) {
            error(nombre, nombre.getText(), "La clase '" + clase.getNombre()
                    + "' ya tiene un constructor con los mismos tipos de parámetros");
        }
        return null;
    }

    @Override
    public Void visitDeclaracionMetodo(ZetarianoParser.DeclaracionMetodoContext contexto) {
        Token nombre = contexto.IDENTIFICADOR().getSymbol();
        Simbolo metodo = new Simbolo(nombre.getText(), CategoriaSimbolo.METODO, tipoRetorno(contexto.tipoRetorno()),
                tiposParametros(contexto.parametros()), clase.getNombre(), errores.getArchivo(), Posicion.de(nombre),
                -1, visibilidad(contexto.modificador()), clase.getNombre(), contexto.OVERRIDE() != null);
        if (!tablas.simbolos().declararMetodo(metodo)) {
            error(nombre, nombre.getText(), "La clase '" + clase.getNombre() + "' ya tiene un método '"
                    + nombre.getText() + "' con los mismos tipos de parámetros");
        }
        return null;
    }
}
