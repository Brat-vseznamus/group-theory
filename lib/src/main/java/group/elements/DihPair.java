package group.elements;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@EqualsAndHashCode
public class DihPair {
    @Getter
    private int r;
    @Getter
    private int s;

    @Override
    public String toString() {
        if (s == 0 && r == 0) {
            return "1";
        }
        String sn = "";
        String rn = "";
        if (s == 0) {
            sn = "";
        } else if (s == 1) {
            sn = "s";
        }

        if (r == 0) {
            rn = "";
        } else if (r == 1) {
            rn = "r";
        } else {
            rn = "r^{" + r + "}";
        }
        return rn + sn;
    }
}
