package group;

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

}
