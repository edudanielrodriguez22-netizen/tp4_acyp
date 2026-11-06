package linguagest.control;

import linguagest.dao.AlumnoDAO;
import linguagest.model.Alumno;
import linguagest.util.Algoritmos;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Clase de control: ordenacion y busqueda de alumnos aplicando
 * los algoritmos de la clase Algoritmos.
 */
public class GestorAlumnos {

    private final AlumnoDAO alumnoDAO = new AlumnoDAO();

    private static final Comparator<Alumno> POR_APELLIDO_Y_NOMBRE =
            Comparator.comparing(Alumno::getApellido, String.CASE_INSENSITIVE_ORDER)
                      .thenComparing(Alumno::getNombre, String.CASE_INSENSITIVE_ORDER);

    private static final Comparator<Alumno> SOLO_APELLIDO =
            Comparator.comparing(Alumno::getApellido, String.CASE_INSENSITIVE_ORDER);

    public List<Alumno> listarOrdenadosPorApellido() throws SQLException {
        List<Alumno> alumnos = alumnoDAO.listarTodos();
        Algoritmos.ordenarPorInsercion(alumnos, POR_APELLIDO_Y_NOMBRE);
        return alumnos;
    }

    /** Busca por apellido con busqueda binaria y devuelve todas las coincidencias. */
    public List<Alumno> buscarPorApellido(String apellido) throws SQLException {
        List<Alumno> ordenados = listarOrdenadosPorApellido();
        Alumno clave = new Alumno();
        clave.setApellido(apellido);

        List<Alumno> coincidencias = new ArrayList<>();
        int pos = Algoritmos.busquedaBinaria(ordenados, clave, SOLO_APELLIDO);
        if (pos == -1) {
            return coincidencias;
        }
        // La busqueda binaria devuelve una posicion cualquiera: se expande a ambos lados
        int desde = pos;
        while (desde > 0 && SOLO_APELLIDO.compare(ordenados.get(desde - 1), clave) == 0) desde--;
        int hasta = pos;
        while (hasta < ordenados.size() - 1 && SOLO_APELLIDO.compare(ordenados.get(hasta + 1), clave) == 0) hasta++;
        for (int i = desde; i <= hasta; i++) {
            coincidencias.add(ordenados.get(i));
        }
        return coincidencias;
    }
}
