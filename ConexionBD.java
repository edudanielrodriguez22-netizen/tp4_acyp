package linguagest.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase utilitaria que centraliza la conexion JDBC a la base de datos MySQL,
 * leyendo los parametros desde config.properties (classpath).
 */
public class ConexionBD {

    private static final Properties props = new Properties();

    static {
        try (InputStream in = ConexionBD.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new RuntimeException("No se encontro config.properties en el classpath");
            }
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo config.properties", e);
        }
    }

    public static Connection obtenerConexion() throws SQLException {
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String pass = props.getProperty("db.password");
        return DriverManager.getConnection(url, user, pass);
    }
}
