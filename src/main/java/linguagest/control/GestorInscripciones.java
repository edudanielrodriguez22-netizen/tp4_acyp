package linguagest.control;

import linguagest.dao.AlumnoDAO;
import linguagest.dao.InscripcionDAO;
import linguagest.excepcion.CupoExcepcion;
import linguagest.model.Alumno;

import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Clase de control: gestiona inscripciones usando dos estructuras de datos.
 *  - COLA (FIFO, LinkedList): lista de espera por curso. El primero que espera
 *    es el primero en ocupar un lugar cuando se libera un cupo.
 *  - PILA (LIFO, ArrayDeque): historial de inscripciones, para deshacer la ultima.
 */
public class GestorInscripciones {

    /** Registro inmutable de una inscripcion realizada (se apila para poder deshacerla). */
    private record RegistroInscripcion(int inscripcionId, int alumnoId, int cursoId) { }

    private final InscripcionDAO inscripcionDAO = new InscripcionDAO();
    private final AlumnoDAO alumnoDAO = new AlumnoDAO();

    private final Map<Integer, Queue<Alumno>> listasEspera = new HashMap<>();
    private final Deque<RegistroInscripcion> pilaInscripciones = new ArrayDeque<>();

    /** Inscribe al alumno. Si el curso esta completo lanza CupoExcepcion. */
    public int inscribir(int alumnoId, int cursoId) throws SQLException, CupoExcepcion {
        if (!inscripcionDAO.tieneCupoDisponible(cursoId)) {
            throw new CupoExcepcion("El curso " + cursoId + " no tiene cupo disponible.", cursoId);
        }
        int id = inscripcionDAO.inscribirAlumno(alumnoId, cursoId);
        pilaInscripciones.push(new RegistroInscripcion(id, alumnoId, cursoId));
        return id;
    }

    /** Agrega al alumno al final de la cola de espera del curso. */
    public void encolarEnEspera(int alumnoId, int cursoId) throws SQLException {
        Alumno alumno = alumnoDAO.buscarPorId(alumnoId);
        if (alumno == null) {
            System.out.println("Alumno no encontrado.");
            return;
        }
        listasEspera.computeIfAbsent(cursoId, k -> new LinkedList<>()).offer(alumno);
        System.out.println(alumno.getNombre() + " " + alumno.getApellido()
                + " agregado a la lista de espera del curso " + cursoId
                + " (posicion " + listasEspera.get(cursoId).size() + ").");
    }

    /** Da de baja una inscripcion y, si hay lista de espera, inscribe al primero de la cola. */
    public Alumno darDeBajaYPromover(int alumnoId, int cursoId) throws SQLException {
        int inscripcionId = inscripcionDAO.obtenerInscripcionId(alumnoId, cursoId);
        if (inscripcionId < 0) {
            System.out.println("El alumno no tiene una inscripcion activa en ese curso.");
            return null;
        }
        inscripcionDAO.darDeBaja(inscripcionId);
        System.out.println("Inscripcion dada de baja.");
        return promoverSiguiente(cursoId);
    }

    /** Deshace la ultima inscripcion realizada (desapila) y promueve al siguiente en espera. */
    public Alumno deshacerUltimaInscripcion() throws SQLException {
        if (pilaInscripciones.isEmpty()) {
            System.out.println("No hay inscripciones para deshacer en esta sesion.");
            return null;
        }
        RegistroInscripcion ultima = pilaInscripciones.pop();
        inscripcionDAO.darDeBaja(ultima.inscripcionId());
        System.out.println("Se deshizo la inscripcion " + ultima.inscripcionId()
                + " (alumno " + ultima.alumnoId() + ", curso " + ultima.cursoId() + ").");
        return promoverSiguiente(ultima.cursoId());
    }

    private Alumno promoverSiguiente(int cursoId) throws SQLException {
        Queue<Alumno> cola = listasEspera.get(cursoId);
        if (cola == null || cola.isEmpty()) {
            return null;
        }
        Alumno siguiente = cola.poll();
        try {
            inscribir(siguiente.getAlumnoId(), cursoId);
            System.out.println("Se inscribio desde la lista de espera a: "
                    + siguiente.getNombre() + " " + siguiente.getApellido());
            return siguiente;
        } catch (CupoExcepcion e) {
            // No deberia ocurrir tras una baja, pero se devuelve al frente de la cola por seguridad
            ((LinkedList<Alumno>) cola).addFirst(siguiente);
            return null;
        }
    }

    public Queue<Alumno> verListaEspera(int cursoId) {
        return listasEspera.getOrDefault(cursoId, new LinkedList<>());
    }
}
