package linguagest.dao;

import linguagest.db.ConexionBD;
import linguagest.model.Alumno;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InscripcionDAO {

    /** Verifica si un curso tiene lugares disponibles antes de inscribir (regla de negocio). */
    public boolean tieneCupoDisponible(int cursoId) throws SQLException {
        String sql = "SELECT c.cupo_maximo, COUNT(i.inscripcion_id) AS ocupados " +
                "FROM cursos c LEFT JOIN inscripciones i " +
                "ON c.curso_id = i.curso_id AND i.estado = 'activa' " +
                "WHERE c.curso_id = ? GROUP BY c.cupo_maximo";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cursoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ocupados") < rs.getInt("cupo_maximo");
                }
            }
        }
        return false;
    }

    /** Inscribe un alumno a un curso. Devuelve el id de inscripcion generado. */
    public int inscribirAlumno(int alumnoId, int cursoId) throws SQLException {
        String sql = "INSERT INTO inscripciones (alumno_id, curso_id, fecha_inscripcion, estado) " +
                "VALUES (?, ?, ?, 'activa')";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, alumnoId);
            ps.setInt(2, cursoId);
            ps.setDate(3, Date.valueOf(LocalDate.now()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    /** Devuelve los alumnos inscriptos (activos) en un curso (Consulta 1 del informe). */
    public List<Alumno> listarInscriptosPorCurso(int cursoId) throws SQLException {
        String sql = "SELECT a.alumno_id, a.legajo, a.nombre, a.apellido, a.email " +
                "FROM alumnos a " +
                "JOIN inscripciones i ON a.alumno_id = i.alumno_id " +
                "WHERE i.curso_id = ? AND i.estado = 'activa'";
        List<Alumno> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cursoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Alumno(rs.getInt("alumno_id"), rs.getString("legajo"),
                            rs.getString("nombre"), rs.getString("apellido"), rs.getString("email")));
                }
            }
        }
        return lista;
    }

    /** Devuelve el id de inscripcion de un alumno en un curso (necesario para asistencias/calificaciones). */
    public int obtenerInscripcionId(int alumnoId, int cursoId) throws SQLException {
        String sql = "SELECT inscripcion_id FROM inscripciones WHERE alumno_id = ? AND curso_id = ? AND estado = 'activa'";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, alumnoId);
            ps.setInt(2, cursoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("inscripcion_id");
            }
        }
        return -1;
    }

    /** Da de baja logica una inscripcion (se conserva el historial). */
    public void darDeBaja(int inscripcionId) throws SQLException {
        String sql = "UPDATE inscripciones SET estado = 'baja' WHERE inscripcion_id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, inscripcionId);
            ps.executeUpdate();
        }
    }
}
