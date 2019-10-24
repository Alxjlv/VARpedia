package models.creation;

import constants.Filename;
import constants.Folder;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import models.Builder;
import models.Manager;
import models.chunk.Chunk;

import java.io.*;
import java.net.URL;
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

        items = FXCollections.observableArrayList(new Callback<Creation, Observable[]>() {
            @Override
            public Observable[] call(Creation param) {
                return new Observable[]{
                        param.nameProperty(),
                        param.viewCountProperty(),
                        param.confidenceRatingProperty(),
                        param.durationProperty()
                };
            }
        });
        items.addListener(new ListChangeListener<Creation>() {
            @Override
            public void onChanged(Change<? extends Creation> c) {
                while (c.next()) {
                    if (c.wasUpdated()) {
                        update(items.get(c.getFrom()));
                    }
                }
            }
        });

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
        return new CreationBuilder().setCreationFolder(folder);
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
                if (o1.getViewCount() == 0 && o2.getViewCount() == 0) {
                    if (o1.getConfidenceRating() == o2.getConfidenceRating()) {
                        return Collator.getInstance(Locale.ENGLISH).compare(o1.getName(), o2.getName());
                    } else {
                        return Integer.compare(o1.getConfidenceRating(), o2.getConfidenceRating());
                    }
                } else if (o1.getViewCount() == 0) {
                    return -1;
                } else if (o2.getViewCount() == 0) {
                    return 1;
                }
                return Integer.compare(o1.getConfidenceRating(), o2.getConfidenceRating());
            }

            @Override
            public String toString() {
                return "To Review";
            }
        },
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
                },
                new Comparator<Creation>() {
                    @Override
                    public int compare(Creation o1, Creation o2) {
                        return Integer.compare(o1.getViewCount(), o2.getViewCount());
                    }

                    @Override
                    public String toString() {
                        return "Least Viewed";
                    }
                },
                new Comparator<Creation>() {
                    @Override
                    public int compare(Creation o1, Creation o2) {
                        return Integer.compare(o2.getViewCount(), o1.getViewCount());
                    }

                    @Override
                    public String toString() {
                        return "Most Viewed";
                    }
                },
                new Comparator<Creation>() {
                    @Override
                    public int compare(Creation o1, Creation o2) {
                        return Integer.compare(o1.getConfidenceRating(), o2.getConfidenceRating());
                    }

                    @Override
                    public String toString() {
                        return "Least confident";
                    }
                },
                new Comparator<Creation>() {
                    @Override
                    public int compare(Creation o1, Creation o2) {
                        return Integer.compare(o2.getConfidenceRating(), o1.getConfidenceRating());
                    }

                    @Override
                    public String toString() {
                        return "Most confident";
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
        serialize(creation, serializedCreation);

        serializedFiles.put(creation, serializedCreation);
        items.add(creation);
    }

    void edit(Creation creation, File folder, Creation old) {
        delete(old);
        save(creation, folder);
    }

    private void update(Creation creation) {
        serialize(creation, serializedFiles.get(creation));
    }

    private void serialize(Creation creation, File serializedCreation) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(serializedCreation);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(creation);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            // TODO - Handle exception
        }
    }
}
