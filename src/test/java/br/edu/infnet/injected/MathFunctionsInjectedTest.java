package br.edu.infnet.injected;

import br.edu.infnet.logger.MathLogger;
import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Arrays;

class MathFunctionsInjectedTest {

    @Property
    void multiplyByTwo_AlwaysReturnsEven(@ForAll int number) {
        MathLogger mockLogger = Mockito.mock(MathLogger.class);
        MathFunctionsInjected mathFunctions = new MathFunctionsInjected(mockLogger);
        
        int result = mathFunctions.multiplyByTwo(number);
        assertTrue(result % 2 == 0, "Result should always be even");
        verify(mockLogger).log("multiplyByTwo", new int[]{number});
    }

    @Property
    void generateMultiplicationTable_AllElementsAreMultiples(
            @ForAll @IntRange(min = 1, max = 100) int number,
            @ForAll @IntRange(min = 1, max = 10) int limit) {
        MathLogger mockLogger = Mockito.mock(MathLogger.class);
        MathFunctionsInjected mathFunctions = new MathFunctionsInjected(mockLogger);
        
        int[] table = mathFunctions.generateMultiplicationTable(number, limit);
        
        for (int i = 0; i < table.length; i++) {
            int expected = number * (i + 1);
            assertEquals(expected, table[i], 
                "Element at index " + i + " should be " + number + " * " + (i + 1));
        }
        verify(mockLogger).log("generateMultiplicationTable", new int[]{number, limit});
    }

    @Property
    void isPrime_NoDivisorsOtherThanOneAndItself(
            @ForAll("primeNumbers") int prime) {
        MathLogger mockLogger = Mockito.mock(MathLogger.class);
        MathFunctionsInjected mathFunctions = new MathFunctionsInjected(mockLogger);
        
        assertTrue(mathFunctions.isPrime(prime), prime + " should be prime");
        
        for (int i = 2; i <= Math.sqrt(prime); i++) {
            assertNotEquals(0, prime % i, 
                prime + " should not be divisible by " + i);
        }
        verify(mockLogger).log("isPrime", new int[]{prime});
    }

    @Property
    void calculateAverage_AlwaysBetweenMinAndMax(
            @ForAll @Size(min = 1, max = 100) int[] numbers) {
        MathLogger mockLogger = Mockito.mock(MathLogger.class);
        MathFunctionsInjected mathFunctions = new MathFunctionsInjected(mockLogger);
        
        double average = mathFunctions.calculateAverage(numbers);
        int min = Arrays.stream(numbers).min().getAsInt();
        int max = Arrays.stream(numbers).max().getAsInt();
        
        assertTrue(average >= min && average <= max,
            "Average " + average + " should be between " + min + " and " + max);
        verify(mockLogger).log("calculateAverage", numbers);
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
        MathLogger mockLogger = Mockito.mock(MathLogger.class);
        MathFunctionsInjected mathFunctions = new MathFunctionsInjected(mockLogger);
        
        assertThrows(IllegalArgumentException.class, () -> {
            mathFunctions.calculateAverage(null);
        });
        verify(mockLogger, never()).log(anyString(), any());
    }

    @Test
    void calculateAverage_ThrowsExceptionForEmptyArray() {
        MathLogger mockLogger = Mockito.mock(MathLogger.class);
        MathFunctionsInjected mathFunctions = new MathFunctionsInjected(mockLogger);
        
        assertThrows(IllegalArgumentException.class, () -> {
            mathFunctions.calculateAverage(new int[0]);
        });
        verify(mockLogger, never()).log(anyString(), any());
    }

    @Property
    void multiplyByTwo_CommutativeProperty(
            @ForAll int a, @ForAll int b) {
        MathLogger mockLogger = Mockito.mock(MathLogger.class);
        MathFunctionsInjected mathFunctions = new MathFunctionsInjected(mockLogger);
        
        int result1 = mathFunctions.multiplyByTwo(a + b);
        int result2 = mathFunctions.multiplyByTwo(a) + mathFunctions.multiplyByTwo(b);
        assertEquals(result1, result2, "multiplyByTwo should be distributive over addition");
        verify(mockLogger, times(3)).log(eq("multiplyByTwo"), any());
    }

    @Property
    void generateMultiplicationTable_SizeMatchesLimit(
            @ForAll @IntRange(min = 1, max = 100) int number,
            @ForAll @IntRange(min = 1, max = 10) int limit) {
        MathLogger mockLogger = Mockito.mock(MathLogger.class);
        MathFunctionsInjected mathFunctions = new MathFunctionsInjected(mockLogger);
        
        int[] table = mathFunctions.generateMultiplicationTable(number, limit);
        assertEquals(limit, table.length, "Table size should match limit");
        verify(mockLogger).log("generateMultiplicationTable", new int[]{number, limit});
    }

    @Property
    void isPrime_NonPrimeNumbers(
            @ForAll("nonPrimeNumbers") int nonPrime) {
        MathLogger mockLogger = Mockito.mock(MathLogger.class);
        MathFunctionsInjected mathFunctions = new MathFunctionsInjected(mockLogger);
        
        assertFalse(mathFunctions.isPrime(nonPrime), 
            nonPrime + " should not be prime");
        verify(mockLogger).log("isPrime", new int[]{nonPrime});
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