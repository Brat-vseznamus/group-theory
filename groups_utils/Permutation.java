package groups_utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Permutation {
    ArrayList<Integer> sequence;

    public Permutation(ArrayList<Integer> sequence) {
        if (!checkSequence(sequence)) {
            throw new IllegalArgumentException("wrong sequence of elements");
        }
        this.sequence = sequence;
    }

    public Permutation(List<Integer> sequence) {
        this(new ArrayList<>(sequence));
    }

    public Permutation apply(Permutation other) {
        if (size() != other.size()) {
            throw new IllegalArgumentException("permutations must have same sizes");
        }
        return new Permutation(
            other.sequence.stream()
                .map(i -> this.sequence.get(i - 1))
                .collect(Collectors.toList()));
    }
    
    private static boolean checkSequence(ArrayList<Integer> sequence) {
        int n = sequence.size();
        int[] numbers = new int[n];
        sequence.forEach(i -> {
            if (0 < i && i <= n) {
                numbers[i - 1] = 1;
            }
        });
        return Arrays.stream(numbers).allMatch(i -> i == 1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sequence);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Permutation other = (Permutation) obj;
        if (sequence == null) {
            if (other.sequence != null)
                return false;
        } else if (!sequence.equals(other.sequence))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return sequence.toString();
    }

    public int size() {
        return sequence.size();
    }

    public static int toInt(Permutation p) {
        final List<Integer> order = new LinkedList<>();
        int n = p.size();
        int factorial = IntStream.range(1, n + 1).reduce(1, (e, ac) -> e * ac);
        IntStream.range(1, n + 1).forEach(i -> order.add(i));

        int number = 0;
        for (int element : p.sequence) {
            int orderOfNumber = order.indexOf(element);
            factorial /= n--;
            number += factorial * orderOfNumber;
            order.remove((Integer)element);
        }
        return number;
    }

    public static Permutation fromInt(int n, int number) {
        int factorial = IntStream.range(1, n + 1).reduce(1, (e, ac) -> e * ac);
        final List<Integer> order = new LinkedList<>();
        IntStream.range(1, n + 1).forEach(i -> order.add(i));

        List<Integer> elements = new ArrayList<>();
        for (int step = 0; step < n; step++) {
            factorial /= (n - step);
            int numOrder = number / factorial;
            number = number % factorial;
            Integer el = order.get(numOrder);
            order.remove(el);
            elements.add(el);
        }
        return new Permutation(elements);
    }

    public static void main(String[] args) {
        List<Permutation> perms = List.of(
            new Permutation(List.of(1, 2, 3, 4)),
            new Permutation(List.of(1, 2, 4, 3)),
            new Permutation(List.of(1, 3, 2, 4)),
            new Permutation(List.of(1, 3, 4, 2)),
            new Permutation(List.of(2, 1, 3, 4)),
            new Permutation(List.of(4, 3, 2, 1))
        );

        for (Permutation p: perms) {
            System.out.println("Permutation: " + p);
            System.out.println("Number of p: " + Permutation.toInt(p));
            System.out.println("Perm from p: " + Permutation.fromInt(4, Permutation.toInt(p)));
            System.out.println("");
        }
    }
}
