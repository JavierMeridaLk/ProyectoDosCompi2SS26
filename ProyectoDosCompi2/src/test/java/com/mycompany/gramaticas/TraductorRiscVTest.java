package com.mycompany.gramaticas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import com.mycompany.proyectodoscompi2.analizador.CargadorArchivos;
import com.mycompany.proyectodoscompi2.analizador.Compilador;
import com.mycompany.proyectodoscompi2.analizador.ResultadoCompilacion;
import com.mycompany.proyectodoscompi2.cuartetas.Cuarteta;
import com.mycompany.proyectodoscompi2.cuartetas.TraductorC3D;
import com.mycompany.proyectodoscompi2.riscv.TraductorRiscV;

// Pruebas de la traduccion de cuartetas a RISC-V.
// Las cuartetas escritas a mano prueban casos que los lenguajes no generan (o no siempre); cada una se ejecuta
// en el interprete del C3D y en el simulador de RISC-V y las dos salidas deben ser la esperada.
// Las demas revisan la forma del ensamblador generado desde programas completos.
//
// @author xavi
public class TraductorRiscVTest {

    private static final Path RAIZ = Path.of("/proyecto");

    // ==================================================================
    //                    Cuartetas escritas a mano
    // ==================================================================

    @Test
    void moduloDeDecimales() {
        List<Cuarteta> programa = principal(1,
                c("%", "7.5", "2", "t1"),
                c("print", "t1", "decimal", ""),
                espacio(),
                c("%", "-7.5", "2", "t2"),
                c("print", "t2", "decimal", ""),
                espacio(),
                c("%", "2.5", "1", "t3"),
                c("print", "t3", "decimal", ""));
        assertEquals("1.5 -1.5 0.5", ejecutarAmbos(programa, ""));
    }

    @Test
    void conversionesTruncanHaciaCero() {
        List<Cuarteta> programa = principal(1,
                c("(int)", "-2.7", "", "t1"),
                c("print", "t1", "entero", ""),
                espacio(),
                c("(int)", "2.7", "", "t2"),
                c("print", "t2", "entero", ""),
                espacio(),
                c("(double)", "7", "", "t3"),
                c("print", "t3", "decimal", ""),
                espacio(),
                c("(int)", "t3", "", "t4"),
                c("print", "t4", "entero", ""));
        assertEquals("-2 2 7.0 7", ejecutarAmbos(programa, ""));
    }

    @Test
    void comparacionesEntreEnterosYDecimales() {
        List<Cuarteta> programa = principal(1,
                c("if <", "3", "3.5", "L1"),
                c("print", "110", "caracter", ""),
                c("goto", "", "", "L2"),
                c("label", "", "", "L1"),
                c("print", "115", "caracter", ""),
                c("label", "", "", "L2"),
                c("==", "2", "2.0", "t1"),
                c("print", "t1", "entero", ""),
                c(">=", "4", "4.5", "t2"),
                c("print", "t2", "entero", ""),
                c("if >", "4.5", "t2", "L3"),
                c("print", "110", "caracter", ""),
                c("label", "", "", "L3"));
        assertEquals("s10", ejecutarAmbos(programa, ""));
    }

    @Test
    void lasSeisComparacionesDeEnteros() {
        List<Cuarteta> cuerpo = new ArrayList<>();
        int temporal = 0;
        for (int[] par : new int[][] {{3, 5}, {5, 5}, {7, 5}}) {
            for (String op : new String[] {"<", ">", "<=", ">=", "==", "!="}) {
                String t = "t" + (++temporal);
                cuerpo.add(c(op, String.valueOf(par[0]), String.valueOf(par[1]), t));
                cuerpo.add(c("print", t, "entero", ""));
            }
            cuerpo.add(espacio());
        }
        assertEquals("101001 001110 010101 ", ejecutarAmbos(principal(1, cuerpo.toArray(Cuarteta[]::new)), ""));
    }

    @Test
    void aritmeticaYUnariosDeEnteros() {
        List<Cuarteta> programa = principal(1,
                c("/", "7", "2", "t1"),
                c("/", "-7", "2", "t2"),
                c("%", "7", "-3", "t3"),
                c("*", "6", "7", "t4"),
                c("-", "3", "10", "t5"),
                c("neg", "t4", "", "t6"),
                c("!", "0", "", "t7"),
                c("!", "t4", "", "t8"),
                c("neg", "-2.5", "", "t9"),
                c("print", "t1", "entero", ""), espacio(),
                c("print", "t2", "entero", ""), espacio(),
                c("print", "t3", "entero", ""), espacio(),
                c("print", "t4", "entero", ""), espacio(),
                c("print", "t5", "entero", ""), espacio(),
                c("print", "t6", "entero", ""), espacio(),
                c("print", "t7", "entero", ""),
                c("print", "t8", "entero", ""), espacio(),
                c("print", "t9", "decimal", ""));
        assertEquals("3 -3 1 42 -7 -42 10 2.5", ejecutarAmbos(programa, ""));
    }

    // t1 empieza con un entero, pero en el ciclo recibe un decimal: debe ser decimal en toda la funcion
    @Test
    void unTemporalQueSeVuelveDecimalEnUnCiclo() {
        List<Cuarteta> programa = principal(1,
                c("=", "0", "", "t1"),
                c("label", "", "", "L1"),
                c("+", "t1", "0.5", "t2"),
                c("=", "t2", "", "t1"),
                c("if <", "t1", "2.0", "L1"),
                c("print", "t1", "decimal", ""));
        assertEquals("2.0", ejecutarAmbos(programa, ""));
    }

    // 300 temporales: sus celdas quedan a mas de 2047 bytes del frame pointer
    @Test
    void marcoConTrescientosTemporales() {
        List<Cuarteta> cuerpo = new ArrayList<>();
        for (int i = 1; i <= 300; i++) {
            cuerpo.add(c("=", String.valueOf(i), "", "t" + i));
        }
        // la suma hasta t_i queda en t(299 + i)
        cuerpo.add(c("+", "t1", "t2", "t301"));
        for (int i = 3; i <= 300; i++) {
            cuerpo.add(c("+", "t" + (298 + i), "t" + i, "t" + (299 + i)));
        }
        cuerpo.add(c("print", "t599", "entero", ""));
        List<Cuarteta> programa = principal(1, cuerpo.toArray(Cuarteta[]::new));
        assertEquals("45150", ejecutarAmbos(programa, ""));
        assertTrue(TraductorRiscV.traducir(programa).contains("li        t5, "),
                "los temporales lejanos deben usar una direccion calculada");
    }

    @Test
    void celdasDelStackLejosDelFramePointer() {
        List<Cuarteta> programa = principal(402,
                c("escribir_stack", "77", "400", "P"),
                c("escribir_stack", "2.5", "401", "P"),
                c("leer_stack", "P", "400", "t1"),
                new Cuarteta("leer_stack", "P", "401", "t2", true),
                c("print", "t1", "entero", ""),
                espacio(),
                c("print", "t2", "decimal", ""));
        assertEquals("77 2.5", ejecutarAmbos(programa, ""));
        assertFalse(TraductorRiscV.traducir(programa).contains("3200(s4)"), "desplazamiento de mas de 12 bits");
    }

    @Test
    void heapConDesplazamientoVariableYLejano() {
        List<Cuarteta> programa = principal(1,
                c("=", "H", "", "t1"),
                c("+", "H", "600", "t2"),
                c("=", "t2", "", "H"),
                c("=", "3", "", "t3"),
                c("escribir_heap", "99", "t3", "t1"),
                c("escribir_heap", "5", "500", "t1"),
                c("escribir_heap", "1.25", "501", "t1"),
                c("leer_heap", "t1", "t3", "t4"),
                c("leer_heap", "t1", "500", "t5"),
                new Cuarteta("leer_heap", "t1", "501", "t6", true),
                c("leer_heap", "t1", "7", "t7"),
                c("print", "t4", "entero", ""),
                c("print", "t5", "entero", ""),
                c("print", "t7", "entero", ""),
                espacio(),
                c("print", "t6", "decimal", ""));
        assertEquals("9950 1.25", ejecutarAmbos(programa, ""));
    }

    @Test
    void leerCaracteresHastaTerminarLaEntrada() {
        List<Cuarteta> programa = principal(1,
                c("label", "", "", "L1"),
                c("leer_caracter", "", "", "t1"),
                c("if ==", "t1", "10", "L2"),
                c("+", "t1", "1", "t2"),
                c("print", "t2", "caracter", ""),
                c("goto", "", "", "L1"),
                c("label", "", "", "L2"),
                c("leer_caracter", "", "", "t3"),
                c("print", "t3", "entero", ""));
        assertEquals("IBM10", ejecutarAmbos(programa, "HAL\n"));
    }

    // Llamada con marco propio, retorno en stack[P + 0] y recursion: n vive en un temporal durante la llamada.
    // cuenta(n) imprime n..1 y retorna n + cuenta(n - 1); su marco es retorno + n + 6 temporales = 8 celdas
    @Test
    void llamadaRecursivaHechaAMano() {
        List<Cuarteta> programa = new ArrayList<>(List.of(
                c("func", "8", "", "cuenta"),
                c("leer_stack", "P", "1", "t1"),
                c("if ==", "t1", "0", "L9"),
                c("print", "t1", "entero", ""),
                c("-", "t1", "1", "t2"),
                c("escribir_stack", "t2", "9", "P"),
                c("+", "P", "8", "t3"),
                c("=", "t3", "", "P"),
                c("call", "", "", "cuenta"),
                c("-", "P", "8", "t4"),
                c("=", "t4", "", "P"),
                c("leer_stack", "P", "8", "t5"),
                c("+", "t1", "t5", "t6"),
                c("escribir_stack", "t6", "0", "P"),
                c("goto", "", "", "L8"),
                c("label", "", "", "L9"),
                c("escribir_stack", "0", "0", "P"),
                c("label", "", "", "L8"),
                c("end", "", "", "")));
        // main: 1 celda + 3 temporales = 4; el argumento va en stack[P + 4 + 1]
        programa.addAll(principal(1,
                c("escribir_stack", "5", "5", "P"),
                c("+", "P", "4", "t7"),
                c("=", "t7", "", "P"),
                c("call", "", "", "cuenta"),
                c("-", "P", "4", "t8"),
                c("=", "t8", "", "P"),
                c("leer_stack", "P", "4", "t9"),
                espacio(),
                c("print", "t9", "entero", "")));
        assertEquals("54321 15", ejecutarAmbos(programa, ""));
    }

    @Test
    void etiquetasConCaracteresQueRiscVNoAcepta() {
        List<Cuarteta> programa = new ArrayList<>(List.of(
                c("func", "1", "", "tamaño_x[]"),
                c("print", "107", "caracter", ""),
                c("end", "", "", "")));
        programa.addAll(principal(1, c("call", "", "", "tamaño_x[]")));
        assertEquals("k", ejecutarAmbos(programa, ""));
        String ensamblador = TraductorRiscV.traducir(programa);
        assertTrue(ensamblador.contains("tama_f1o_x_5b_5d:"), ensamblador);
        assertFalse(ensamblador.lines().filter(linea -> !linea.strip().startsWith("#")).anyMatch(l -> l.contains("ñ")),
                "solo los comentarios pueden llevar la etiqueta original");
    }

    // Un .y o .z compilado solo no tiene main: el programa ensambla y termina sin hacer nada
    @Test
    void sinProgramaPrincipalSoloTermina() {
        List<Cuarteta> funcion = List.of(c("func", "1", "", "f_x"), c("print", "1", "entero", ""), c("end", "", "", ""));
        String ensamblador = TraductorRiscV.traducir(funcion);
        assertFalse(ensamblador.contains("jal       ra, main"));
        assertEquals("", new SimuladorRiscV(ensamblador, "").ejecutar());
    }

    @Test
    void cuartetasMalFormadas() {
        assertThrows(IllegalArgumentException.class,
                () -> TraductorRiscV.traducir(List.of(c("print", "1", "entero", ""))), "fuera de una funcion");
        assertThrows(IllegalArgumentException.class,
                () -> TraductorRiscV.traducir(List.of(c("func", "1", "", "main"), c("halt", "", "", ""))),
                "funcion sin end");
        assertThrows(IllegalArgumentException.class,
                () -> TraductorRiscV.traducir(principal(1, c("potencia", "2", "3", "t1"))), "operador desconocido");
        assertThrows(IllegalArgumentException.class,
                () -> TraductorRiscV.traducir(principal(1, c("=", "x", "", "t1"))), "operando desconocido");
    }

    // ==================================================================
    //                    Forma del ensamblador
    // ==================================================================

    private static final String PROGRAMA = """
            import lib.f.y
            VARIABILES>
            esto x : decimalis 0.25;
            esto n : numerus 0;
            MAIOR>
            dum (n < 3) {
                x = x + 0.25;
                si (x > 0.5) {
                    >> "x=" + x + " ";
                } finis;
                n++;
            } finis;
            >> doble(0.25) >> " " >> factorial(5);
            FINIS;
            """;
    private static final String LIBRERIA = """
            %funciones
            definir doble(flotante a) -> flotante:
                retornar a * 2

            definir factorial(entero n) -> entero:
                si (n < 2) entonces
                    retornar 1
                retornar n * factorial(n - 1)
            """;

    @Test
    void programaCompletoDaLaMismaSalida() throws IOException {
        ResultadoCompilacion resultado = compilar(Map.of("main.pig", PROGRAMA, "lib/f.y", LIBRERIA));
        assertEquals("x=0.75 x=1.00 0.5 120", ejecutarAmbos(resultado.cuartetas(), ""));
    }

    @Test
    void estructuraGeneral() throws IOException {
        String ensamblador = traducir(Map.of("main.pig", PROGRAMA, "lib/f.y", LIBRERIA));
        int datos = ensamblador.indexOf("\n.data\n");
        int texto = ensamblador.indexOf("\n.text\n");
        assertTrue(datos > 0 && texto > datos, ".data antes de .text");
        String codigo = ensamblador.substring(texto + 7);
        assertTrue(codigo.startsWith("inicio:\n"), "lo primero que se ejecuta es inicio");
        assertTrue(codigo.indexOf("jal       ra, main") < codigo.indexOf("\nmain:"), "inicio llama a main");
        assertTrue(codigo.contains("\nf_doble:") && codigo.contains("\nf_factorial:"));
        assertTrue(ensamblador.contains("li        a7, 9"), "el stack y el heap se piden con sbrk");
    }

    @Test
    void cadaCuartetaApareceComoComentario() throws IOException {
        ResultadoCompilacion resultado = compilar(Map.of("main.pig", PROGRAMA, "lib/f.y", LIBRERIA));
        String ensamblador = TraductorRiscV.traducir(resultado.cuartetas());
        for (Cuarteta cuarteta : resultado.cuartetas()) {
            if (!cuarteta.operador().equals("label") && !cuarteta.operador().equals("func")) {
                assertTrue(ensamblador.contains("    # " + TraductorC3D.instruccion(cuarteta) + "\n"),
                        "falta el comentario de " + cuarteta);
            }
        }
    }

    @Test
    void cadaDecimalSeDeclaraUnaVez() throws IOException {
        String ensamblador = traducir(Map.of("main.pig", PROGRAMA, "lib/f.y", LIBRERIA));
        String datos = ensamblador.substring(ensamblador.indexOf(".data"), ensamblador.indexOf(".text"));
        assertEquals(1, ocurrencias(datos, ".double 0.25\n"), datos);
        assertEquals(1, ocurrencias(datos, ".double 0.5\n"), datos);
    }

    // Todo branch salta a la etiqueta que sigue (sobre un j); asi una condicion nunca queda fuera de alcance
    @Test
    void losBranchSoloSaltanSobreUnJ() throws IOException {
        String[] lineas = traducir(Map.of("main.pig", PROGRAMA, "lib/f.y", LIBRERIA)).split("\n");
        Pattern branch = Pattern.compile("^\\s+(beq|bne|blt|bge|ble|bgt|beqz)\\s+.*, (\\S+)$");
        int revisados = 0;
        for (int i = 0; i < lineas.length; i++) {
            Matcher m = branch.matcher(lineas[i]);
            if (m.matches()) {
                assertTrue(m.group(2).matches("_sigue\\d+"), lineas[i]);
                assertTrue(lineas[i + 1].strip().startsWith("j "), lineas[i + 1]);
                assertEquals(m.group(2) + ":", lineas[i + 2]);
                revisados++;
            }
        }
        assertTrue(revisados > 5);
    }

    @Test
    void soloSeUsanLosRegistrosDeLaConvencion() throws IOException {
        Set<String> permitidos = Set.of("zero", "ra", "sp", "s0", "s1", "s2", "s3", "s4", "t0", "t1", "t2", "t5",
                "t6", "a0", "a7", "ft0", "ft1", "ft2", "ft3", "fa0");
        Pattern registro = Pattern.compile("\\b(zero|ra|sp|gp|tp|s\\d+|t\\d|a\\d|f[tsa]\\d+)\\b");
        Set<String> usados = new LinkedHashSet<>();
        for (String linea : traducir(Map.of("main.pig", PROGRAMA, "lib/f.y", LIBRERIA)).split("\n")) {
            String instruccion = linea.contains("#") ? linea.substring(0, linea.indexOf('#')) : linea;
            if (instruccion.isBlank() || instruccion.endsWith(":")) {
                continue;
            }
            Matcher m = registro.matcher(instruccion.strip().replaceFirst("^\\S+", ""));
            while (m.find()) {
                usados.add(m.group(1));
            }
        }
        usados.removeAll(permitidos);
        assertTrue(usados.isEmpty(), "registros fuera de la convención: " + usados);
    }

    // Un cuerpo de ciclo de mas de 4 KB: el j de regreso queda lejos y la condicion sigue funcionando
    @Test
    void cicloConCuerpoDeMasDeCuatroKilobytes() throws IOException {
        StringBuilder programa = new StringBuilder("VARIABILES>\nesto i : numerus 0;\nMAIOR>\ndum (i < 2) {\n");
        StringBuilder esperada = new StringBuilder();
        for (int i = 0; i < 2; i++) {
            for (int k = 0; k < 40; k++) {
                esperada.append(i).append('.').append(k).append(' ');
            }
        }
        for (int k = 0; k < 40; k++) {
            programa.append("    >> i + \".").append(k).append(" \";\n");
        }
        programa.append("    i++;\n} finis;\nFINIS;\n");
        ResultadoCompilacion resultado = compilar(Map.of("main.pig", programa.toString()));
        SimuladorRiscV simulador = new SimuladorRiscV(TraductorRiscV.traducir(resultado.cuartetas()), "");
        assertEquals(esperada.toString(), simulador.ejecutar());
        assertTrue(simulador.saltoMasLargo() > 4096, "el cuerpo debía pasar de 4 KB: " + simulador.saltoMasLargo());
    }

    @Test
    void elSimuladorRechazaLoQueRarsNoEnsamblaria() {
        String base = ".data\n.text\ninicio:\n";
        assertThrows(IllegalStateException.class, () -> new SimuladorRiscV(base + "    call      main\n", ""));
        assertThrows(IllegalStateException.class, () -> new SimuladorRiscV(base + "    j         nada\n", ""));
        StringBuilder lejos = new StringBuilder(base + "    beqz      t0, fin\n");
        lejos.append("    add       t0, t0, t0\n".repeat(1100));
        lejos.append("fin:\n    li        a7, 10\n    ecall\n");
        assertThrows(IllegalStateException.class, () -> new SimuladorRiscV(lejos.toString(), ""),
                "un branch a mas de 4 KB");
    }

    // ==================================================================
    //                          Ayudas
    // ==================================================================

    private static Cuarteta c(String operador, String a, String b, String r) {
        return new Cuarteta(operador, a, b, r);
    }

    private static Cuarteta espacio() {
        return c("print", "32", "caracter", "");
    }

    // func main con P = 0 y H = 1; el marco son las variables mas los temporales (como en finFuncion)
    private static List<Cuarteta> principal(int variables, Cuarteta... cuerpo) {
        Set<String> temporales = new LinkedHashSet<>();
        for (Cuarteta q : cuerpo) {
            for (String operando : new String[] {q.argumento1(), q.argumento2(), q.resultado()}) {
                if (operando.matches("t\\d+")) {
                    temporales.add(operando);
                }
            }
        }
        List<Cuarteta> programa = new ArrayList<>();
        programa.add(c("func", String.valueOf(variables + temporales.size()), "", "main"));
        programa.add(c("=", "0", "", "P"));
        programa.add(c("=", "1", "", "H"));
        programa.addAll(List.of(cuerpo));
        programa.add(c("halt", "", "", ""));
        programa.add(c("end", "", "", ""));
        return programa;
    }

    private static String ejecutarAmbos(List<Cuarteta> cuartetas, String entrada) {
        String c3d = new InterpreteC3D(cuartetas, entrada).ejecutar();
        String ensamblador = TraductorRiscV.traducir(cuartetas);
        String riscV = new SimuladorRiscV(ensamblador, entrada).ejecutar();
        assertEquals(c3d, riscV, "el RISC-V imprime distinto que el C3D");
        return riscV;
    }

    private static ResultadoCompilacion compilar(Map<String, String> archivos) throws IOException {
        Map<Path, String> enMemoria = new LinkedHashMap<>();
        archivos.forEach((ruta, texto) -> enMemoria.put(RAIZ.resolve(ruta).normalize(), texto));
        CargadorArchivos.LectorArchivos lector = new CargadorArchivos.LectorArchivos() {
            @Override
            public String leer(Path archivo) throws IOException {
                String texto = enMemoria.get(archivo.normalize());
                if (texto == null) {
                    throw new IOException("No existe " + archivo);
                }
                return texto;
            }

            @Override
            public boolean existe(Path archivo) {
                return enMemoria.containsKey(archivo.normalize());
            }
        };
        ResultadoCompilacion resultado = Compilador.compilar(RAIZ, RAIZ.resolve("main.pig"), lector);
        assertTrue(resultado.exitoso(), "el programa tiene errores: " + resultado.errores());
        return resultado;
    }

    private static String traducir(Map<String, String> archivos) throws IOException {
        return TraductorRiscV.traducir(compilar(archivos).cuartetas());
    }

    private static int ocurrencias(String texto, String buscado) {
        int cuenta = 0;
        for (int i = texto.indexOf(buscado); i >= 0; i = texto.indexOf(buscado, i + 1)) {
            cuenta++;
        }
        return cuenta;
    }
}
