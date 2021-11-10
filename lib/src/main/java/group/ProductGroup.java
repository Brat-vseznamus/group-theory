package group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import group.elements.Product;

public class ProductGroup<T, R> extends Group<Product<T, R>> {
    private Group<T> left;
    private Group<R> right;

    public ProductGroup(Group<T> left, Group<R> right) {
        super(left.size * right.size,
                (g1, g2) -> new Product<>(left.rule.apply(g1.left, g2.left), right.rule.apply(g1.right, g2.right)),
                n -> new Product<>(left.transform.apply(n / right.size), right.transform.apply(n % right.size)),
                pr -> right.size * left.deTransform.apply(pr.left) + right.deTransform.apply(pr.right));
        this.left = left;
        this.right = right;
    }

    @Override
    public Map<Integer, Set<Group<Product<T, R>>>> allSubGroups() {
        var leftSubGroups = left.allSubGroups();
        var rightSubGroups = right.allSubGroups();

        Map<Integer, Set<Group<Product<T, R>>>> subgroups = new HashMap<>();

        System.out.println(leftSubGroups);
        System.out.println(rightSubGroups);

        for (int s1 : leftSubGroups.keySet()) {
            for (int s2 : rightSubGroups.keySet()) {
                System.out.println(s1 + " , " + s2);
                if (s1 * s2 == 1) {
                    subgroups.putIfAbsent(1, new HashSet<>());
                    subgroups.get(1).add(new Group<>(this, List.of(0)));
                } else {
                    for (var g1 : leftSubGroups.get(s1)) {
                        for (var g2 : rightSubGroups.get(s2)) {
                            // TODO: fix isomorphism
                            if (s1 == s2) {
                                Map<Integer, Integer> isomorphism = g1.isomorphic(g2);
                                if (isomorphism != null) {
                                    List<Integer> subGroupElements = new ArrayList<>();
                                    for (int g : g1.elements) {
                                        subGroupElements.add(deTransform.apply(new Product<>(
                                                left.transform.apply(left.elements.get(g)),
                                                right.transform.apply(right.elements.get(isomorphism.get(g))))));
                                    }
                                    subgroups.putIfAbsent(s1, new HashSet<>());
                                    subgroups.get(s1).add(new Group<>(this, subGroupElements));
                                }
                            }

                            List<Integer> subGroupElements = new ArrayList<>();
                            for (int i : g1.elements) {
                                for (int j : g2.elements) {
                                    subGroupElements.add(deTransform
                                            .apply(new Product<>(left.transform.apply(i), right.transform.apply(j))));
                                }
                            }
                            subgroups.putIfAbsent(s1 * s2, new HashSet<>());
                            subgroups.get(s1 * s2).add(new Group<>(this, subGroupElements));
                        }
                    }
                }
            }
        }

        return subgroups;
    }

}
