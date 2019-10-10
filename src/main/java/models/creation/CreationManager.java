package models.creation;

import constants.Filename;
import constants.Folder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Builder;
import models.Manager;
import models.chunk.Chunk;

import java.io.*;
import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

        items = FXCollections.observableArrayList();
        serializedFiles = new HashMap<>();

        File[] creationFolders = creationsFolder.listFiles(pathname -> {
            if (!pathname.isDirectory()) {
                return false;
            }
            try {
                int creationId = Integer.parseInt(pathname.getName());
                if (creationId >= id) {
                    id = creationId;
                    id++;
                }
            } catch (NumberFormatException e) {
                return false;
            }
            File serializedCreation = new File(pathname, Filename.CREATION.get());
            if (!serializedCreation.exists()) {
                recursiveDelete(pathname);
                return false;
            }

            return true;
        });

        if (creationFolders != null) {
            for (File creationFolder: creationFolders) {
                try {
                    File serializedCreation = new File(creationFolder, Filename.CREATION.get());

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
        File folder = new File(Folder.CREATIONS.get(), Integer.toString(id++));
        folder.mkdirs();
        return new CreationBuilder().setFolder(folder);
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

    @Override
    public void create(Builder<Creation> builder) {
        builder.build();
    }

    /**
     * Package-private method. Saves a creation to filesystem
     * @param creation Creation to save
     * @param folder Where to save
     */
    void save(Creation creation, File folder) {
        File serializedCreation = new File(folder, Filename.CREATION.get());

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(serializedCreation);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(creation);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            // TODO - Handle exception
        }
        serializedFiles.put(creation, serializedCreation);

        items.add(creation);
    }
}
