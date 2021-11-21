package group;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import group.elements.Permutation;

public class GroupIsomorphismTest {

    static final long seed = 239 * 38 * 284589;

    private static Stream<? extends Arguments> cyclicGroups() {
        return groupArgumentsGenerator(10, CyclicGroup::new);
    }

    @ParameterizedTest(name="{argumentsWithNames}")
    @MethodSource("cyclicGroups")
    void isomorphismCyclic(CyclicGroup g) {
        int n = g.getSize();
        baseGroupTest(g, (e1, e2) -> (e1 + e2) % n);
    }

    private static Stream<? extends Arguments> dihedralGroups() {
        return groupArgumentsGenerator(5, DihedralGroup::new);
    }

    @ParameterizedTest(name="{argumentsWithNames}")
    @MethodSource("dihedralGroups")
    void isomorphismDihedral(DihedralGroup g) {
        int n = g.getSize();
        baseGroupTest(g, (e1, e2) -> computeDih(e1, e2, n/2));
    }

    private static int computeDih(int e1, int e2, int dihN) {
        int s1 = e1 / dihN;
        int s2 = e2 / dihN;
        int r1 = e1 % dihN;
        int r2 = e2 % dihN;
        return dihN * ((s1 + s2) % 2) + ((r1 + (s1 == 0? r2:-r2) + dihN) % dihN);
    }

    private static Stream<? extends Arguments> groupArgumentsGenerator(int endSize, 
                                                        Function<Integer, Group<?>> groupGenerator) {
        return IntStream.rangeClosed(2, endSize)
                    .boxed()
                    .map(groupGenerator)
                    .map(Arguments::of);
    }

    private void baseGroupTest(Group<?> g, BinaryOperator<Integer> f) {
        int n = g.getSize();
        int nf = IntStream.range(1, n + 1).reduce(1, (c, ac) -> ac * c);
        
        Random r = new Random(seed);

        int numberOfTests = (int)Math.floor(Math.sqrt(nf));

        for (int permutation : IntStream.range(0, numberOfTests)
                    .map(i -> r.nextInt(nf))
                    .boxed()
                    .collect(Collectors.toList())) {
            Permutation perm = Permutation.fromInt(n, permutation);
            List<Integer> iso = 
                perm.getSequence().stream()
                .map(i -> i - 1)
                .collect(Collectors.toList());
            
            if (iso.get(0) != 0) {
                continue;
            }
            
            List<Integer> isoRev = new ArrayList<>(Collections.nCopies(n, 0));
            for (int i = 0; i < n; i++) {
                isoRev.set(iso.get(i), i);
            }
            CustomGroup<Integer> gPerm = new CustomGroup<>(
                n,
                (e1, e2) -> iso.get(f.apply(isoRev.get(e1), isoRev.get(e2))),
                Function.identity(),
                Function.identity()
            );
            assertNotNull(g.isomorphism(gPerm));
        }
    }
}
