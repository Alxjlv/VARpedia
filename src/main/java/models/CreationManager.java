package models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CreationManager extends Manager<Creation> {
    private static CreationManager instance;

    private CreationManager() {
    }

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

    // Create folder
    // Instantiate items
    // Load files -
    @Override
    public void load() {
        File file = new File("Final.mp4");
        items = FXCollections.observableArrayList();
        items.add(new Creation("Test1", file));
        items.add(new Creation("Test2", file));
        items.add(new Creation("Test3", file));
        items.add(new Creation("Test4", file));
        items.add(new Creation("Test5", file));
    }

//    public List<Comparator<Creation>> getComparatorList() {
//        return comparatorList;
//    }

}
