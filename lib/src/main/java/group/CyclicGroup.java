package group;

import group.elements.Cycle;

public class CyclicGroup extends Group<Cycle> {

    public CyclicGroup(int size) {
        super(size, (n, m) -> new Cycle((n.getC() + m.getC()) % size), n -> new Cycle(n % size), Cycle::getC);
    }

}
