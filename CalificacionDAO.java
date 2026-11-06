package linguagest.dao;

import linguagest.db.ConexionBD;

import java.sql.*;
import java.time.LocalDate;

public class CalificacionDAO {

    /** Carga una nota. Si la BD rechaza el valor (CHECK chk_nota_rango), se propaga la SQLException. */
    public void cargarNota(int inscripcionId, LocalDate fechaEvaluacion, double nota) throws SQLException {
        String sql = "INSERT INTO calificaciones (inscripcion_id, fecha_evaluacion, nota) VALUES (?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, inscripcionId);
            ps.setDate(2, Date.valueOf(fechaEvaluacion));
            ps.setDouble(3, nota);
            ps.executeUpdate();
        }
    }

    /** Promedio de notas por curso (Consulta 3 del informe). */
    public Double promedioPorCurso(int cursoId) throws SQLException {
        String sql = "SELECT AVG(cal.nota) AS promedio " +
                "FROM calificaciones cal " +
                "JOIN inscripciones i ON cal.inscripcion_id = i.inscripcion_id " +
                "WHERE i.curso_id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cursoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double promedio = rs.getDouble("promedio");
                    return rs.wasNull() ? null : promedio;
                }
            }
        }
        return null;
    }
}
