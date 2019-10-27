package models.creation;

import constants.Filename;
import constants.Folder;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import models.FileManager;
import models.chunk.Chunk;

import java.io.*;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * Implements {@link FileManager} for {@link Chunk} objects
 */
public class CreationFileManager extends FileManager<Creation> {
    private static CreationFileManager instance;

    private int id;

    private CreationFileManager() {
        super();

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
                        param.dateLastViewedProperty()
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
                    if (new File(creationFolder, Filename.VIDEO.get()).exists() && new File(creationFolder, Filename.THUMBNAIL.get()).exists()) { // TODO - Check on File not Creation?
                        files.put(creation, creationFolder);
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
    public static CreationFileManager getInstance() {
        if (instance == null) {
            synchronized (CreationFileManager.class) {
                if (instance == null) {
                    instance = new CreationFileManager();
                }
            }
        }
        return instance;
    }

    @Override
    public CreationFileBuilder getBuilder() {
        File folder = new File(Folder.CREATIONS.get(), Integer.toString(id++));
        folder.mkdirs();
        return new CreationFileBuilder().setCreationFolder(folder);
    }

    /**
     * Package-private method. Saves a creation to filesystem
     * @param creation Creation to save
     * @param folder Where to save
     */
    @Override
    public void save(Creation creation, File folder) {
        File serializedCreation = new File(folder, Filename.CREATION.get());
        serialize(creation, serializedCreation);

        super.save(creation, folder);
    }

    void edit(Creation updated, File folder, Creation old) {
        delete(old);
        save(updated, folder);
    }

    private void update(Creation creation) {
        serialize(creation, getSerializedFile(creation));
    }

    public File getVideoFile(Creation creation) {
        return new File(getFile(creation), Filename.VIDEO.get());
    }

    public File getThumbnailFile(Creation creation) {
        return new File(getFile(creation), Filename.THUMBNAIL.get());
    }

    private File getSerializedFile(Creation creation) {
        return new File(getFile(creation), Filename.CREATION.get());
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
