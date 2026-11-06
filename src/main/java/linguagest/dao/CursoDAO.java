package linguagest.dao;

import linguagest.db.ConexionBD;
import linguagest.model.Curso;
import linguagest.model.CursoPresencial;
import linguagest.model.CursoVirtual;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CursoDAO {

    public int insertar(Curso c) throws SQLException {
        String sql = "INSERT INTO cursos (idioma, nivel, cupo_maximo, modalidad) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getIdioma());
            ps.setString(2, c.getNivel());
            ps.setInt(3, c.getCupoMaximo());
            ps.setString(4, c.getModalidad());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public List<Curso> listarTodos() throws SQLException {
        String sql = "SELECT curso_id, idioma, nivel, cupo_maximo, modalidad FROM cursos";
        List<Curso> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    /** Consulta 4 del informe: cursos con cupo disponible. */
    public List<String> cursosConCupoDisponible() throws SQLException {
        String sql = "SELECT c.curso_id, c.idioma, c.nivel, c.cupo_maximo, " +
                "COUNT(i.inscripcion_id) AS inscriptos_actuales, " +
                "(c.cupo_maximo - COUNT(i.inscripcion_id)) AS lugares_disponibles " +
                "FROM cursos c " +
                "LEFT JOIN inscripciones i ON c.curso_id = i.curso_id AND i.estado = 'activa' " +
                "GROUP BY c.curso_id " +
                "HAVING lugares_disponibles > 0";
        List<String> resultado = new ArrayList<>();
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(rs.getInt("curso_id") + " | " + rs.getString("idioma") + " " + rs.getString("nivel")
                        + " | cupo: " + rs.getInt("cupo_maximo")
                        + " | inscriptos: " + rs.getInt("inscriptos_actuales")
                        + " | disponibles: " + rs.getInt("lugares_disponibles"));
            }
        }
        return resultado;
    }

    private Curso mapear(ResultSet rs) throws SQLException {
        int id = rs.getInt("curso_id");
        String idioma = rs.getString("idioma");
        String nivel = rs.getString("nivel");
        int cupo = rs.getInt("cupo_maximo");
        String modalidad = rs.getString("modalidad");
        if ("virtual".equalsIgnoreCase(modalidad)) {
            return new CursoVirtual(id, idioma, nivel, cupo, "Plataforma institucional");
        }
        return new CursoPresencial(id, idioma, nivel, cupo, "Aula sin asignar");
    }
}
