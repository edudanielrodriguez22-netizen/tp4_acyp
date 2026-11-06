package linguagest.control;

import linguagest.dao.AsistenciaDAO;
import linguagest.dao.InscripcionDAO;
import linguagest.model.Alumno;
import linguagest.model.Curso;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Clase de control: coordina el caso de uso "Registrar asistencia",
 * tal como se modelo en el diagrama de secuencia de la Etapa 2.
 */
public class GestorAsistencia {

    private final InscripcionDAO inscripcionDAO = new InscripcionDAO();
    private final AsistenciaDAO asistenciaDAO = new AsistenciaDAO();

    /**
     * Solicita el listado de inscriptos, delega en el Curso (entidad) la
     * responsabilidad de "tomar" la asistencia segun su modalidad, y persiste
     * el resultado en la base de datos.
     */
    public void registrarAsistencia(Curso curso, LocalDate fecha, List<Alumno> presentes) throws SQLException {
        List<Alumno> inscriptos = inscripcionDAO.listarInscriptosPorCurso(curso.getCursoId());

        if (inscriptos.isEmpty()) {
            System.out.println("El curso no tiene alumnos inscriptos activos.");
            return;
        }

        // Mensaje polimorfico: cada subclase de Curso decide como "mostrar" la toma de asistencia
        curso.registrarAsistencia(fecha, presentes, inscriptos);

        // Persistencia real en la base de datos
        for (Alumno a : inscriptos) {
            int inscripcionId = inscripcionDAO.obtenerInscripcionId(a.getAlumnoId(), curso.getCursoId());
            boolean estuvoPresente = presentes.stream().anyMatch(p -> p.getAlumnoId() == a.getAlumnoId());
            asistenciaDAO.registrar(inscripcionId, fecha, estuvoPresente);
        }
        System.out.println("Asistencia guardada correctamente en la base de datos.\n");
    }
}
