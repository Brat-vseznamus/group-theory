import java.util.ArrayList;
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

    private Group(int size, BinaryOperator<T> rule) {
        this.size = size;
        this.rule = rule;
        this.elements = IntStream
                    .range(0, size)
                    .boxed()
                    .collect(Collectors.toList());
    }

    public <S extends Group<T>> Group(S originalGroup, List<Integer> subgroup) {
        this.size = subgroup.size();
        this.rule = originalGroup.rule;
        this.elements = subgroup;
        this.transform = originalGroup.transform;
        this.deTransform = originalGroup.deTransform;
    }

    public Group(int size, 
        BinaryOperator<T> rule, 
        Function<Integer, T> transform, 
        Function<T, Integer> deTransform) {
        
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
            if (group.containsAll(multiplies) 
            && multiplies.containsAll(group)) {
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
        return elements.stream().allMatch(
            index -> {
                T g = transform.apply(index);
                Set<T> cosetLeft = groupElements.stream()
                                .map(s -> rule.apply(s, g))
                                .collect(Collectors.toSet());
                Set<T> cosetRight = groupElements.stream()
                                .map(s -> rule.apply(g, s))
                                .collect(Collectors.toSet());
                return cosetLeft.equals(cosetRight);
            }
        );
    }

    // return map if isomorphic and null otherwise
    // TODO
    public <R> Map<Integer, Integer> isomorphic(Group<R> group) {
        if (this.size != group.size) {
            throw new IllegalArgumentException("comparing groups must have same sizes");
        }
        int n = this.size;
        int[][] m1 = new int[n][n];
        int[][] m2 = new int[n][n];
        int i = 0, j = 0;
        for (int e1 : this.elements) {
            for (int e2 : this.elements) {
                m1[i][j] = this.deTransform.apply(
                        this.rule.apply(
                            this.transform.apply(e1), 
                            this.transform.apply(e2)));
                j++;
            }
            i++;
        }
        i = j = 0;
        for (int e1 : group.elements) {
            for (int e2 : group.elements) {
                m1[i][j] = group.deTransform.apply(
                        group.rule.apply(
                            group.transform.apply(e1), 
                            group.transform.apply(e2)));
                j++;
            }
            i++;
        }
        return null;
    }

    @Override
    public String toString() {
        return elements.stream()
                .map(transform)
                .collect(Collectors.toList())
                .toString();
    }

    public int getSize() {
        return size;
    }
}