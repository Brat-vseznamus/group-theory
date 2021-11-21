package group;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;

public class GroupTest {
    static class CyclicGroupOfPrimeOrderProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            // what's efficiency anyway?
            return IntStream.rangeClosed(2, 20)
                    .filter(i -> IntStream.rangeClosed(2, (int) Math.sqrt(i))
                            .allMatch(j -> i % j != 0))
                    .boxed()
                    .map(CyclicGroup::new)
                    .map(Arguments::of);
        }
    }

    static class GroupProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            List<Group<?>> result = new ArrayList<>();
            for (int i = 1; i < 10; i++) {
                result.add(new CyclicGroup(i));
                result.add(new DihedralGroup(i));
            }
            // return result.stream().map(Arguments::of);
            return IntStream.rangeClosed(1, 10)
                    .boxed()
                    .flatMap(i -> List.of(new CyclicGroup(i), new DihedralGroup(i), new SymmetricGroup(i)).stream())
                    .map(Arguments::of);
        }
    }

    private static Stream<? extends Arguments> smallGroupArguments() {
        return IntStream.rangeClosed(1, 10)
                .boxed()
                .flatMap(i -> List.of(
                    new CyclicGroup(i), 
                    new DihedralGroup(i), 
                    new SymmetricGroup(i)).stream()
                )
                .filter(g -> g.getSize() <= 31)
                .map(Arguments::of);
    }
    
    @ParameterizedTest(name="{argumentsWithNames}")
    @ArgumentsSource(GroupProvider.class)
    <T> void selfSubgroup(Group<T> G) {
        assertEquals(G, G.subgroup(G.elements));
    }
    
    @ParameterizedTest(name="{argumentsWithNames}")
    @ArgumentsSource(GroupProvider.class)
    <T> void neutralElement(Group<T> G) {
        var neutralElements = G.elements.stream()
            .map(G::transform)
            .map(G::getPowerOfElement)
            .filter(p -> p == 1)
            .collect(Collectors.toList());
        assertEquals(1, neutralElements.size());
        assertEquals("1", G.neutralElement().toString());
    }
    
    @ParameterizedTest(name="{argumentsWithNames}")
    @MethodSource("smallGroupArguments")
    <T> void trivialSubgroupsForSmallGroups(Group<T> G) {
        var subgroups = G.allSubGroups();
        assertEquals(Set.of(G.subgroup(List.of(G.deTransform(G.neutralElement())))), subgroups.get(1));
        assertEquals(Set.of(G), subgroups.get(G.getSize()));
    }

    @ParameterizedTest(name="{argumentsWithNames}")
    @ArgumentsSource(CyclicGroupOfPrimeOrderProvider.class)
    <T> void cyclicGroupOfPrimeOrderHasNoSubgroups(Group<T> G) {
        assertEquals(2, G.allSubGroups().size());
    }
}
