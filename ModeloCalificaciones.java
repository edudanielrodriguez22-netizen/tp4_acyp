package linguagest.mvc.modelo;

import linguagest.db.ConexionBD;
import linguagest.excepcion.PersistenciaException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * MODELO del patron MVC. Es el unico que conoce JDBC: abre conexiones, ejecuta
 * consultas parametrizadas y traduce las SQLException a PersistenciaException.
 * No imprime ni lee nada de pantalla.
 */
public class ModeloCalificaciones {

    /** Etiquetas de los rangos del histograma (arreglo complementario al ArrayList). */
    public static final String[] RANGOS = {"0 - 3.99", "4 - 5.99", "6 - 7.99", "8 - 10"};

    /** Consulta (SELECT + JOIN): calificaciones de un curso, cargadas en un ArrayList. */
    public ArrayList<Calificacion> listarPorCurso(int cursoId) throws PersistenciaException {
        String sql = "SELECT cal.calificacion_id, a.alumno_id, CONCAT(a.apellido, ', ', a.nombre) AS alumno, " +
                "i.curso_id, cal.fecha_evaluacion, cal.nota " +
                "FROM calificaciones cal " +
                "JOIN inscripciones i ON cal.inscripcion_id = i.inscripcion_id " +
                "JOIN alumnos a ON i.alumno_id = a.alumno_id " +
                "WHERE i.curso_id = ? ORDER BY a.apellido, cal.fecha_evaluacion";
        ArrayList<Calificacion> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cursoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Calificacion(
                            rs.getInt("calificacion_id"), rs.getInt("alumno_id"),
                            rs.getString("alumno"), rs.getInt("curso_id"),
                            rs.getDate("fecha_evaluacion").toLocalDate(), rs.getDouble("nota")));
                }
            }
        } catch (SQLException e) {
            throw traducir("No se pudieron consultar las calificaciones del curso " + cursoId, e);
        }
        return lista;
    }

    /** INSERT: registra una nota para un alumno inscripto activamente en el curso. */
    public int registrar(int alumnoId, int cursoId, LocalDate fecha, double nota) throws PersistenciaException {
        String buscar = "SELECT inscripcion_id FROM inscripciones " +
                "WHERE alumno_id = ? AND curso_id = ? AND estado = 'activa'";
        String insertar = "INSERT INTO calificaciones (inscripcion_id, fecha_evaluacion, nota) VALUES (?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion()) {
            int inscripcionId;
            try (PreparedStatement ps = con.prepareStatement(buscar)) {
                ps.setInt(1, alumnoId);
                ps.setInt(2, cursoId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new PersistenciaException(
                                "El alumno " + alumnoId + " no esta inscripto activamente en el curso " + cursoId + ".");
                    }
                    inscripcionId = rs.getInt(1);
                }
            }
            try (PreparedStatement ps = con.prepareStatement(insertar, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, inscripcionId);
                ps.setDate(2, Date.valueOf(fecha));
                ps.setDouble(3, nota);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    return keys.next() ? keys.getInt(1) : -1;
                }
            }
        } catch (SQLException e) {
            throw traducir("No se pudo registrar la calificacion", e);
        }
    }

    /** UPDATE: corrige una nota existente. Devuelve false si el id no existe. */
    public boolean actualizarNota(int calificacionId, double nuevaNota) throws PersistenciaException {
        String sql = "UPDATE calificaciones SET nota = ? WHERE calificacion_id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, nuevaNota);
            ps.setInt(2, calificacionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw traducir("No se pudo actualizar la calificacion " + calificacionId, e);
        }
    }

    /** DELETE: elimina una calificacion. Devuelve false si el id no existe. */
    public boolean eliminar(int calificacionId) throws PersistenciaException {
        String sql = "DELETE FROM calificaciones WHERE calificacion_id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, calificacionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw traducir("No se pudo eliminar la calificacion " + calificacionId, e);
        }
    }

    /** Calcula, con un arreglo int[], cuantas notas caen en cada rango de RANGOS. */
    public int[] distribucion(ArrayList<Calificacion> calificaciones) {
        int[] cantidades = new int[RANGOS.length];
        for (Calificacion c : calificaciones) {
            double n = c.getNota();
            int pos = n < 4 ? 0 : n < 6 ? 1 : n < 8 ? 2 : 3;
            cantidades[pos]++;
        }
        return cantidades;
    }

    /** Promedio calculado en memoria sobre el ArrayList; null si la lista esta vacia. */
    public Double promedio(ArrayList<Calificacion> calificaciones) {
        if (calificaciones.isEmpty()) return null;
        double suma = 0;
        for (Calificacion c : calificaciones) suma += c.getNota();
        return suma / calificaciones.size();
    }

    /** Convierte la SQLException en un mensaje de dominio segun su causa. */
    private PersistenciaException traducir(String contexto, SQLException e) {
        String estado = e.getSQLState() == null ? "" : e.getSQLState();
        String detalle;
        if (estado.startsWith("08")) {
            detalle = "no hay conexion con la base de datos (revisar que MySQL este activo y config.properties)";
        } else if (e.getErrorCode() == 3819 || e.getErrorCode() == 4025) {
            detalle = "la nota debe estar entre 0 y 10";
        } else if (estado.startsWith("23")) {
            detalle = "se violo una restriccion de integridad";
        } else if (estado.startsWith("28")) {
            detalle = "usuario o contrasena de MySQL incorrectos";
        } else {
            detalle = e.getMessage();
        }
        return new PersistenciaException(contexto + ": " + detalle, e);
    }
}
