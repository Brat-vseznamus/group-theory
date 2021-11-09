package groups;

import java.util.stream.IntStream;

import groups_utils.Permutation;

public class SymmetricGroup extends Group<Permutation> {
    public SymmetricGroup(int size) {
        super(IntStream.range(1, size + 1).reduce(1, (e, ac) -> e * ac), 
            (s1, s2) -> s1.apply(s2),
            n -> Permutation.fromInt(size, n),
            Permutation::toInt);
    }
}
