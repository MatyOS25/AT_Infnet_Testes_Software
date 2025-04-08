package br.edu.infnet.bubblesort;

import java.util.Arrays;
import java.util.List;

/**
 * Interface comum para algoritmos de ordenação
 */
public interface SortAlgorithm {
    /**
     * Método principal para ordenação de arrays
     *
     * @param unsorted - array a ser ordenado
     * @return array ordenado
     */
    <T extends Comparable<T>> T[] sort(T[] unsorted);

    /**
     * Método auxiliar para trabalhar com listas da JCF
     *
     * @param unsorted - lista a ser ordenada
     * @return lista ordenada
     */
    @SuppressWarnings("unchecked")
    default<T extends Comparable<T>> List<T> sort(List<T> unsorted) {
        return Arrays.asList(sort(unsorted.toArray((T[]) new Comparable[unsorted.size()])));
    }
} 