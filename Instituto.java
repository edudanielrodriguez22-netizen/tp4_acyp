package linguagest.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase de entidad: agrega la coleccion de cursos ofrecidos por el instituto.
 */
public class Instituto {

    private String nombre;
    private List<Curso> cursos = new ArrayList<>();

    public Instituto(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() { return nombre; }

    public void agregarCurso(Curso c) {
        cursos.add(c);
    }

    public List<Curso> listarCursos() {
        return cursos;
    }

    public List<Curso> buscarPorIdioma(String idioma) {
        List<Curso> resultado = new ArrayList<>();
        for (Curso c : cursos) {
            if (c.getIdioma().equalsIgnoreCase(idioma)) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    public boolean hayCursosDisponibles() {
        return !cursos.isEmpty();
    }
}
