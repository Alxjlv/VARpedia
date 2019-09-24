package models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.Comparator;

/**
 * Implements {@link Manager} for {@link Chunk} objects
 */
public class CreationManager extends Manager<Creation> {
    private static CreationManager instance;

    private CreationManager() {
    }

    /**
     * Get the singleton instance
     * @return The singleton instance
     */
    public static CreationManager getInstance() {
        if (instance == null) {
            synchronized (CreationManager.class) {
                if (instance == null) {
                    instance = new CreationManager();
                }
            }
        }
        return instance;
    }

    @Override
    public void load() {
        // TODO - Ensure ".creations/" folder exists

        items = FXCollections.observableArrayList();

        // TODO - Load creations from ".creations/" folder
        File file = new File("Final.mp4");
        items.add(new Creation("Test1", file));
        items.add(new Creation("Test2", file));
        items.add(new Creation("Test3", file));
        items.add(new Creation("Test4", file));
        items.add(new Creation("Test5", file));
    }

    @Override
    public CreationBuilder getBuilder() {
        return new CreationBuilder();
    }

    /**
     * Get a list of Comparators to sort Creations
     * @return A list of Comparators
     */
    public static ObservableList<Comparator<Creation>> getComparators() {
        return FXCollections.observableArrayList(
                new Comparator<Creation>() {
                    @Override
                    public int compare(Creation o1, Creation o2) {
                        return o1.getName().compareTo(o2.getName());
                    }

                    @Override
                    public String toString() {
                        return "Name (A-Z)";
                    }
                },
                new Comparator<Creation>() {
                    @Override
                    public int compare(Creation o1, Creation o2) {
                        return o2.getName().compareTo(o1.getName());
                    }

                    @Override
                    public String toString() {
                        return "Name (Z-A)";
                    }
                }
        );
    }

}
