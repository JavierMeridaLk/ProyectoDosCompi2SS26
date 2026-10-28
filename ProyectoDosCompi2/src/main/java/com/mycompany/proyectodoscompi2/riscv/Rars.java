package com.mycompany.proyectodoscompi2.riscv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Abre un programa en RARS
public final class Rars {

    public static final String ARCHIVO = "rars1_6.jar";
    public static final String CARPETA = "herramientas";
    public static final int ERROR_ENSAMBLADO = 1;
    public static final int ERROR_EJECUCION = 2;

    private Rars() {
    }

    // Busca herramientas/rars1_6.jar
    public static Optional<Path> buscar(List<Path> carpetas) {
        for (Path carpeta : carpetas) {
            for (Path actual = carpeta == null ? null : carpeta.toAbsolutePath(); actual != null;
                    actual = actual.getParent()) {
                Path rars = actual.resolve(CARPETA).resolve(ARCHIVO);
                if (Files.isRegularFile(rars)) {
                    return Optional.of(rars);
                }
            }
        }
        return Optional.empty();
    }

    // Carpetas donde se busca por defecto
    public static List<Path> carpetasPorDefecto() {
        List<Path> carpetas = new ArrayList<>();
        carpetas.add(Path.of("").toAbsolutePath());
        try {
            Path jar = Path.of(Rars.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            carpetas.add(jar.getParent());
        } catch (Exception e) {
        }
        return carpetas;
    }

    // Abre la ventana de RARS con el programa en su editor
    public static void abrir(Path rars, Path programa) throws IOException {
        new ProcessBuilder(java(), "-jar", rars.toString(), "g", programa.toString())
                .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                .redirectError(ProcessBuilder.Redirect.DISCARD)
                .start();
    }

    // Ensambla y ejecuta sin ventana
    public static Process ejecutar(Path rars, Path programa) throws IOException {
        return new ProcessBuilder(java(), "-jar", rars.toString(), "nc", "me", "ae" + ERROR_ENSAMBLADO,
                "se" + ERROR_EJECUCION, programa.toString()).start();
    }

    private static String java() {
        return Path.of(System.getProperty("java.home"), "bin", "java").toString();
    }
}
