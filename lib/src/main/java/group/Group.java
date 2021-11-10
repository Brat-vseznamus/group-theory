package group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Group<T> {
    protected int size;
    protected List<Integer> elements;
    protected BinaryOperator<T> rule;
    protected Function<Integer, T> transform;
    protected Function<T, Integer> deTransform;

    protected Group(int size, BinaryOperator<T> rule) {
        this.size = size;
        this.rule = rule;
        this.elements = IntStream.range(0, size).boxed().collect(Collectors.toList());
    }

    public <S extends Group<T>> Group(S originalGroup, List<Integer> subgroup) {
        this.size = subgroup.size();
        this.rule = originalGroup.rule;
        this.elements = subgroup;
        this.transform = originalGroup.transform;
        this.deTransform = originalGroup.deTransform;
    }

    public Group(int size, BinaryOperator<T> rule, Function<Integer, T> transform, Function<T, Integer> deTransform) {
        this(size, rule);
        this.transform = transform;
        this.deTransform = deTransform;
    }

    public Map<Integer, Set<Group<T>>> allSubGroups() {
        Set<List<Integer>> subGroups = new HashSet<>();
        for (int mask = 1; mask < 1 << size; mask++) {
            Set<T> group = new HashSet<>();
            List<Integer> intels = new ArrayList<>();
            for (int el = 0; el < size; el++) {
                if ((1 << el & mask) == 1 << el) {
                    group.add(transform.apply(elements.get(el)));
                    intels.add(elements.get(el));
                }
            }
            Set<T> multiplies = new HashSet<>();
            for (T el1 : group) {
                for (T el2 : group) {
                    multiplies.add(rule.apply(el1, el2));
                }
            }
            if (group.containsAll(multiplies) && multiplies.containsAll(group)) {
                subGroups.add(intels);
            }
        }
        return subGroups.stream()
                .map(s -> new Group<T>(this, s))
                .collect(Collectors.groupingBy(Group<T>::getSize, Collectors.toSet()));
    }

    public boolean checkNormal() {
        Set<T> groupElements = elements.stream()
            .map(transform)
            .collect(Collectors.toSet());
        return elements.stream().allMatch(index -> {
            T g = transform.apply(index);
            Set<T> cosetLeft = leftCoset(groupElements, g);
            Set<T> cosetRight = rightCoset(groupElements, g);
            return cosetLeft.equals(cosetRight);
        });
    }

    private Set<T> leftCoset(Collection<T> groupElements, T g) {
        return groupElements.stream().map(s -> rule.apply(s, g)).collect(Collectors.toSet());
    }

    private Set<T> rightCoset(Collection<T> groupElements, T g) {
        return groupElements.stream().map(s -> rule.apply(g, s)).collect(Collectors.toSet());
    }

    // return map if isomorphic and null otherwise
    // TODO
    public <R> Map<Integer, Integer> isomorphism(Group<R> group) {
        if (this.size != group.size) {
            return null;
        }
        int n = this.size;
        int[][] m1 = new int[n][n];
        int[][] m2 = new int[n][n];
        int i = 0, j = 0;
        for (int e1 : this.elements) {
            for (int e2 : this.elements) {
                m1[i][j] = integerRule(e1, e2);
                j++;
            }
            i++;
        }
        i = j = 0;
        for (int e1 : group.elements) {
            for (int e2 : group.elements) {
                m2[i][j] = integerRule(e1, e2);
                j++;
            }
            i++;
        }
        var powers1 = allPowers();
        var powers2 = group.allPowers();
        for (Integer pow : powers1.keySet()) {
            if (powers1.get(pow).size() != powers2.getOrDefault(pow, Set.of()).size()) {
                return null;
            }
        }

        return null;
    }

    protected int getPowerOfElement(T element) {
        T e = transform.apply(0);
        int pw = 1;
        T ac = element;
        while (!elementEquals(e, ac)) {
            ac = rule.apply(ac, element);
            pw++;
        }
        return pw;
    }

    protected T getPower(T element, int pow) {
        assert pow >= 0;
        T ac = transform.apply(0);
        while (pow-- > 0) {
            ac = rule.apply(ac, element);
        }
        return ac;
    }

    protected int integerRule(int g1, int g2) {
        return deTransform.apply(rule.apply(transform.apply(g1), transform.apply(g2)));
    }

    protected T inverseOf(T element) {
        return getPower(element, getPowerOfElement(element) - 1);
    }

    public Map<Integer, Set<T>> allPowers() {
        return elements.stream()
                .map(this.transform)
                .collect(Collectors.groupingBy(this::getPowerOfElement, Collectors.toSet()));
    }

    @Override
    public String toString() {
        return elements.stream()
            .map(transform)
            .collect(Collectors.toList()).toString();
    }

    public int getSize() {
        return size;
    }

    protected boolean elementEquals(T g1, T g2) {
        return deTransform.apply(g1).equals(deTransform.apply(g2));
    }
}
