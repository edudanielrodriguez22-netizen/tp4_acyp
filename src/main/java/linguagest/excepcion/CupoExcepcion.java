package linguagest.excepcion;

/**
 * Excepcion propia del dominio: se lanza cuando se intenta inscribir
 * un alumno en un curso que ya alcanzo su cupo maximo.
 */
public class CupoExcepcion extends Exception {

    private static final long serialVersionUID = 1L;

    private final int cursoId;

    public CupoExcepcion(String mensaje, int cursoId) {
        super(mensaje);
        this.cursoId = cursoId;
    }

    public int getCursoId() { return cursoId; }
}
