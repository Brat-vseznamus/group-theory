package group;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import group.elements.Cycle;

public class CyclicGroup extends Group<Cycle> {

    public CyclicGroup(int size) {
        super(size);
    }

    @Override
    Cycle transform(int n) {
        return new Cycle(n % getSize());
    }

    @Override
    int deTransform(Cycle element) {
        return element.getC();
    }

    @Override
    Cycle rule(Cycle e1, Cycle e2) {
        return new Cycle((e1.getC() + e2.getC()) % getSize());
    }

    @Override
    public Map<Integer, Set<Group<Cycle>>> allSubGroups() {
        int n = getSize();
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
        return divisors.stream()
            .map(d -> 
                subgroup(IntStream.range(0, d)
                            .map(i -> i * n / d)
                            .boxed()
                            .collect(Collectors.toList())))
            .collect(Collectors.groupingBy(
                Group<Cycle>::getSize, 
                Collectors.toSet()));
    }

}
