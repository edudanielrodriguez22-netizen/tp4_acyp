package linguagest.model;

/**
 * Clase de entidad: representa un alumno inscripto en el instituto.
 */
public class Alumno implements Describible {

    private int alumnoId;
    private String legajo;
    private String nombre;
    private String apellido;
    private String email;

    public Alumno() { }

    public Alumno(String legajo, String nombre, String apellido, String email) {
        this.legajo = legajo;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
    }

    public Alumno(int alumnoId, String legajo, String nombre, String apellido, String email) {
        this(legajo, nombre, apellido, email);
        this.alumnoId = alumnoId;
    }

    public int getAlumnoId() { return alumnoId; }
    public void setAlumnoId(int alumnoId) { this.alumnoId = alumnoId; }

    public String getLegajo() { return legajo; }
    public void setLegajo(String legajo) { this.legajo = legajo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDatosContacto() {
        return nombre + " " + apellido + " <" + email + ">";
    }

    @Override
    public String getDescripcion() {
        return "Alumno " + legajo + " - " + nombre + " " + apellido;
    }

    @Override
    public String toString() {
        return alumnoId + " | " + legajo + " | " + nombre + " " + apellido + " | " + email;
    }
}
