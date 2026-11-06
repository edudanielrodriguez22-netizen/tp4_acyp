package linguagest.mvc.modelo;

import java.time.LocalDate;

/** Entidad del modelo: una nota de un alumno en un curso (vista aplanada de la consulta con JOIN). */
public class Calificacion {

    private final int id;
    private final int alumnoId;
    private final String alumno;
    private final int cursoId;
    private final LocalDate fecha;
    private double nota;

    public Calificacion(int id, int alumnoId, String alumno, int cursoId, LocalDate fecha, double nota) {
        this.id = id;
        this.alumnoId = alumnoId;
        this.alumno = alumno;
        this.cursoId = cursoId;
        this.fecha = fecha;
        this.nota = nota;
    }

    public int getId() { return id; }
    public int getAlumnoId() { return alumnoId; }
    public String getAlumno() { return alumno; }
    public int getCursoId() { return cursoId; }
    public LocalDate getFecha() { return fecha; }
    public double getNota() { return nota; }
    public void setNota(double nota) { this.nota = nota; }

    @Override
    public String toString() {
        return String.format("#%d | %-22s | curso %d | %s | %.2f", id, alumno, cursoId, fecha, nota);
    }
}
