package group;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class CustomGroup<T> extends Group<T> {
    protected BinaryOperator<T> rule;
    protected Function<Integer, T> transform;
    protected Function<T, Integer> deTransform;

    protected CustomGroup(int size, BinaryOperator<T> rule) {
        super(size);
        this.rule = rule;
    }

    public <S extends Group<T>> CustomGroup(S originalGroup, List<Integer> subgroup) {
        super(subgroup);
        this.rule = originalGroup::rule;
        this.transform = originalGroup::transform;
        this.deTransform = originalGroup::deTransform;
    }

    public CustomGroup(int size, BinaryOperator<T> rule, Function<Integer, T> transform,
            Function<T, Integer> deTransform) {
        this(size, rule);
        this.transform = transform;
        this.deTransform = deTransform;
    }

    @Override
    T transform(int x) {
        return transform.apply(x);
    }

    @Override
    int deTransform(T element) {
        return deTransform.apply(element);
    }

    @Override
    T rule(T e1, T e2) {
        return rule.apply(e1, e2);
    }
}
