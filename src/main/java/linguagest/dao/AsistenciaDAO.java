package linguagest.dao;

import linguagest.db.ConexionBD;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AsistenciaDAO {

    public void registrar(int inscripcionId, LocalDate fecha, boolean presente) throws SQLException {
        String sql = "INSERT INTO asistencias (inscripcion_id, fecha_clase, presente) VALUES (?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, inscripcionId);
            ps.setDate(2, Date.valueOf(fecha));
            ps.setBoolean(3, presente);
            ps.executeUpdate();
        }
    }

    /** Historial de asistencia de un alumno (Consulta 2 del informe). */
    public List<String> historialPorAlumno(int alumnoId) throws SQLException {
        String sql = "SELECT al.nombre, al.apellido, c.idioma, ast.fecha_clase, ast.presente " +
                "FROM asistencias ast " +
                "JOIN inscripciones i ON ast.inscripcion_id = i.inscripcion_id " +
                "JOIN alumnos al ON i.alumno_id = al.alumno_id " +
                "JOIN cursos c ON i.curso_id = c.curso_id " +
                "WHERE al.alumno_id = ?";
        List<String> resultado = new ArrayList<>();
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, alumnoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(rs.getString("nombre") + " " + rs.getString("apellido")
                            + " | " + rs.getString("idioma")
                            + " | " + rs.getDate("fecha_clase")
                            + " | presente: " + rs.getBoolean("presente"));
                }
            }
        }
        return resultado;
    }
}
