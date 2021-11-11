package group;

import java.util.stream.IntStream;

import group.elements.Permutation;

public class SymmetricGroup extends Group<Permutation> {
    private int order;

    public SymmetricGroup(int size) {
        super(IntStream.range(1, size + 1).reduce(1, (e, ac) -> e * ac));
        order = size;
    }

    @Override
    Permutation transform(int x) {
        return Permutation.fromInt(order, x);
    }

    @Override
    int deTransform(Permutation element) {
        return Permutation.toInt(element);
    }

    @Override
    Permutation rule(Permutation e1, Permutation e2) {
        return e1.apply(e2);
    }
    
    public static void main(String[] args) {
        System.out.println(new SymmetricGroup(4));
    }

    @Override
    public String toString() {
        return "S[" + order + "]";
    }
}
