package linguagest.model;

/**
 * Interfaz: define un contrato de comportamiento ("poder describirse") que
 * pueden cumplir clases sin relacion de herencia entre si (Curso y Alumno).
 * Una interfaz especifica QUE se debe hacer, no COMO.
 */
public interface Describible {

    String getDescripcion();
}
