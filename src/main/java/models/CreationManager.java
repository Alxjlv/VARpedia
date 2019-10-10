package models;

import constants.Folder;
import events.NewCreationEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implements {@link Manager} for {@link Chunk} objects
 */
public class CreationManager extends Manager<Creation> {
    private static CreationManager instance;

    private int id;

    private Map<Creation, File> serializedFiles;

    private CreationManager() {
        File creationsFolder = Folder.CREATIONS.get();
        if (!creationsFolder.exists()) {
            creationsFolder.mkdir();
        }
        File serializedFolder = Folder.CREATIONS_SERIALIZED.get();
        if (!serializedFolder.exists()) {
            serializedFolder.mkdir();
        }
        File videosFolder = Folder.CREATIONS_VIDEO.get();
        if (!videosFolder.exists()) {
            videosFolder.mkdir();
        }

        items = FXCollections.observableArrayList();
        serializedFiles = new HashMap<>();

        Pattern serializedFilter = Pattern.compile("^"+serializedFolder.getPath()+"/.+.ser");
        File[] serializedCreations = serializedFolder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                Matcher match = serializedFilter.matcher(pathname.getPath());
                return match.matches();
            }
        });
        if (serializedCreations != null) {
            for (File serializedCreation : serializedCreations) {
                try {
                    FileInputStream fileInputStream = new FileInputStream(serializedCreation);
                    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                    Creation creation = (Creation) objectInputStream.readObject();
                    if (creation.getVideoFile().exists()) {
                        serializedFiles.put(creation, serializedCreation);
                        items.add(creation);
                    } else {
                        // TODO - Regenerate?
                    }
                    objectInputStream.close();
                    fileInputStream.close();
                } catch (IOException | ClassNotFoundException e) {
                    // TODO - Handle exception
                    e.printStackTrace();
                }
            }
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
        if (!recursiveDelete(creation.getVideoFile())) {
            // TODO - Handle failed deletion
        }
        if (!recursiveDelete(serializedFiles.get(creation))) {
            // TODO - Handle failed deletion
        }
        super.delete(creation);
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

    public void create(CreationBuilder builder) { // TODO - Change signature to Builder<Creation> to override Manager
        builder.setListener(this).build();
    }

    public void handle(NewCreationEvent event) {
        Creation creation = event.getCreation();

        File serializedFolder = Folder.CREATIONS_SERIALIZED.get();
        serializedFolder.mkdir();
        File serializedCreation = new File(serializedFolder, String.format("%s.ser", creation.getName()));

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(serializedCreation);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(creation);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            // TODO - Handle exception
        } catch (IOException e) {
            // TODO - Handle exception
        }
        serializedFiles.put(creation, serializedCreation);
        items.add(creation);
    }
}
