package linguagest.mvc.vista;

import linguagest.mvc.modelo.Calificacion;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

/** VISTA por consola: solo lee y muestra datos; no contiene logica de negocio ni de base de datos. */
public class VistaConsola implements VistaCalificaciones {

    private final Scanner sc;

    public VistaConsola(Scanner sc) {
        this.sc = sc;
    }

    @Override
    public int mostrarMenu() {
        System.out.println("\n--- Modulo Calificaciones (MVC) ---");
        System.out.println("1. Listar calificaciones de un curso");
        System.out.println("2. Registrar calificacion");
        System.out.println("3. Corregir nota");
        System.out.println("4. Eliminar calificacion");
        System.out.println("5. Estadisticas del curso");
        System.out.println("6. Exportar calificaciones a archivo CSV");
        System.out.println("7. Importar calificaciones desde archivo CSV");
        System.out.println("0. Volver al menu principal");
        return pedirEntero("Opcion: ");
    }

    @Override
    public int pedirEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                mostrarError("Debe ingresar un numero entero.");
            }
        }
    }

    @Override
    public double pedirNota(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Double.parseDouble(sc.nextLine().trim().replace(',', '.'));
            } catch (NumberFormatException e) {
                mostrarError("Debe ingresar un numero (ej: 7.5).");
            }
        }
    }

    @Override
    public LocalDate pedirFecha(String mensaje) {
        while (true) {
            System.out.print(mensaje + " (AAAA-MM-DD, vacio = hoy): ");
            String texto = sc.nextLine().trim();
            if (texto.isEmpty()) return LocalDate.now();
            try {
                return LocalDate.parse(texto);
            } catch (DateTimeParseException e) {
                mostrarError("Fecha invalida. Formato esperado: AAAA-MM-DD.");
            }
        }
    }

    @Override
    public String pedirTexto(String mensaje) {
        System.out.print(mensaje);
        return sc.nextLine().trim();
    }

    @Override
    public void mostrarCalificaciones(ArrayList<Calificacion> lista) {
        if (lista.isEmpty()) {
            System.out.println("Sin calificaciones para mostrar.");
            return;
        }
        System.out.println("  ID | Alumno                 | Curso   | Fecha      | Nota");
        for (Calificacion c : lista) System.out.println(c);
        System.out.println("Total: " + lista.size());
    }

    @Override
    public void mostrarEstadisticas(String[] rangos, int[] cantidades, Double promedio) {
        System.out.println("Promedio: " + (promedio == null ? "sin datos" : String.format("%.2f", promedio)));
        System.out.println("Distribucion de notas:");
        for (int i = 0; i < rangos.length; i++) {
            System.out.printf("  %-9s | %-10s %d%n", rangos[i], "#".repeat(cantidades[i]), cantidades[i]);
        }
    }

    @Override
    public void mostrarMensaje(String mensaje) { System.out.println(mensaje); }

    @Override
    public void mostrarError(String mensaje) { System.out.println("Error: " + mensaje); }
}
