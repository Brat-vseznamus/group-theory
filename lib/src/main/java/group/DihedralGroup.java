package group;

import group.elements.DihPair;

public class DihedralGroup extends Group<DihPair> {

    public DihedralGroup(int size) {
        super(2 * size,
                (p1, p2) -> new DihPair((p1.getR() + (p1.getS() == 1 ? -1 : 1) * p2.getR() + size) % size,
                        (p1.getS() + p2.getS()) % 2),
                num -> new DihPair(num % size, num / size), dihPair -> dihPair.getS() * size + dihPair.getR());
    }

}
