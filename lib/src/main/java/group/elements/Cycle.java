package group.elements;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@EqualsAndHashCode
public class Cycle {
    @Getter
    private final int c;

    @Override
    public String toString() {
        if (c == 0) {
            return "1";
        } else if (c == 1) {
            return "c";
        } else {
            return "c^{" + c + "}";
        }
    }
}
