package linguagest.mvc.controlador;

import linguagest.excepcion.PersistenciaException;
import linguagest.mvc.modelo.ArchivoCalificaciones;
import linguagest.mvc.modelo.Calificacion;
import linguagest.mvc.modelo.ModeloCalificaciones;
import linguagest.mvc.vista.VistaCalificaciones;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * CONTROLADOR del patron MVC: recibe las acciones elegidas en la vista, invoca al
 * modelo y le ordena a la vista que muestre el resultado. Aqui se capturan las
 * excepciones de persistencia y de archivos para informarlas al usuario.
 */
public class ControladorCalificaciones {

    private static final File CARPETA = new File("datos");

    private final ModeloCalificaciones modelo;
    private final ArchivoCalificaciones archivos;
    private final VistaCalificaciones vista;

    public ControladorCalificaciones(ModeloCalificaciones modelo, ArchivoCalificaciones archivos,
                                     VistaCalificaciones vista) {
        this.modelo = modelo;
        this.archivos = archivos;
        this.vista = vista;
    }

    /** Bucle del modulo: atiende las opciones hasta que el usuario elige volver. */
    public void iniciar() {
        int opcion;
        do {
            opcion = vista.mostrarMenu();
            try {
                switch (opcion) {
                    case 1 -> listar();
                    case 2 -> registrar();
                    case 3 -> corregir();
                    case 4 -> eliminar();
                    case 5 -> estadisticas();
                    case 6 -> exportar();
                    case 7 -> importar();
                    case 0 -> vista.mostrarMensaje("Volviendo al menu principal...");
                    default -> vista.mostrarError("Opcion invalida.");
                }
            } catch (PersistenciaException e) {
                vista.mostrarError(e.getMessage());
            } catch (IOException e) {
                vista.mostrarError("Problema con el archivo: " + e.getMessage());
            }
        } while (opcion != 0);
    }

    private void listar() throws PersistenciaException {
        int cursoId = vista.pedirEntero("Id de curso: ");
        vista.mostrarCalificaciones(modelo.listarPorCurso(cursoId));
    }

    private void registrar() throws PersistenciaException {
        int alumnoId = vista.pedirEntero("Id de alumno: ");
        int cursoId = vista.pedirEntero("Id de curso: ");
        LocalDate fecha = vista.pedirFecha("Fecha de evaluacion");
        double nota = vista.pedirNota("Nota (0 a 10): ");
        int id = modelo.registrar(alumnoId, cursoId, fecha, nota);
        vista.mostrarMensaje("Calificacion registrada con id " + id + ".");
    }

    private void corregir() throws PersistenciaException {
        int id = vista.pedirEntero("Id de calificacion: ");
        double nota = vista.pedirNota("Nueva nota (0 a 10): ");
        vista.mostrarMensaje(modelo.actualizarNota(id, nota) ? "Nota actualizada." : "No existe la calificacion " + id + ".");
    }

    private void eliminar() throws PersistenciaException {
        int id = vista.pedirEntero("Id de calificacion: ");
        vista.mostrarMensaje(modelo.eliminar(id) ? "Calificacion eliminada." : "No existe la calificacion " + id + ".");
    }

    private void estadisticas() throws PersistenciaException {
        int cursoId = vista.pedirEntero("Id de curso: ");
        ArrayList<Calificacion> lista = modelo.listarPorCurso(cursoId);
        vista.mostrarEstadisticas(ModeloCalificaciones.RANGOS, modelo.distribucion(lista), modelo.promedio(lista));
    }

    private void exportar() throws PersistenciaException, IOException {
        int cursoId = vista.pedirEntero("Id de curso a exportar: ");
        ArrayList<Calificacion> lista = modelo.listarPorCurso(cursoId);
        File destino = new File(CARPETA, "calificaciones_curso_" + cursoId + ".csv");
        archivos.exportar(lista, destino);
        vista.mostrarMensaje("Se exportaron " + lista.size() + " calificaciones a " + destino.getPath());
    }

    private void importar() throws IOException {
        String ruta = vista.pedirTexto("Ruta del CSV (ej: datos/importar.csv): ");
        File origen = new File(ruta);
        if (!origen.isFile()) {
            vista.mostrarError("El archivo '" + ruta + "' no existe.");
            return;
        }
        ArrayList<String> errores = new ArrayList<>();
        ArrayList<ArchivoCalificaciones.Fila> filas = archivos.leerImportacion(origen, errores);
        int cargadas = 0;
        for (ArchivoCalificaciones.Fila f : filas) {
            try {
                modelo.registrar(f.alumnoId(), f.cursoId(), f.fecha(), f.nota());
                cargadas++;
            } catch (PersistenciaException e) {
                errores.add("Alumno " + f.alumnoId() + ", curso " + f.cursoId() + ": " + e.getMessage());
            }
        }
        archivos.registrarErrores(errores, new File(CARPETA, "importacion_errores.log"));
        vista.mostrarMensaje("Importacion terminada: " + cargadas + " cargadas, " + errores.size() + " rechazadas.");
        if (!errores.isEmpty()) vista.mostrarMensaje("El detalle quedo en datos/importacion_errores.log");
    }
}
