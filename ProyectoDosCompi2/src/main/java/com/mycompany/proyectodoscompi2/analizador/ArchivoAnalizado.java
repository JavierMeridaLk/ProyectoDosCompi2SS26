package com.mycompany.proyectodoscompi2.analizador;

import java.nio.file.Path;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;

import com.mycompany.proyectodoscompi2.errores.ErrorCompilacion;
import com.mycompany.proyectodoscompi2.errores.ManejadorErrores;
import com.mycompany.proyectodoscompi2.errores.TipoError;

// Un archivo ya pasado por el lexer y el parser
public record ArchivoAnalizado(Lenguaje lenguaje, Path ruta, ParserRuleContext arbol, ManejadorErrores manejador) {

    public String nombre() {
        return manejador.getArchivo();
    }

    // El semantico solo revisa archivos con un arbol completo
    public boolean sintaxisCorrecta() {
        return !manejador.hayErrores(TipoError.LEXICO) && !manejador.hayErrores(TipoError.SINTACTICO);
    }

    public List<ErrorCompilacion> errores() {
        return manejador.getErrores();
    }
}
