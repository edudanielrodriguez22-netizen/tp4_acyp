package linguagest.util;

import java.util.Comparator;
import java.util.List;

/**
 * Algoritmos de ordenacion y busqueda implementados a mano
 * (sin usar Collections.sort ni Collections.binarySearch).
 */
public final class Algoritmos {

    private Algoritmos() { }

    /** Ordenacion por insercion: estable y simple para listas pequenas o medianas. */
    public static <T> void ordenarPorInsercion(List<T> lista, Comparator<T> comparador) {
        for (int i = 1; i < lista.size(); i++) {
            T actual = lista.get(i);
            int j = i - 1;
            while (j >= 0 && comparador.compare(lista.get(j), actual) > 0) {
                lista.set(j + 1, lista.get(j));
                j--;
            }
            lista.set(j + 1, actual);
        }
    }

    /**
     * Busqueda binaria sobre una lista YA ORDENADA con el mismo comparador.
     * Devuelve la posicion de un elemento coincidente, o -1 si no existe.
     */
    public static <T> int busquedaBinaria(List<T> lista, T clave, Comparator<T> comparador) {
        int inicio = 0;
        int fin = lista.size() - 1;
        while (inicio <= fin) {
            int medio = (inicio + fin) / 2;
            int cmp = comparador.compare(lista.get(medio), clave);
            if (cmp == 0) {
                return medio;
            } else if (cmp < 0) {
                inicio = medio + 1;
            } else {
                fin = medio - 1;
            }
        }
        return -1;
    }
}
