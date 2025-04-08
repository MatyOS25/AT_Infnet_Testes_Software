package br.edu.infnet;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.stream.IntStream;

class MathFunctionsTest {

    @Property
    void multiplyByTwo_AlwaysReturnsEven(@ForAll int number) {
        int result = MathFunctions.multiplyByTwo(number);
        assertTrue(result % 2 == 0, "Result should always be even");
    }

    @Property
    void generateMultiplicationTable_AllElementsAreMultiples(
            @ForAll @IntRange(min = 1, max = 100) int number,
            @ForAll @IntRange(min = 1, max = 10) int limit) {
        int[] table = MathFunctions.generateMultiplicationTable(number, limit);
        
        for (int i = 0; i < table.length; i++) {
            int expected = number * (i + 1);
            assertEquals(expected, table[i], 
                "Element at index " + i + " should be " + number + " * " + (i + 1));
        }
    }

    @Property
    void isPrime_NoDivisorsOtherThanOneAndItself(
            @ForAll("primeNumbers") int prime) {
        assertTrue(MathFunctions.isPrime(prime), prime + " should be prime");
        
        // Verifica que não há divisores entre 2 e sqrt(prime)
        for (int i = 2; i <= Math.sqrt(prime); i++) {
            assertNotEquals(0, prime % i, 
                prime + " should not be divisible by " + i);
        }
    }

    @Property
    void calculateAverage_AlwaysBetweenMinAndMax(
            @ForAll @Size(min = 1, max = 100) int[] numbers) {
        double average = MathFunctions.calculateAverage(numbers);
        int min = Arrays.stream(numbers).min().getAsInt();
        int max = Arrays.stream(numbers).max().getAsInt();
        
        assertTrue(average >= min && average <= max,
            "Average " + average + " should be between " + min + " and " + max);
    }

    @Provide
    Arbitrary<Integer> primeNumbers() {
        return Arbitraries.integers()
            .between(2, 1000)
            .filter(n -> {
                if (n <= 1) return false;
                for (int i = 2; i <= Math.sqrt(n); i++) {
                    if (n % i == 0) return false;
                }
                return true;
            });
    }

    @Test
    void calculateAverage_ThrowsExceptionForNullArray() {
        assertThrows(IllegalArgumentException.class, () -> {
            MathFunctions.calculateAverage(null);
        });
    }

    @Test
    void calculateAverage_ThrowsExceptionForEmptyArray() {
        assertThrows(IllegalArgumentException.class, () -> {
            MathFunctions.calculateAverage(new int[0]);
        });
    }

    @Property
    void multiplyByTwo_CommutativeProperty(
            @ForAll int a, @ForAll int b) {
        int result1 = MathFunctions.multiplyByTwo(a + b);
        int result2 = MathFunctions.multiplyByTwo(a) + MathFunctions.multiplyByTwo(b);
        assertEquals(result1, result2, "multiplyByTwo should be distributive over addition");
    }

    @Property
    void generateMultiplicationTable_SizeMatchesLimit(
            @ForAll @IntRange(min = 1, max = 100) int number,
            @ForAll @IntRange(min = 1, max = 10) int limit) {
        int[] table = MathFunctions.generateMultiplicationTable(number, limit);
        assertEquals(limit, table.length, "Table size should match limit");
    }

    @Property
    void isPrime_NonPrimeNumbers(
            @ForAll("nonPrimeNumbers") int nonPrime) {
        assertFalse(MathFunctions.isPrime(nonPrime), 
            nonPrime + " should not be prime");
    }

    @Provide
    Arbitrary<Integer> nonPrimeNumbers() {
        return Arbitraries.integers()
            .between(4, 1000)
            .filter(n -> {
                for (int i = 2; i <= Math.sqrt(n); i++) {
                    if (n % i == 0) return true;
                }
                return false;
            });
    }
} 