package com.mycompany.proyectodoscompi2.visitors.y;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;

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
import com.mycompany.yBaseVisitor;
import com.mycompany.yParser;

// Primer recorrido de un .y , registra las estructuras y las firmas de las funciones,q ue los tipos existan
public class DeclaracionesY extends yBaseVisitor<Void> {

    private final Tablas tablas;
    private final ManejadorErrores errores;

    public DeclaracionesY(Tablas tablas, ManejadorErrores errores) {
        this.tablas = tablas;
        this.errores = errores;
    }

    private void error(Token token, String lexema, String descripcion) {
        errores.agregar(TipoError.SEMANTICO, token.getLine(), token.getCharPositionInLine() + 1, lexema, descripcion);
    }

    //Tipos

    public static Tipo tipoDe(yParser.TipoContext contexto) {
        if (contexto.ENTERO_T() != null) {
            return Tipo.ENTERO;
        }
        if (contexto.FLOTANTE_T() != null) {
            return Tipo.DECIMAL;
        }
        if (contexto.CADENA_T() != null) {
            return Tipo.CADENA;
        }
        if (contexto.CARACTER_T() != null) {
            return Tipo.CARACTER;
        }
        if (contexto.BOOL_T() != null) {
            return Tipo.BOOLEANO;
        }
        return Tipo.clase(contexto.IDENTIFICADOR().getText());
    }

    public static Tipo tipoDe(yParser.ParametroContext contexto) {
        if (contexto instanceof yParser.ParametroArregloContext arreglo) {
            return tipoDe(arreglo.tipo()).conDimensiones(arreglo.CORCHETE_IZQUIERDO().size());
        }
        if (contexto instanceof yParser.ParametroEstructuraContext estructura) {
            return Tipo.clase(estructura.IDENTIFICADOR(0).getText());
        }
        return tipoDe(((yParser.ParametroValorContext) contexto).tipo());
    }

    // Nombre del parametro 
    public static Token nombreDe(yParser.ParametroContext contexto) {
        if (contexto instanceof yParser.ParametroArregloContext arreglo) {
            return arreglo.IDENTIFICADOR().getSymbol();
        }
        if (contexto instanceof yParser.ParametroEstructuraContext estructura) {
            return estructura.IDENTIFICADOR(1).getSymbol();
        }
        return ((yParser.ParametroValorContext) contexto).IDENTIFICADOR().getSymbol();
    }

    public static Tipo tipoRetorno(yParser.DefinicionFuncionContext contexto) {
        if (contexto.tipoRetorno() == null) {
            return Tipo.VOID;
        }
        return tipoDe(contexto.tipoRetorno().tipo()).conDimensiones(contexto.tipoRetorno().CORCHETE_IZQUIERDO().size());
    }

    // Estructuras y funciones
    
    @Override
    public Void visitDefinicionEstructura(yParser.DefinicionEstructuraContext contexto) {
        Token nombre = contexto.IDENTIFICADOR().getSymbol();
        TipoDefinido estructura = new TipoDefinido(nombre.getText(), TipoDefinido.Categoria.ESTRUCTURA,
                errores.getArchivo(), Posicion.de(nombre), null);
        if (!tablas.tipos().agregar(estructura)) {
            error(nombre, nombre.getText(), "Ya existe una estructura o clase llamada '" + nombre.getText() + "'");
            return null;
        }
        for (yParser.AtributoEstructuraContext atributo : contexto.atributoEstructura()) {
            Token nombreAtributo = atributo.IDENTIFICADOR().getSymbol();
            List<Integer> tamanos = new ArrayList<>();
            for (yParser.DimensionContext dimension : atributo.dimension()) {
                String texto = dimension.expresion().getText();
                if (texto.matches("\\d+") && Integer.parseInt(texto) > 0) {
                    tamanos.add(Integer.parseInt(texto));
                } else {
                    error(dimension.getStart(), texto, "El tamaño de un arreglo dentro de una estructura debe ser "
                            + "una constante entera mayor que 0");
                }
            }
            Tipo tipo = tipoDe(atributo.tipo()).conDimensiones(atributo.dimension().size());
            Campo campo = new Campo(nombreAtributo.getText(), tipo, tamanos, Posicion.de(nombreAtributo),
                    Visibilidad.PUBLIC, nombre.getText());
            if (!estructura.agregarCampo(campo)) {
                error(nombreAtributo, nombreAtributo.getText(), "La estructura '" + nombre.getText()
                        + "' ya tiene un atributo '" + nombreAtributo.getText() + "'");
            }
        }
        return null;
    }

    @Override
    public Void visitDefinicionFuncion(yParser.DefinicionFuncionContext contexto) {
        Token nombre = contexto.IDENTIFICADOR().getSymbol();
        List<Tipo> parametros = new ArrayList<>();
        if (contexto.parametros() != null) {
            contexto.parametros().parametro().forEach(parametro -> parametros.add(tipoDe(parametro)));
        }
        Simbolo funcion = new Simbolo(nombre.getText(), CategoriaSimbolo.FUNCION, tipoRetorno(contexto), parametros,
                "global", errores.getArchivo(), Posicion.de(nombre), -1, Visibilidad.PUBLIC, null, false);
        if (!tablas.simbolos().declararFuncion(funcion)) {
            error(nombre, nombre.getText(), "Ya existe una función llamada '" + nombre.getText() + "'");
        }
        // el cuerpo solo se recorre para registrar las estructuras definidas adentro
        visit(contexto.bloque());
        return null;
    }
}
