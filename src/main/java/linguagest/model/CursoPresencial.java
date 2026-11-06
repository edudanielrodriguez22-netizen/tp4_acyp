package linguagest.model;

import java.time.LocalDate;
import java.util.List;

/**
 * Especializacion de Curso para la modalidad presencial.
 */
public class CursoPresencial extends Curso {

    private String aula;

    public CursoPresencial() {
        super();
        this.modalidad = "presencial";
    }

    public CursoPresencial(String idioma, String nivel, int cupoMaximo, String aula) {
        super(idioma, nivel, cupoMaximo, "presencial");
        this.aula = aula;
    }

    public CursoPresencial(int cursoId, String idioma, String nivel, int cupoMaximo, String aula) {
        super(cursoId, idioma, nivel, cupoMaximo, "presencial");
        this.aula = aula;
    }

    public String getAula() { return aula; }
    public void setAula(String aula) { this.aula = aula; }

    @Override
    public String getDescripcion() {
        return "Curso presencial de " + idioma + " " + nivel + " (aula: " + aula + ", cupo " + cupoMaximo + ")";
    }

    @Override
    public void registrarAsistencia(LocalDate fecha, List<Alumno> presentes, List<Alumno> inscriptos) {
        System.out.println("[CursoPresencial] Tomando asistencia en aula \"" + aula
                + "\" - " + idioma + " " + nivel + " - fecha " + fecha);
        for (Alumno a : inscriptos) {
            boolean estuvoPresente = presentes.stream().anyMatch(p -> p.getAlumnoId() == a.getAlumnoId());
            System.out.println("   " + a.getNombre() + " " + a.getApellido()
                    + " -> " + (estuvoPresente ? "PRESENTE" : "AUSENTE"));
        }
    }
}
