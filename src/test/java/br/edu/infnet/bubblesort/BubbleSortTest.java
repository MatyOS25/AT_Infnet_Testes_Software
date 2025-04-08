package br.edu.infnet.bubblesort;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import java.util.Arrays;

/**
 * Testes unitários para o BubbleSort
 * 
 * Cobertura de decisões e ramificações:
 * 1. Loop externo (for i = 1 to size)
 * 2. Loop interno (for j = 0 to size-i)
 * 3. Condição de troca (if greater)
 * 4. Condição de otimização (if !swapped)
 * 
 * Condições extremas testadas:
 * 1. Arrays nulos
 * 2. Arrays vazios
 * 3. Arrays com elementos nulos
 * 4. Arrays muito grandes
 * 5. Arrays já ordenados
 * 6. Arrays em ordem reversa
 * 7. Arrays com elementos repetidos
 * 8. Arrays com elementos iguais
 */
public class BubbleSortTest {

    private BubbleSort bubbleSort;

    @BeforeEach
    public void setUp() {
        bubbleSort = new BubbleSort();
    }

    // Testes de casos básicos
    @Test
    @DisplayName("Deve ordenar array vazio")
    public void testEmptyArray() {
        Integer[] array = {};
        Integer[] result = (Integer[]) bubbleSort.sort(array);
        assertArrayEquals(new Integer[]{}, result);
    }

    @Test
    @DisplayName("Deve ordenar array com um elemento")
    public void testSingleElement() {
        Integer[] array = {1};
        Integer[] result = (Integer[]) bubbleSort.sort(array);
        assertArrayEquals(new Integer[]{1}, result);
    }

    @Test
    @DisplayName("Deve ordenar array com dois elementos")
    public void testTwoElements() {
        Integer[] array = {2, 1};
        Integer[] result = (Integer[]) bubbleSort.sort(array);
        assertArrayEquals(new Integer[]{1, 2}, result);
    }

    // Testes de cobertura de decisões
    @Test
    @DisplayName("Deve ordenar array desordenado - Cobertura do loop principal")
    public void testUnsortedArray() {
        Integer[] array = {5, 3, 1, 4, 2};
        Integer[] expected = {1, 2, 3, 4, 5};
        Integer[] result = (Integer[]) bubbleSort.sort(array);
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Não deve realizar trocas em array ordenado - Cobertura da otimização")
    public void testAlreadySorted() {
        Integer[] array = {1, 2, 3, 4, 5};
        Integer[] original = array.clone();
        Integer[] result = (Integer[]) bubbleSort.sort(array);
        assertArrayEquals(original, result);
        assertArrayEquals(original, array);
    }

    // Testes de ramificações
    @Test
    @DisplayName("Deve ordenar array em ordem reversa - Cobertura máxima de trocas")
    public void testReverseOrder() {
        Integer[] array = {5, 4, 3, 2, 1};
        Integer[] expected = {1, 2, 3, 4, 5};
        Integer[] result = (Integer[]) bubbleSort.sort(array);
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Deve manter elementos iguais - Cobertura da condição de troca")
    public void testEqualElements() {
        Integer[] array = {2, 2, 2, 2};
        Integer[] expected = {2, 2, 2, 2};
        Integer[] result = (Integer[]) bubbleSort.sort(array);
        assertArrayEquals(expected, result);
    }

    // Testes de robustez
    @Test
    @DisplayName("Deve lidar com array nulo")
    public void testNullArray() {
        try {
            Integer[] array = null;
            bubbleSort.<Integer>sort(array);
            fail("Deveria ter lançado NullPointerException");
        } catch (NullPointerException e) {
            // Esperado
            assertTrue(true, "NullPointerException lançada como esperado");
        }
    }

    @Test
    @DisplayName("Deve lidar com elementos nulos")
    public void testNullElements() {
        Integer[] array = {1, null, 3};
        try {
            bubbleSort.<Integer>sort(array);
            fail("Deveria ter lançado NullPointerException");
        } catch (NullPointerException e) {
            // Esperado
            assertTrue(true, "NullPointerException lançada como esperado");
        }
    }

    // Testes de tamanho
    @Test
    @DisplayName("Deve ordenar array pequeno")
    public void testSmallArray() {
        Integer[] array = {3, 1, 4, 1, 5, 9, 2, 6, 5, 3};
        Integer[] expected = {1, 1, 2, 3, 3, 4, 5, 5, 6, 9};
        Integer[] result = (Integer[]) bubbleSort.sort(array);
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Deve ordenar array médio")
    public void testMediumArray() {
        Integer[] array = new Integer[100];
        for (int i = 0; i < array.length; i++) {
            array[i] = 100 - i;
        }
        Integer[] expected = array.clone();
        Arrays.sort(expected);
        Integer[] result = (Integer[]) bubbleSort.sort(array);
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Deve ordenar array grande")
    public void testLargeArray() {
        Integer[] array = new Integer[1000];
        for (int i = 0; i < array.length; i++) {
            array[i] = 1000 - i;
        }
        Integer[] expected = array.clone();
        Arrays.sort(expected);
        
        long startTime = System.nanoTime();
        Integer[] result = (Integer[]) bubbleSort.sort(array);
        long endTime = System.nanoTime();
        
        assertArrayEquals(expected, result);
        
        long duration = (endTime - startTime) / 1_000_000;
        assertTrue(duration > 0, "A ordenação deve levar tempo mensurável");
    }

    // Testes de tipos diferentes
    @Test
    @DisplayName("Deve ordenar strings com caracteres especiais")
    public void testSpecialCharacters() {
        String[] array = {"ç", "á", "à", "ã", "â", "a"};
        String[] expected = {"a", "à", "á", "â", "ã", "ç"};
        String[] result = (String[]) bubbleSort.sort(array);
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Deve ordenar strings vazias e espaços")
    public void testEmptyAndSpaceStrings() {
        String[] array = {"  ", "", "abc", " ", "   ", "a"};
        String[] expected = {"", " ", "  ", "   ", "a", "abc"};
        String[] result = (String[]) bubbleSort.sort(array);
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Deve ordenar objetos customizados")
    public void testCustomObjects() {
        Person[] array = {
            new Person("Bob", 30),
            new Person("Alice", 25),
            new Person("Charlie", 35)
        };
        Person[] expected = {
            new Person("Alice", 25),
            new Person("Bob", 30),
            new Person("Charlie", 35)
        };
        Person[] result = (Person[]) bubbleSort.sort(array);
        assertArrayEquals(expected, result);
    }

    // Testes de estabilidade
    @Test
    @DisplayName("Deve manter a ordem relativa de elementos iguais")
    public void testStability() {
        Person[] array = {
            new Person("Alice", 25),
            new Person("Bob", 25),
            new Person("Charlie", 25)
        };
        Person[] result = (Person[]) bubbleSort.sort(array);
        assertEquals("Alice", result[0].name);
        assertEquals("Bob", result[1].name);
        assertEquals("Charlie", result[2].name);
    }

    // Testes de casos extremos adicionais
    @Test
    @DisplayName("Deve ordenar array com valores extremos")
    public void testExtremeValues() {
        Integer[] array = {Integer.MAX_VALUE, Integer.MIN_VALUE, 0, -1, 1};
        Integer[] expected = {Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE};
        Integer[] result = (Integer[]) bubbleSort.sort(array);
        assertArrayEquals(expected, result);
    }

    @Test
    @DisplayName("Deve ordenar array com elementos duplicados em posições críticas")
    public void testDuplicatesInCriticalPositions() {
        Integer[] array = {1, 1, 2, 2, 1, 1};
        Integer[] expected = {1, 1, 1, 1, 2, 2};
        Integer[] result = (Integer[]) bubbleSort.sort(array);
        assertArrayEquals(expected, result);
    }

    // Classe auxiliar para testes
    private static class Person implements Comparable<Person> {
        String name;
        int age;

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public int compareTo(Person other) {
            return Integer.compare(this.age, other.age);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Person person = (Person) obj;
            return age == person.age && name.equals(person.name);
        }
    }
} 