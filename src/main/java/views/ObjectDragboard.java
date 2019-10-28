package views;

/**
 * ObjectDragboard provides a simple interface for {@link DraggableCell}'s to place objects on a Dragboard.
 * Note: Extending classes should be made singleton.
 * @param <T> The type of object contained by the Dragboard
 * @author Tait & Alex
 */
public abstract class ObjectDragboard<T> {
    /**
     * The item contained by the Dragboard
     */
    private T item;

    /**
     * Get the object contained by the Dragboard
     * @return The item contained by the Dragboard
     */
    public T get() {
        return item;
    }

    /**
     * Set the object contained by the Dragboard
     * @param item The item to set the Dragboard to contain
     */
    public void set(T item) {
        this.item = item;
    }
}
