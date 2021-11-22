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

        sn = s == 0 ? "" : "s";
        rn = r == 0 ? "" : 
            r == 1 ? "r" : "r^{" + r + "}";
        
        return rn + sn;
    }
}
