package group.elements;

import lombok.Data;

public @Data(staticConstructor = "of") class Product<T, R> {
    private final T left;
    private final R right;

    @Override
    public String toString() {
        return "(" + left + ", " + right + ")";
    }
}
