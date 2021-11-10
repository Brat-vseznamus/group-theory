package group;

import group.elements.DihPair;

public class DihedralGroup extends Group<DihPair> {

    public DihedralGroup(int size) {
        super(2 * size);
    }

    @Override
    DihPair transform(int n) {
        return new DihPair(n % getSize(), n / getSize());
    }

    @Override
    int deTransform(DihPair element) {
        return element.getS() * getSize() + element.getR();
    }

    @Override
    DihPair rule(DihPair e1, DihPair e2) {
        return new DihPair((e1.getR() + (e1.getS() == 1 ? -1 : 1) * e2.getR() + getSize()) % getSize(),
                (e1.getS() + e2.getS()) % 2);
    }

}
