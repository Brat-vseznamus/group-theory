package group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import group.elements.Permutation;
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
        List<List<Integer>> table = cayleyTable();
        
        for (int mask = 1; mask < 1 << getSize(); mask++) {
            if (!allDividors.contains(Integer.bitCount(mask))
                || mask % 2 == 0) {
                continue;
            }

            Set<Integer> group = new HashSet<>();
            List<Integer> intEls = new ArrayList<>();
            for (int el = 0; el < getSize(); el++) {
                if ((1 << el & mask) == 1 << el) {
                    group.add(el);
                    intEls.add(elements.get(el));
                }
            }

            Set<Integer> multiplies = new HashSet<>();

            for (int el1 : intEls) {
                for (int el2 : intEls) {
                    multiplies.add(table.get(el1).get(el2));
                }
            }
            if (group.equals(multiplies)) {
                subGroups.add(intEls);
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
    public <R> Map<Integer, Integer> isomorphism(Group<R> group) {
        if (this.getSize() != group.getSize()) {
            return null;
        }
        int n = this.getSize();
        List<List<Integer>> m1 = cayleyTable();
        List<List<Integer>> m2 = group.cayleyTable();

        var powers1 = allPowers();
        var powers2 = group.allPowers();

        for (Integer pow : powers1.keySet()) {
            if (powers1.get(pow).size() 
            != powers2.getOrDefault(pow, Set.of()).size()) {
                return null;
            }
        }

        return defaultIsomorphism(group, m1, m2);
    }

    private <R> Map<Integer, Integer> defaultIsomorphism(
        Group<R> group, 
        List<List<Integer>> m1, 
        List<List<Integer>> m2) {
        int n = m1.size();
        int nf = IntStream.range(1, n + 1).reduce(1, (c, ac) -> ac * c);

        var sigma1 = indexesReverse();
        var sigma2 = group.indexesReverse();

        for (int permutation = 0; permutation < nf; permutation++) {
            Permutation perm = Permutation.fromInt(n, permutation);
            List<Integer> iso = 
                perm.getSequence().stream()
                .map(i -> i - 1)
                .collect(Collectors.toList());
            
            if (IntStream.range(0, n).allMatch(
                i -> IntStream.range(0, n).allMatch(
                    j -> {
                    var mult1 = m1.get(i).get(j);
                    var multIndex1 = sigma1.get(mult1);

                    var mult2 = m2.get(iso.get(i)).get(iso.get(j));
                    var multIndex2 = sigma2.get(mult2);

                    return multIndex2.equals(iso.get(multIndex1));
                })
            )) {
                HashMap<Integer, Integer> map = new HashMap<>();
                IntStream.range(0, n).forEach(
                    i -> map.put(
                        elements.get(i), 
                        group.elements.get(iso.get(i))));
                return map;
            }
        }
        return null;
    }

    private Map<Integer, Integer> indexesReverse() {
        Map<Integer, Integer> map = new HashMap<>();
        IntStream.range(0, elements.size())
                    .forEach(i -> map.put(elements.get(i), i));
        return map; 
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

    public List<List<Integer>> cayleyTable() {
        List<T> transformedElements = 
            elements.stream()
            .map(this::transform)
            .collect(Collectors.toList());
        return transformedElements.stream()
                .map(fst -> transformedElements.stream()
                            .map(snd -> this.rule(fst, snd))
                            .map(el -> this.deTransform(el))
                            .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }
}
