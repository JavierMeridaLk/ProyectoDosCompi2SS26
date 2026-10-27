package com.mycompany.gramaticas;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import com.mycompany.proyectodoscompi2.analizador.CargadorArchivos;
import com.mycompany.proyectodoscompi2.analizador.Compilador;
import com.mycompany.proyectodoscompi2.analizador.ResultadoCompilacion;
import com.mycompany.proyectodoscompi2.cuartetas.TraductorC3D;
import com.mycompany.proyectodoscompi2.errores.ErrorCompilacion;
import com.mycompany.proyectodoscompi2.errores.TipoError;

// Pruebas del analisis semantico y de la generacion de cuartetas con programas completos.
//
// Los casos estan en src/test/resources/semantico/*.casos:
//
//   @@@ OK : nombre                el programa compila sin errores
//   @@@ ERROR : nombre             el programa tiene errores semanticos (y ni lexicos ni sintacticos)
//   @@> error: texto               un error debe contener el texto (puede haber varios)
//   @@> c3d: texto                 el codigo de tres direcciones debe contener el texto
//   @@> entrada: texto             lo que se escribe en el teclado al ejecutar (\n separa lineas)
//   @@> salida: texto              lo que debe imprimir el programa al ejecutar el C3D (\n es salto de linea)
//   === main.pig                   empieza un archivo; el primero es el que se compila
//   === clases/Persona.z           (si no hay un .pig se genera un main.pig que importa todos)
//
// @author xavi
public class SemanticoTest {

    private static final Path RAIZ = Path.of("/proyecto");

    private record Caso(String archivo, int linea, boolean esperaOk, String nombre, Map<String, String> fuentes,
            List<String> errores, List<String> c3d, String entrada, String salida) {
    }

    // Lo unico que puede usar el codigo de tres direcciones: temporales, stack, heap, saltos y llamadas
    private static final List<String> OPERADORES = List.of("=", "+", "-", "*", "/", "%", "<", ">", "<=", ">=", "==",
            "!=", "!", "neg", "(int)", "(double)", "leer_stack", "leer_heap", "escribir_stack", "escribir_heap",
            "label", "goto", "if ==", "if !=", "if <", "if >", "if <=", "if >=", "func", "end", "call", "halt",
            "print", "leer_caracter");

    private static String escapes(String texto) {
        return texto.replace("\\n", "\n").replace("\\t", "\t");
    }

    private static List<Caso> leerCasos(Path archivo) throws IOException {
        List<Caso> casos = new ArrayList<>();
        List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);
        int i = 0;
        while (i < lineas.size()) {
            if (!lineas.get(i).startsWith("@@@ ")) {
                i++;
                continue;
            }
            int inicio = i + 1;
            String encabezado = lineas.get(i).substring(4);
            boolean ok = encabezado.startsWith("OK");
            String nombre = encabezado.substring(encabezado.indexOf(':') + 1).trim();
            Map<String, String> fuentes = new LinkedHashMap<>();
            List<String> errores = new ArrayList<>();
            List<String> c3d = new ArrayList<>();
            String entrada = "";
            String salida = null;
            String actual = null;
            StringBuilder texto = new StringBuilder();
            i++;
            while (i < lineas.size() && !lineas.get(i).startsWith("@@@ ")) {
                String linea = lineas.get(i);
                if (linea.startsWith("@@> error:")) {
                    errores.add(linea.substring("@@> error:".length()).trim());
                } else if (linea.startsWith("@@> c3d:")) {
                    c3d.add(linea.substring("@@> c3d:".length()).trim());
                } else if (linea.startsWith("@@> entrada:")) {
                    entrada = escapes(linea.substring("@@> entrada:".length()).stripLeading());
                } else if (linea.startsWith("@@> salida:")) {
                    salida = escapes(linea.substring("@@> salida:".length()).stripLeading());
                } else if (linea.startsWith("=== ")) {
                    if (actual != null) {
                        fuentes.put(actual, texto.toString());
                    }
                    actual = linea.substring(4).trim();
                    texto = new StringBuilder();
                } else if (actual != null) {
                    texto.append(linea).append('\n');
                }
                i++;
            }
            if (actual != null) {
                fuentes.put(actual, texto.toString().replaceAll("\\s+$", "") + "\n");
            }
            casos.add(new Caso(archivo.getFileName().toString(), inicio, ok, nombre, fuentes, errores, c3d, entrada,
                    salida));
        }
        return casos;
    }

    @TestFactory
    Stream<DynamicNode> semantico() throws Exception {
        Path directorio = Path.of(getClass().getResource("/semantico").toURI());
        List<Path> archivos;
        try (Stream<Path> listado = Files.list(directorio)) {
            archivos = listado.filter(p -> p.toString().endsWith(".casos")).sorted().toList();
        }
        assertFalse(archivos.isEmpty());
        List<DynamicNode> contenedores = new ArrayList<>();
        for (Path archivo : archivos) {
            List<DynamicNode> pruebas = new ArrayList<>();
            for (Caso caso : leerCasos(archivo)) {
                pruebas.add(DynamicTest.dynamicTest((caso.esperaOk() ? "[OK] " : "[ERROR] ") + caso.nombre(),
                        () -> verificar(caso)));
            }
            contenedores.add(DynamicContainer.dynamicContainer(archivo.getFileName().toString(), pruebas));
        }
        return contenedores.stream();
    }

    private static void verificar(Caso caso) throws IOException {
        Map<Path, String> enMemoria = new LinkedHashMap<>();
        if (!caso.fuentes().keySet().iterator().next().endsWith(".pig")) {
            StringBuilder principal = new StringBuilder();
            caso.fuentes().keySet().forEach(ruta -> principal.append("import ").append(ruta.replace('/', '.'))
                    .append('\n'));
            enMemoria.put(RAIZ.resolve("main.pig"), principal.append("MAIOR>\nFINIS;\n").toString());
        }
        caso.fuentes().forEach((ruta, texto) -> enMemoria.put(RAIZ.resolve(ruta).normalize(), texto));
        CargadorArchivos.LectorArchivos lector = new CargadorArchivos.LectorArchivos() {
            @Override
            public String leer(Path archivo) throws IOException {
                String texto = enMemoria.get(archivo.normalize());
                if (texto == null) {
                    throw new IOException("no existe " + archivo);
                }
                return texto;
            }

            @Override
            public boolean existe(Path archivo) {
                return enMemoria.containsKey(archivo.normalize());
            }
        };
        Path principal = enMemoria.keySet().iterator().next();
        ResultadoCompilacion resultado;
        try {
            resultado = Compilador.compilar(RAIZ, principal, lector);
        } catch (RuntimeException e) {
            throw new AssertionError(encabezado(caso) + " lanzó una excepción", e);
        }
        List<ErrorCompilacion> errores = resultado.errores();
        List<String> c3d = TraductorC3D.traducir(resultado.cuartetas());
        String detalle = "\n  errores:\n" + listar(errores) + "  c3d (" + c3d.size() + " líneas)";

        if (caso.esperaOk()) {
            if (!errores.isEmpty()) {
                fail(encabezado(caso) + " se esperaba sin errores" + detalle);
            }
        } else {
            if (errores.isEmpty()) {
                fail(encabezado(caso) + " se esperaban errores semánticos" + detalle);
            }
            if (errores.stream().anyMatch(e -> e.tipo() != TipoError.SEMANTICO)) {
                fail(encabezado(caso) + " hay errores léxicos o sintácticos (el caso debe probar el semántico)"
                        + detalle);
            }
            if (!resultado.cuartetas().isEmpty()) {
                fail(encabezado(caso) + " con errores no se deben entregar cuartetas" + detalle);
            }
        }
        for (String esperado : caso.errores()) {
            if (errores.stream().noneMatch(e -> e.descripcion().contains(esperado))) {
                fail(encabezado(caso) + " ningún error contiene: " + esperado + detalle);
            }
        }
        if (caso.esperaOk()) {
            for (var cuarteta : resultado.cuartetas()) {
                if (!OPERADORES.contains(cuarteta.operador()) || cuarteta.toString().contains("RET")) {
                    fail(encabezado(caso) + " cuarteta no permitida en el C3D: " + cuarteta);
                }
            }
        }
        if (caso.salida() != null) {
            String obtenida;
            try {
                obtenida = new InterpreteC3D(resultado.cuartetas(), caso.entrada()).ejecutar();
            } catch (RuntimeException e) {
                throw new AssertionError(encabezado(caso) + " falló al ejecutar el C3D: " + e.getMessage() + "\n"
                        + String.join("\n", c3d), e);
            }
            if (!obtenida.equals(caso.salida())) {
                fail(encabezado(caso) + " salida distinta\n  esperada: " + visible(caso.salida()) + "\n  obtenida: "
                        + visible(obtenida));
            }
        }
        String todo = String.join("\n", c3d);
        for (String esperado : caso.c3d()) {
            if (!todo.contains(esperado)) {
                fail(encabezado(caso) + " el C3D no contiene: " + esperado + "\n" + todo);
            }
        }
    }

    private static String visible(String texto) {
        return "\"" + texto.replace("\n", "\\n") + "\"";
    }

    private static String encabezado(Caso caso) {
        return caso.archivo() + ":" + caso.linea() + " [" + caso.nombre() + "]";
    }

    private static String listar(List<ErrorCompilacion> errores) {
        StringBuilder texto = new StringBuilder();
        errores.forEach(error -> texto.append("    ").append(error).append('\n'));
        return texto.toString();
    }
}
