package models;

import javafx.collections.ObservableList;

public abstract class Manager<T> {
//    File itemsFolder; TODO
    protected ObservableList<T> items;

    public abstract void load();

    public abstract Builder<T> getBuilder();

    public ObservableList<T> getItems() {
        return items;
    }

    public void create(Builder<T> builder) {
        items.add(builder.build());
    }

    public void delete(T item) {
        // TODO  - Try remove from filesystem

        // If succeeded, remove from List
        items.remove(item);
    }
}
