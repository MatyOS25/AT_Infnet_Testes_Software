package br.edu.infnet;

import java.util.Arrays;

public class MathFunctions
{
    public static int multiplyByTwo(int number)
    {
        return number * 2;
    }
    public static int[] generateMultiplicationTable(int number, int limit)
    {
        int[] result = new int[limit];
        for (int i = 0; i < limit; i++)
        {
            result[i] = number * (i + 1);
        }
        return result;
    }
    public static boolean isPrime(int number)
    {
        if (number <= 1) return false;
        for (int i = 2; i <= Math.sqrt(number); i++)
        {
            if (number % i == 0) return false;
        }
        return true;
    }
    public static double calculateAverage(int[] numbers) {
        if (numbers == null || numbers.length == 0) {
            throw new IllegalArgumentException("Array cannot be null or empty.");
        }
        return Arrays.stream(numbers).average().orElse(0.0);
    }
}
