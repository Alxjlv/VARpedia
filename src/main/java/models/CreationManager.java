package models;

import constants.FileExtension;
import constants.FolderPath;
import events.NewCreationEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileFilter;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implements {@link Manager} for {@link Chunk} objects
 */
public class CreationManager extends Manager<Creation> {
    private static CreationManager instance;

    private final File creationsFolder;

    private CreationManager() {
        creationsFolder = FolderPath.CREATIONS_FOLDER.getPath();

        items = FXCollections.observableArrayList();

        if (creationsFolder.exists()) { // TODO Concurrency?
            Pattern videoFilter = Pattern.compile("^"+creationsFolder.getPath()+"/(.+).mp4");
            File[] creationVideos = creationsFolder.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    Matcher match = videoFilter.matcher(pathname.getPath());
                    return match.matches();
                }
            });

            for (File creationVideo: creationVideos) {
                System.out.println(creationVideo.getPath());
                Matcher match = videoFilter.matcher(creationVideo.getPath());
                if (match.matches()) {
                    Creation creation = new Creation(match.group(1), creationVideo);
                    items.add(creation);
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
        if (recursiveDelete(creation.getVideoFile())) {
            super.delete(creation);
        }
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
                        return Collator.getInstance(Locale.ENGLISH).compare(o1.getName(), o2.getName());
                    }

                    @Override
                    public String toString() {
                        return "Name (A-Z)";
                    }
                },
                new Comparator<Creation>() {
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
