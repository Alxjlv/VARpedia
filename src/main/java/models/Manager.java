package models;

import javafx.collections.ObservableList;

import java.io.File;

/**
 * A file manager for objects of type {@param <T>}
 * @param <T> The objects to be managed
 */
public abstract class Manager<T> {
    protected ObservableList<T> items;

    public abstract Builder<T> getBuilder();

    /**
     * Returns an observable list of objects
     * @return An observable list of objects
     */
    public ObservableList<T> getItems() {
        return items;
    }

    /**
     * Creates a new object using {@link Builder} and adds this new object to {@code items}
     * @param builder The builder to create a new object from
     */
    public void create(Builder<T> builder) {
        items.add(builder.build());
    }

    /**
     * Deletes a specified object from the filesystem and removes from {@code items}
     * @param item The object to be deleted
     */
    public void delete(T item) {
        // TODO  - Try remove from filesystem


        // If succeeded, remove from List
        items.remove(item);
    }

    protected boolean recursiveDelete(File directory) {
        if (directory.isDirectory()) {
            File[] children = directory.listFiles();
            for (File child: children) {
                boolean status = recursiveDelete(child);
                if (!status) {
                    return false;
                }
            }
        }
        return directory.delete();
    }
}
