package groups_utils;

public class Product<T, R> {
    public T left;
    public R right;

    public Product(T left, R right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "(" + left + ", " + right + ")";
    }
}
