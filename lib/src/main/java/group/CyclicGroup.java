package group;

import group.elements.Cycle;

public class CyclicGroup extends Group<Cycle> {

    public CyclicGroup(int size) {
        super(size, (n, m) -> new Cycle((n.c + m.c) % size), n -> new Cycle(n % size), cycle -> cycle.c);
    }

}
