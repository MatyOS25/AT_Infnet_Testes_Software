package br.edu.infnet.injected;

import br.edu.infnet.logger.MathLogger;
import java.util.Arrays;

public class MathFunctionsInjected {
    private final MathLogger logger;

    public MathFunctionsInjected(MathLogger logger) {
        this.logger = logger;
    }

    public int multiplyByTwo(int number) {
        logger.log("multiplyByTwo", new int[]{number});
        return number * 2;
    }

    public int[] generateMultiplicationTable(int number, int limit) {
        logger.log("generateMultiplicationTable", new int[]{number, limit});
        int[] result = new int[limit];
        for (int i = 0; i < limit; i++) {
            result[i] = number * (i + 1);
        }
        return result;
    }

    public boolean isPrime(int number) {
        logger.log("isPrime", new int[]{number});
        if (number <= 1) return false;
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) return false;
        }
        return true;
    }

    public double calculateAverage(int[] numbers) {
        if (numbers == null || numbers.length == 0) {
            throw new IllegalArgumentException("Array cannot be null or empty.");
        }
        logger.log("calculateAverage", numbers);
        return Arrays.stream(numbers).average().orElse(0.0);
    }
} 