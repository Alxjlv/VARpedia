package models;

/**
 * Specifies an interface for {@link Manager} to build objects
 * @param <T> The type of object to be built
 */
public interface Builder<T> {
    T build();
}
