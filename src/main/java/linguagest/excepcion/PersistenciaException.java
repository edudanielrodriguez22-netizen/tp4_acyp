package linguagest.excepcion;

/**
 * Excepcion propia de la capa de persistencia. Encapsula las SQLException de JDBC
 * para que la vista y el controlador no dependan de java.sql (bajo acoplamiento)
 * y para entregar al usuario un mensaje comprensible.
 */
public class PersistenciaException extends Exception {

    private static final long serialVersionUID = 1L;

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    public PersistenciaException(String mensaje) {
        super(mensaje);
    }
}
