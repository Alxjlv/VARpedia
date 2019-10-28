package models;

/**
 * This class implements a variation of the Builder pattern for placing a callback to a {@link FileManager}
 * @param <T> The type of object that will be built
 * @auhtor Tait & Alex
 */
public interface CallbackFileBuilder<T> {
    /**
     * Build the Object and associated Files
     * @param caller The FileManager of the Object. build() will invoke caller.save(T, File) when complete
     */
    void build(FileManager<T> caller);
}
