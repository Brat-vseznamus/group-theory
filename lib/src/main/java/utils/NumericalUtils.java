package utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NumericalUtils {

    private NumericalUtils() {}
    
    public static Set<Integer> allDivisors(int number) {
        int n = number;
        Set<Integer> halfOfDivisors = IntStream
            .range(1, (int)Math.floor(Math.sqrt(n)) + 1)
            .filter(i -> n % i == 0)
            .boxed()
            .collect(Collectors.toSet());
        Set<Integer> divisors = new HashSet<>();
        for (int d : halfOfDivisors) {
            divisors.add(d);
            divisors.add(n / d);
        }
        return divisors;
    }

    public static Map<Integer, Integer> factorization(int number) {
        if (number <= 0) {
            throw new IllegalArgumentException("number must be positive");
        }
        return factorization(number, 2);
    }

    private static Map<Integer, Integer> factorization(int number, int d) {
        if (number == 1) {
            return new HashMap<>(Map.of());
        }
        int e = 0;
        while (number % d != 0) {
            d++;
        }
        while (number % d == 0) {
            number /= d;
            e++;
        }   
        Map<Integer, Integer> nextFactor = factorization(number, d);
        nextFactor.put(d, e);
        return nextFactor;
    }
}
