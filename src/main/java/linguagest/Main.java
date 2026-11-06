package linguagest;

import linguagest.control.GestorAlumnos;
import linguagest.control.GestorAsistencia;
import linguagest.control.GestorInscripciones;
import linguagest.dao.*;
import linguagest.excepcion.CupoExcepcion;
import linguagest.mvc.controlador.ControladorCalificaciones;
import linguagest.mvc.modelo.ArchivoCalificaciones;
import linguagest.mvc.modelo.ModeloCalificaciones;
import linguagest.mvc.vista.VistaConsola;
import linguagest.model.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

/**
 * Prototipo operacional de LinguaGest (consola).
 * Requiere que la base de datos "linguagest" ya exista (ver sql/01_crear_tablas.sql
 * y, opcionalmente, sql/02_datos_prueba.sql) y que config.properties tenga
 * los datos de conexion correctos.
 */
public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final AlumnoDAO alumnoDAO = new AlumnoDAO();
    private static final CursoDAO cursoDAO = new CursoDAO();
    private static final InscripcionDAO inscripcionDAO = new InscripcionDAO();
    private static final CalificacionDAO calificacionDAO = new CalificacionDAO();
    private static final GestorAsistencia gestorAsistencia = new GestorAsistencia();
    private static final GestorInscripciones gestorInscripciones = new GestorInscripciones();
    private static final GestorAlumnos gestorAlumnos = new GestorAlumnos();

    public static void main(String[] args) {
        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Opcion: ");
            try {
                switch (opcion) {
                    case 1 -> altaAlumno();
                    case 2 -> altaCurso();
                    case 3 -> inscribirAlumno();
                    case 4 -> registrarAsistencia();
                    case 5 -> consultarCursosDisponibles();
                    case 6 -> consultarHistorialAsistencia();
                    case 7 -> cargarCalificacion();
                    case 8 -> consultarPromedioCurso();
                    case 9 -> darDeBajaInscripcion();
                    case 10 -> deshacerUltimaInscripcion();
                    case 11 -> verListaEspera();
                    case 12 -> listarAlumnosOrdenados();
                    case 13 -> buscarAlumnoPorApellido();
                    case 14 -> listarDescripciones();
                    case 15 -> new ControladorCalificaciones(new ModeloCalificaciones(),
                            new ArchivoCalificaciones(), new VistaConsola(sc)).iniciar();
                    case 0 -> System.out.println("Saliendo...");
                    default -> System.out.println("Opcion invalida.");
                }
            } catch (SQLException e) {
                System.out.println("Error de base de datos: " + e.getMessage());
            } catch (IllegalArgumentException | DateTimeParseException e) {
                System.out.println("Dato invalido: " + e.getMessage());
            }
        } while (opcion != 0);
    }

    private static void mostrarMenu() {
        System.out.println("\n===== LinguaGest - Prototipo =====");
        System.out.println("1. Alta de alumno");
        System.out.println("2. Alta de curso");
        System.out.println("3. Inscribir alumno a curso");
        System.out.println("4. Registrar asistencia");
        System.out.println("5. Consultar cursos con cupo disponible");
        System.out.println("6. Consultar historial de asistencia de un alumno");
        System.out.println("7. Cargar calificacion");
        System.out.println("8. Consultar promedio de un curso");
        System.out.println("9. Dar de baja una inscripcion");
        System.out.println("10. Deshacer ultima inscripcion (pila)");
        System.out.println("11. Ver lista de espera de un curso (cola)");
        System.out.println("12. Listar alumnos ordenados por apellido");
        System.out.println("13. Buscar alumno por apellido (busqueda binaria)");
        System.out.println("14. Listar cursos y alumnos (interfaz Describible)");
        System.out.println("15. Modulo Calificaciones (patron MVC, archivos CSV)");
        System.out.println("0. Salir");
    }

    private static void altaAlumno() throws SQLException {
        System.out.print("Legajo: "); String legajo = sc.nextLine();
        System.out.print("Nombre: "); String nombre = sc.nextLine();
        System.out.print("Apellido: "); String apellido = sc.nextLine();
        System.out.print("Email: "); String email = sc.nextLine();
        int id = alumnoDAO.insertar(new Alumno(legajo, nombre, apellido, email));
        System.out.println("Alumno creado con id: " + id);
    }

    private static void altaCurso() throws SQLException {
        System.out.print("Idioma: "); String idioma = sc.nextLine();
        System.out.print("Nivel: "); String nivel = sc.nextLine();
        int cupo = leerEntero("Cupo maximo: ");
        System.out.print("Modalidad (presencial/virtual): "); String modalidad = sc.nextLine();

        Curso curso = modalidad.equalsIgnoreCase("virtual")
                ? new CursoVirtual(idioma, nivel, cupo, "Plataforma institucional")
                : new CursoPresencial(idioma, nivel, cupo, "Aula 1");

        int id = cursoDAO.insertar(curso);
        System.out.println("Curso creado con id: " + id);
    }

    private static void inscribirAlumno() throws SQLException {
        int alumnoId = leerEntero("Id de alumno: ");
        int cursoId = leerEntero("Id de curso: ");
        try {
            int idInscripcion = gestorInscripciones.inscribir(alumnoId, cursoId);
            System.out.println("Inscripcion registrada con id: " + idInscripcion);
        } catch (CupoExcepcion e) {
            System.out.println("Aviso: " + e.getMessage());
            System.out.print("Desea agregar al alumno a la lista de espera? (s/n): ");
            if (sc.nextLine().trim().equalsIgnoreCase("s")) {
                gestorInscripciones.encolarEnEspera(alumnoId, cursoId);
            }
        }
    }

    private static void darDeBajaInscripcion() throws SQLException {
        int alumnoId = leerEntero("Id de alumno: ");
        int cursoId = leerEntero("Id de curso: ");
        gestorInscripciones.darDeBajaYPromover(alumnoId, cursoId);
    }

    private static void deshacerUltimaInscripcion() throws SQLException {
        gestorInscripciones.deshacerUltimaInscripcion();
    }

    private static void verListaEspera() {
        int cursoId = leerEntero("Id de curso: ");
        Queue<Alumno> cola = gestorInscripciones.verListaEspera(cursoId);
        if (cola.isEmpty()) {
            System.out.println("La lista de espera esta vacia.");
            return;
        }
        System.out.println("Lista de espera del curso " + cursoId + " (orden de llegada):");
        int posicion = 1;
        for (Alumno a : cola) {
            System.out.println("  " + posicion++ + ". " + a.getNombre() + " " + a.getApellido());
        }
    }

    private static void listarAlumnosOrdenados() throws SQLException {
        List<Alumno> alumnos = gestorAlumnos.listarOrdenadosPorApellido();
        System.out.println("Alumnos ordenados por apellido:");
        alumnos.forEach(System.out::println);
    }

    /** Polimorfismo por interfaz: una misma lista contiene cursos y alumnos. */
    private static void listarDescripciones() throws SQLException {
        List<Describible> elementos = new ArrayList<>();
        elementos.addAll(cursoDAO.listarTodos());
        elementos.addAll(alumnoDAO.listarTodos());
        for (Describible d : elementos) {
            System.out.println(" - " + d.getDescripcion());
        }
    }

    private static void buscarAlumnoPorApellido() throws SQLException {
        System.out.print("Apellido a buscar: ");
        String apellido = sc.nextLine().trim();
        List<Alumno> encontrados = gestorAlumnos.buscarPorApellido(apellido);
        if (encontrados.isEmpty()) {
            System.out.println("No se encontraron alumnos con ese apellido.");
        } else {
            System.out.println("Coincidencias:");
            encontrados.forEach(System.out::println);
        }
    }

    private static void registrarAsistencia() throws SQLException {
        int cursoId = leerEntero("Id de curso: ");
        List<Curso> cursos = cursoDAO.listarTodos();
        Curso curso = cursos.stream().filter(c -> c.getCursoId() == cursoId).findFirst().orElse(null);
        if (curso == null) {
            System.out.println("Curso no encontrado.");
            return;
        }

        List<Alumno> inscriptos = inscripcionDAO.listarInscriptosPorCurso(cursoId);
        if (inscriptos.isEmpty()) {
            System.out.println("No hay alumnos inscriptos en ese curso.");
            return;
        }

        List<Alumno> presentes = new ArrayList<>();
        System.out.println("Marcar presentes (s/n) para cada alumno:");
        for (Alumno a : inscriptos) {
            System.out.print("  " + a.getNombre() + " " + a.getApellido() + " presente? (s/n): ");
            String resp = sc.nextLine();
            if (resp.equalsIgnoreCase("s")) {
                presentes.add(a);
            }
        }

        gestorAsistencia.registrarAsistencia(curso, LocalDate.now(), presentes);
    }

    private static void consultarCursosDisponibles() throws SQLException {
        List<String> disponibles = cursoDAO.cursosConCupoDisponible();
        System.out.println("Cursos con cupo disponible:");
        disponibles.forEach(System.out::println);
    }

    private static void consultarHistorialAsistencia() throws SQLException {
        int alumnoId = leerEntero("Id de alumno: ");
        AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
        List<String> historial = asistenciaDAO.historialPorAlumno(alumnoId);
        System.out.println("Historial de asistencia:");
        historial.forEach(System.out::println);
    }

    private static void cargarCalificacion() throws SQLException {
        int alumnoId = leerEntero("Id de alumno: ");
        int cursoId = leerEntero("Id de curso: ");
        double nota = leerDouble("Nota (0 a 10): ");
        int inscripcionId = inscripcionDAO.obtenerInscripcionId(alumnoId, cursoId);
        if (inscripcionId < 0) {
            System.out.println("El alumno no esta inscripto activamente en ese curso.");
            return;
        }
        calificacionDAO.cargarNota(inscripcionId, LocalDate.now(), nota);
        System.out.println("Calificacion cargada.");
    }

    private static void consultarPromedioCurso() throws SQLException {
        int cursoId = leerEntero("Id de curso: ");
        Double promedio = calificacionDAO.promedioPorCurso(cursoId);
        System.out.println(promedio == null ? "Sin calificaciones cargadas." : "Promedio: " + promedio);
    }

    /** Repite la lectura hasta recibir un entero valido (manejo de NumberFormatException). */
    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Error: debe ingresar un valor numerico entero.");
            }
        }
    }

    private static double leerDouble(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Double.parseDouble(sc.nextLine().trim().replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Error: debe ingresar un valor numerico.");
            }
        }
    }
}
