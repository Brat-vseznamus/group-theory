package groups;
import groups_utils.DihPair;

public class DihedralGroup extends Group<DihPair> {

    public DihedralGroup(int size) {
        super(2 * size, 
            (p1, p2) -> 
                new DihPair(
                    (p1.r + (p1.s == 1 ? -1 : 1) * p2.r + size) % size, 
                    (p1.s + p2.s) % 2),
            num -> new DihPair(num % size, num / size),
            dihPair -> dihPair.s * size + dihPair.r);
    }
    
}
