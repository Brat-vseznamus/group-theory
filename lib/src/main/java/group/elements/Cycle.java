package group.elements;

public class Cycle {
    public int c;

    public Cycle(int c) {
        this.c = c;
    }

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + c;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Cycle other = (Cycle) obj;
        if (c != other.c)
            return false;
        return true;
    }

}
