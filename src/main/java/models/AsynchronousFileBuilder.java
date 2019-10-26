package models;

/**
 * This class implements a variation of the Builder pattern for creating objects related to a particular object
 * @param <T>
 */
public interface AsynchronousFileBuilder<T> {
    /**
     * Build the Object and associated Files
     * @param caller The FileManager of the Object. build() will invoke caller.save(T, File) when complete
     */
    void build(FileManager<T> caller);
}
