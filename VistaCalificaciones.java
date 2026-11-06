package linguagest.mvc.vista;

import linguagest.mvc.modelo.Calificacion;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Contrato de la VISTA. El controlador depende de esta interfaz y no de una
 * implementacion concreta: la consola actual podria reemplazarse por una
 * interfaz grafica sin tocar controlador ni modelo.
 */
public interface VistaCalificaciones {

    int mostrarMenu();

    int pedirEntero(String mensaje);

    double pedirNota(String mensaje);

    LocalDate pedirFecha(String mensaje);

    String pedirTexto(String mensaje);

    void mostrarCalificaciones(ArrayList<Calificacion> lista);

    void mostrarEstadisticas(String[] rangos, int[] cantidades, Double promedio);

    void mostrarMensaje(String mensaje);

    void mostrarError(String mensaje);
}
