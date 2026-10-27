package com.mycompany.proyectodoscompi2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.mycompany.proyectodoscompi2.analizador.Compilador;
import com.mycompany.proyectodoscompi2.analizador.ResultadoCompilacion;
import com.mycompany.proyectodoscompi2.cuartetas.TraductorC3D;
import com.mycompany.proyectodoscompi2.errores.ErrorCompilacion;
import com.mycompany.proyectodoscompi2.views.FramePrincipal;

// Sin argumentos abre el IDE. Con archivos los compila en consola: muestra los errores o el C3D.
//   java -jar ProyectoDosCompi2.jar programa.pig
//
// @author xavi
public class ProyectoDosCompi2 {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            FramePrincipal.main(args);
            return;
        }
        for (String archivo : args) {
            Path ruta = Path.of(archivo).toAbsolutePath().normalize();
            ResultadoCompilacion resultado = Compilador.compilar(ruta.getParent(), ruta, Files::readString);
            System.out.println("=== " + ruta.getFileName());
            if (resultado.exitoso()) {
                TraductorC3D.traducir(resultado.cuartetas()).forEach(System.out::println);
            } else {
                for (ErrorCompilacion error : resultado.errores()) {
                    System.out.println(error);
                }
            }
        }
    }
}
