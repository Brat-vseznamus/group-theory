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
        super(left.getSize() * right.getSize());
        this.left = left;
        this.right = right;
    }

    // TODO: simplify
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
                    subgroups.get(1).add(subgroup(List.of(0)));
                } else {
                    for (var g1 : leftSubGroups.get(s1)) {
                        for (var g2 : rightSubGroups.get(s2)) {
                            // TODO: fix isomorphism
                            if (s1 == s2) {
                                Map<Integer, Integer> isomorphism = g1.isomorphism(g2);
                                if (isomorphism != null) {
                                    List<Integer> subGroupElements = new ArrayList<>();
                                    for (int g : g1.elements) {
                                        subGroupElements
                                                .add(deTransform(Product.of(left.transform(left.elements.get(g)),
                                                        right.transform(right.elements.get(isomorphism.get(g))))));
                                    }
                                    subgroups.putIfAbsent(s1, new HashSet<>());
                                    subgroups.get(s1).add(subgroup(subGroupElements));
                                }
                            }

                            List<Integer> subGroupElements = new ArrayList<>();
                            for (int i : g1.elements) {
                                for (int j : g2.elements) {
                                    subGroupElements
                                            .add(deTransform(Product.of(left.transform(i), right.transform(j))));
                                }
                            }
                            subgroups.putIfAbsent(s1 * s2, new HashSet<>());
                            subgroups.get(s1 * s2).add(subgroup(subGroupElements));
                        }
                    }
                }
            }
        }

        return subgroups;
    }

    @Override
    Product<T, R> transform(int n) {
        return Product.of(left.transform(n / right.getSize()), right.transform(n % right.getSize()));
    }

    @Override
    int deTransform(Product<T, R> element) {
        return right.getSize() * left.deTransform(element.getLeft()) + right.deTransform(element.getRight());
    }

    @Override
    Product<T, R> rule(Product<T, R> e1, Product<T, R> e2) {
        return Product.of(left.rule(e1.getLeft(), e2.getLeft()), right.rule(e1.getRight(), e2.getRight()));
    }

    @Override
    public String toString() {
        return left.toString() + " x " + right.toString();
    }

}
