package linguagest.mvc.modelo;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Persistencia en archivos de texto (java.io). Exporta calificaciones a CSV,
 * lee un CSV de importacion y deja un registro de las filas rechazadas.
 * Formato de importacion (separador ';'): alumno_id;curso_id;AAAA-MM-DD;nota
 */
public class ArchivoCalificaciones {

    /** Fila ya validada de un CSV de importacion. */
    public record Fila(int alumnoId, int cursoId, LocalDate fecha, double nota) { }

    /** Escribe las calificaciones en un CSV con BufferedWriter (se cierra solo: try-with-resources). */
    public void exportar(ArrayList<Calificacion> lista, File destino) throws IOException {
        File carpeta = destino.getAbsoluteFile().getParentFile();
        if (carpeta != null && !carpeta.exists() && !carpeta.mkdirs()) {
            throw new IOException("No se pudo crear la carpeta " + carpeta);
        }
        try (BufferedWriter out = new BufferedWriter(new FileWriter(destino))) {
            out.write("calificacion_id;alumno_id;alumno;curso_id;fecha;nota");
            out.newLine();
            for (Calificacion c : lista) {
                out.write(c.getId() + ";" + c.getAlumnoId() + ";" + c.getAlumno() + ";" +
                        c.getCursoId() + ";" + c.getFecha() + ";" + c.getNota());
                out.newLine();
            }
        }
    }

    /**
     * Lee un CSV con BufferedReader. Las filas validas se devuelven en un ArrayList;
     * las invalidas se agregan (con el motivo) a la lista 'rechazadas'.
     */
    public ArrayList<Fila> leerImportacion(File origen, ArrayList<String> rechazadas) throws IOException {
        ArrayList<Fila> filas = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new FileReader(origen))) {
            String linea;
            int numero = 0;
            while ((linea = in.readLine()) != null) {
                numero++;
                if (linea.isBlank() || linea.startsWith("#")) continue;
                String[] campos = linea.split(";");
                try {
                    if (campos.length != 4) throw new IllegalArgumentException("se esperaban 4 campos");
                    filas.add(new Fila(Integer.parseInt(campos[0].trim()), Integer.parseInt(campos[1].trim()),
                            LocalDate.parse(campos[2].trim()), Double.parseDouble(campos[3].trim().replace(',', '.'))));
                } catch (IllegalArgumentException | java.time.DateTimeException e) {
                    rechazadas.add("Linea " + numero + " (" + linea + "): " + e.getMessage());
                }
            }
        }
        return filas;
    }

    /** Agrega lineas al archivo de errores (modo append). */
    public void registrarErrores(ArrayList<String> errores, File log) throws IOException {
        if (errores.isEmpty()) return;
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(log, true)))) {
            out.println("--- Importacion " + java.time.LocalDateTime.now().withNano(0) + " ---");
            for (String e : errores) out.println(e);
        }
    }
}
