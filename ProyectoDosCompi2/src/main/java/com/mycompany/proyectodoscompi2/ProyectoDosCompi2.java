package com.mycompany.proyectodoscompi2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.mycompany.proyectodoscompi2.analizador.Compilador;
import com.mycompany.proyectodoscompi2.analizador.ResultadoCompilacion;
import com.mycompany.proyectodoscompi2.cuartetas.TraductorC3D;
import com.mycompany.proyectodoscompi2.errores.ErrorCompilacion;
import com.mycompany.proyectodoscompi2.riscv.TraductorRiscV;
import com.mycompany.proyectodoscompi2.views.FramePrincipal;


// @author xavi
public class ProyectoDosCompi2 {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            FramePrincipal.main(args);
            return;
        }
        boolean riscV = args[0].equals("--riscv");
        for (int i = riscV ? 1 : 0; i < args.length; i++) {
            Path ruta = Path.of(args[i]).toAbsolutePath().normalize();
            ResultadoCompilacion resultado = Compilador.compilar(ruta.getParent(), ruta, Files::readString);
            System.out.println((riscV ? "# === " : "=== ") + ruta.getFileName());
            if (resultado.exitoso() && riscV) {
                System.out.print(TraductorRiscV.traducir(resultado.cuartetas()));
            } else if (resultado.exitoso()) {
                TraductorC3D.traducir(resultado.cuartetas()).forEach(System.out::println);
            } else {
                for (ErrorCompilacion error : resultado.errores()) {
                    System.out.println(error);
                }
            }
        }
    }
}
