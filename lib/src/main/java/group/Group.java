package group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.EqualsAndHashCode;
import utils.NumericalUtils;

@EqualsAndHashCode
public abstract class Group<T> {

    abstract T transform(int x);

    abstract int deTransform(T element);

    abstract T rule(T e1, T e2);

    protected Group(int size) {
        this(IntStream.range(0, size).boxed().collect(Collectors.toList()));
    }

    protected Group(List<Integer> elements) {
        this.elements = elements;
    }

    protected final List<Integer> elements;

    public Map<Integer, Set<Group<T>>> allSubGroups() {
        Set<List<Integer>> subGroups = new HashSet<>();
        Set<Integer> allDividors = NumericalUtils.allDivisors(getSize());
        for (int mask = 1; mask < 1 << getSize(); mask++) {
            if (!allDividors.contains(Integer.bitCount(mask))) {
                continue;
            }
            Set<T> group = new HashSet<>();
            List<Integer> intels = new ArrayList<>();
            for (int el = 0; el < getSize(); el++) {
                if ((1 << el & mask) == 1 << el) {
                    group.add(transform(elements.get(el)));
                    intels.add(elements.get(el));
                }
            }
            Set<T> multiplies = new HashSet<>();
            multiplies.add(neutralElement());
            for (T el1 : group) {
                for (T el2 : group) {
                    multiplies.add(rule(el1, el2));
                }
            }
            if (group.containsAll(multiplies) && multiplies.containsAll(group)) {
                subGroups.add(intels);
            }
        }
        return subGroups.stream()
                .map(this::subgroup)
                .collect(Collectors.groupingBy(Group<T>::getSize, Collectors.toSet()));
    }

    public boolean isNormal(Group<T> originalGroup) {
        if (!originalGroup.elements.stream()
            .collect(Collectors.toSet())
            .contains(elements)) {
            return false;
        }
        Set<T> groupElements = elements.stream().map(this::transform).collect(Collectors.toSet());
        return originalGroup.elements.stream().allMatch(index -> {
            T g = transform(index);
            Set<T> cosetLeft = leftCoset(groupElements, g);
            Set<T> cosetRight = rightCoset(groupElements, g);
            return cosetLeft.equals(cosetRight);
        });
    }

    protected Set<T> leftCoset(Collection<T> groupElements, T g) {
        return groupElements.stream().map(s -> rule(s, g)).collect(Collectors.toSet());
    }

    protected Set<T> rightCoset(Collection<T> groupElements, T g) {
        return groupElements.stream().map(s -> rule(g, s)).collect(Collectors.toSet());
    }

    public Group<T> subgroup(List<Integer> subelements) {
        return new CustomGroup<>(this, subelements);
    }

    // return map if isomorphic and null otherwise
    // TODO
    public <R> Map<Integer, Integer> isomorphism(Group<R> group) {
        if (this.getSize() != group.getSize()) {
            return null;
        }
        int n = this.getSize();
        int[][] m1 = new int[n][n];
        int[][] m2 = new int[n][n];
        int i = 0, j = 0;
        // TODO: extract this to `cayleyTable` method
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
        T e = transform(0);
        int pw = 1;
        T ac = element;
        while (!elementEquals(e, ac)) {
            ac = rule(ac, element);
            pw++;
        }
        return pw;
    }

    protected T getPower(T element, int pow) {
        assert pow >= 0;
        T ac = transform(0);
        while (pow-- > 0) {
            ac = rule(ac, element);
        }
        return ac;
    }

    protected int integerRule(int g1, int g2) {
        return deTransform(rule(transform(g1), transform(g2)));
    }

    protected T inverseOf(T element) {
        return getPower(element, getPowerOfElement(element) - 1);
    }

    public Map<Integer, Set<T>> allPowers() {
        return elements.stream().map(this::transform)
                .collect(Collectors.groupingBy(this::getPowerOfElement, Collectors.toSet()));
    }

    public T neutralElement() {
        return transform(0);
    }

    @Override
    public String toString() {
        return "G[" + getSize() + "] = " + toElements();
    }

    public String toElements() {
        return elements.stream().map(this::transform).collect(Collectors.toList()).toString();
    }

    public int getSize() {
        return elements.size();
    }

    protected boolean elementEquals(T g1, T g2) {
        return deTransform(g1) == deTransform(g2);
    }
}
