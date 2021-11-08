package groups_utils;

public class DihPair {
    public DihPair(int r, int s) {
        this.r = r;
        this.s = s;
    }

    public int r;
    public int s;

    @Override
    public String toString() {
        if (s == 0 && r == 0) {
            return "1";
        }
        String sn = "", rn = "";
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + r;
        result = prime * result + s;
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
        DihPair other = (DihPair) obj;
        if (r != other.r)
            return false;
        if (s != other.s)
            return false;
        return true;
    }
        
}

