package models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * A file manager for objects of type {@param <T>}
 * @param <T> The objects to be managed
 */
public abstract class FileManager<T> {
    protected ObservableList<T> items; // TODO - Make synchronised?
    protected Map<T, File> files; // TODO - Make Synchronised?

    public FileManager() {
        items = FXCollections.observableArrayList();
        files = new HashMap<>();
    }

    /**
     * Returns an unmodifiable observable list of items
     * @return An unmodifiable observable list of items
     */
    public ObservableList<T> getItems() {
        return FXCollections.unmodifiableObservableList(items);
    }

    /**
     * Return the file for the given item
     * @param item
     * @return
     */
    public File getFile(T item) {
        return files.get(item);
    }

    /**
     * Return an async builder for type T
     * @return
     */
    public abstract AsynchronousFileBuilder<T> getBuilder();

    /**
     * Creates a new object using {@link AsynchronousFileBuilder} and adds this new object to {@code items}
     * @param builder The builder to create a new object from
     */
    public void create(AsynchronousFileBuilder<T> builder) {
        builder.build(this);
    }

    /**
     * Save the item generated asynchronously by builder
     * @param item
     * @param file
     */
    public void save(T item, File file) {
        files.put(item, file);
        System.out.println(item);
        items.add(item);
    }

    /**
     * Removes a specified object from {@code items} and its files
     * @param item The object to be deleted
     */
    public void delete(T item) {
        recursiveDelete(getFile(item));
        items.remove(item);
        files.remove(item);
    }

    /**
     * Helper method to recursively delete a File or Folder
     * @param directory The location to be deleted
     * @return
     */
    protected boolean recursiveDelete(File directory) {
        if (directory.isDirectory()) {
            File[] children = directory.listFiles();
            if (children != null) {
                for (File child : children) {
                    boolean status = recursiveDelete(child);
                    if (!status) {
                        return false;
                    }
                }
            }
        }
        return directory.delete();
    }
}
