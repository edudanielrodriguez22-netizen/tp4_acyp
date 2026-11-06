package linguagest.dao;

import linguagest.db.ConexionBD;
import linguagest.model.Alumno;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlumnoDAO {

    public int insertar(Alumno a) throws SQLException {
        String sql = "INSERT INTO alumnos (legajo, nombre, apellido, email) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getLegajo());
            ps.setString(2, a.getNombre());
            ps.setString(3, a.getApellido());
            ps.setString(4, a.getEmail());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public List<Alumno> listarTodos() throws SQLException {
        String sql = "SELECT alumno_id, legajo, nombre, apellido, email FROM alumnos";
        List<Alumno> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Alumno(rs.getInt("alumno_id"), rs.getString("legajo"),
                        rs.getString("nombre"), rs.getString("apellido"), rs.getString("email")));
            }
        }
        return lista;
    }

    public Alumno buscarPorId(int alumnoId) throws SQLException {
        String sql = "SELECT alumno_id, legajo, nombre, apellido, email FROM alumnos WHERE alumno_id = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, alumnoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Alumno(rs.getInt("alumno_id"), rs.getString("legajo"),
                            rs.getString("nombre"), rs.getString("apellido"), rs.getString("email"));
                }
            }
        }
        return null;
    }
}
