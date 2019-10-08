package models;

import events.NewCreationEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileFilter;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * Implements {@link Manager} for {@link Chunk} objects
 */
public class CreationManager extends Manager<Creation> {
    private static CreationManager instance;

    private final File creationsFolder;

    private CreationManager() {
        creationsFolder = new File(".creations/");

        items = FXCollections.observableArrayList();

        if (creationsFolder.exists()) { // TODO Concurrency?
            File[] creationFolders = creationsFolder.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });
            for (File creationFolder: creationFolders) {
                File videoFile = new File(creationFolder, "video.mp4");
                if (videoFile.exists()) {
                    items.add(new Creation(creationFolder.getName(), creationFolder));
                }
            }
        } else {
            creationsFolder.mkdir();
        }
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
    public CreationBuilder getBuilder() {
        return new CreationBuilder();
    }

    @Override
    public void delete(Creation creation) {
        if (recursiveDelete(creation.getFolder())) {
            super.delete(creation);
        }
    }

    /**
     * Get a list of Comparators to sort Creations
     * @return A list of Comparators
     */
    public static ObservableList<Comparator<Creation>> getComparators() {
        return FXCollections.observableArrayList(
                new Comparator<>() {
                    @Override
                    public int compare(Creation o1, Creation o2) {
                        return Collator.getInstance(Locale.ENGLISH).compare(o1.getName(), o2.getName());
                    }

                    @Override
                    public String toString() {
                        return "Name (A-Z)";
                    }
                },
                new Comparator<>() {
                    @Override
                    public int compare(Creation o1, Creation o2) {
                        return Collator.getInstance(Locale.ENGLISH).compare(o2.getName(), o1.getName());
                    }

                    @Override
                    public String toString() {
                        return "Name (Z-A)";
                    }
                }
        );
    }

    public void create(CreationBuilder builder) {
        builder.setListener(this).build();
    }

    public void handle(NewCreationEvent event) {
        items.add(event.getCreation());
    }
}
