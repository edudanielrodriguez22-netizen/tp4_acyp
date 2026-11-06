package linguagest.model;

import java.time.LocalDate;
import java.util.List;

/**
 * Clase de entidad abstracta: generaliza el comportamiento comun
 * de un curso de idiomas. CursoPresencial y CursoVirtual la especializan.
 */
public abstract class Curso implements Describible {

    protected int cursoId;
    protected String idioma;
    protected String nivel;
    protected int cupoMaximo;
    protected String modalidad;

    public Curso() { }

    public Curso(String idioma, String nivel, int cupoMaximo, String modalidad) {
        this.idioma = idioma;
        this.nivel = nivel;
        this.cupoMaximo = validarCupo(cupoMaximo);
        this.modalidad = modalidad;
    }

    /** Encapsulamiento: el cupo nunca puede ser menor o igual a cero. */
    private static int validarCupo(int cupo) {
        if (cupo <= 0) {
            throw new IllegalArgumentException("El cupo maximo debe ser mayor a cero.");
        }
        return cupo;
    }

    public Curso(int cursoId, String idioma, String nivel, int cupoMaximo, String modalidad) {
        this(idioma, nivel, cupoMaximo, modalidad);
        this.cursoId = cursoId;
    }

    public int getCursoId() { return cursoId; }
    public void setCursoId(int cursoId) { this.cursoId = cursoId; }

    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public int getCupoMaximo() { return cupoMaximo; }
    public void setCupoMaximo(int cupoMaximo) { this.cupoMaximo = validarCupo(cupoMaximo); }

    public String getModalidad() { return modalidad; }

    /**
     * Cada modalidad de curso registra la asistencia de forma distinta
     * (por eso es un metodo polimorfico redefinido en las subclases).
     * fecha: fecha de la clase. presentes: alumnos marcados como presentes.
     */
    public abstract void registrarAsistencia(LocalDate fecha, List<Alumno> presentes, List<Alumno> inscriptos);

    @Override
    public String toString() {
        return cursoId + " | " + idioma + " - " + nivel + " (" + modalidad + ", cupo " + cupoMaximo + ")";
    }
}
