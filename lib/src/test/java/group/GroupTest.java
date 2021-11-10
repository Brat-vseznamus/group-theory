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

public class GroupTest {
    public static class CyclicGroupOfPrimeOrderProvider implements ArgumentsProvider {
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

    public static class GroupProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            List<Group<?>> result = new ArrayList<>();
            for (int i = 1; i < 10; i++) {
                result.add(new CyclicGroup(i));
                result.add(new DihedralGroup(i));
            }
            return result.stream().map(Arguments::of);
        }
    }
    
    @ParameterizedTest
    @ArgumentsSource(GroupProvider.class)
    public <T> void selfSubgroup(Group<T> G) {
        assertEquals(G, G.subgroup(G.elements));
    }
    
    @ParameterizedTest
    @ArgumentsSource(GroupProvider.class)
    public <T> void neutralElement(Group<T> G) {
        var neutralElements = G.elements.stream()
            .map(G::transform)
            .map(G::getPowerOfElement)
            .filter(p -> p == 1)
            .collect(Collectors.toList());
        assertEquals(1, neutralElements.size());
        assertEquals("1", G.neutralElement().toString());
    }
    
    @ParameterizedTest
    @ArgumentsSource(GroupProvider.class)
    public <T> void trivialSubgroups(Group<T> G) {
        var subgroups = G.allSubGroups();
        assertEquals(subgroups.get(1), Set.of(G.subgroup(List.of(G.deTransform(G.neutralElement())))));
        assertEquals(subgroups.get(G.getSize()), Set.of(G));
    }

    @ParameterizedTest
    @ArgumentsSource(CyclicGroupOfPrimeOrderProvider.class)
    public <T> void cyclicGroupOfPrimeOrderHasNoSubgroups(Group<T> G) {
        assertEquals(2, G.allSubGroups().size());
    }
}
