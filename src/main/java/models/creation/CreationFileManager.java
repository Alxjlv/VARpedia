package models.creation;

import constants.Filename;
import constants.Folder;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.util.Callback;
import models.FileManager;

import java.io.*;

/**
 * CreationFileManager is a singleton {@link FileManager} for {@link Creation} items. It ensures that {@link Creation}'s
 * exist with a video and thumbnail file, as well as creating and deleting new {@link Creation} objects.
 * @author Tait & Alex
 */
public class CreationFileManager extends FileManager<Creation> {
    /**
     * The singleton instance
     */
    private static CreationFileManager instance;

    /**
     * The id to be given to the next new creation
     */
    private int nextId;

    /**
     * Private constructor for singleton
     */
    private CreationFileManager() {
        super();

        /* Check creations folder exists */
        File creationsFolder = Folder.CREATIONS.get();
        if (!creationsFolder.exists()) {
            creationsFolder.mkdir();
        }

        /* Setup items with a property extractor */
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
        /* Add listener to reserialize creations when they update */
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

        /* Find creation folders */
        File[] creationFolders = creationsFolder.listFiles(pathname -> {
            if (!pathname.isDirectory()) {
                return false;
            }
            try {
                int creationId = Integer.parseInt(pathname.getName());
                if (creationId >= nextId) {
                    nextId = creationId;
                    nextId++;
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

        /* Load creations */
        if (creationFolders != null) {
            for (File creationFolder: creationFolders) {
                try {
                    File serializedCreation = new File(creationFolder, Filename.CREATION.get());

                    FileInputStream fileInputStream = new FileInputStream(serializedCreation);
                    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

                    Creation creation = (Creation) objectInputStream.readObject();
                    if (new File(creationFolder, Filename.VIDEO.get()).exists() && new File(creationFolder, Filename.THUMBNAIL.get()).exists()) {
                        files.put(creation, creationFolder);
                        items.add(creation);
                    } else {
                        continue;
                    }

                    objectInputStream.close();
                    fileInputStream.close();
                } catch (IOException | ClassNotFoundException e) {
                    continue;
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

    /* Get a new CreationFileBuilder */
    @Override
    public CreationFileBuilder getBuilder() {
        File folder = new File(Folder.CREATIONS.get(), Integer.toString(nextId++));
        folder.mkdirs();
        return new CreationFileBuilder().setCreationFolder(folder);
    }

    /* Saves a creation. */
    @Override
    public void save(Creation creation, File folder) {
        File serializedCreation = new File(folder, Filename.CREATION.get());
        serialize(creation, serializedCreation);

        super.save(creation, folder);
    }

    /**
     * Edit a creation
     * @param newCreation
     * @param folder
     * @param oldCreation
     */
    void edit(Creation newCreation, File folder, Creation oldCreation) {
        delete(oldCreation);
        save(newCreation, folder);
    }

    /**
     * Update a creation's mutable fields by reserializing
     * @param creation The creation to update
     */
    private void update(Creation creation) {
        serialize(creation, getSerializedFile(creation));
    }

    /**
     * Get the video file of a creation
     * @param creation The creation whose video file to get
     * @return The video file of the specified creation
     */
    public File getVideoFile(Creation creation) {
        return new File(getFile(creation), Filename.VIDEO.get());
    }

    /**
     * Get the thumbnail file of a creation
     * @param creation The creation whose thumbnail file to get
     * @return The thumbnail file of the specified creation
     */
    public File getThumbnailFile(Creation creation) {
        return new File(getFile(creation), Filename.THUMBNAIL.get());
    }

    /**
     * Get the serialized file of a creation
     * @param creation The creation whose serialized file to get
     * @return The serialised file of the specified creation
     */
    private File getSerializedFile(Creation creation) {
        return new File(getFile(creation), Filename.CREATION.get());
    }

    /**
     * Serializes a {@link Creation} to a file
     * @param creation The creation to serialize
     * @param serializedCreation The file to serialize the {@link Creation} to
     */
    private void serialize(Creation creation, File serializedCreation) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(serializedCreation);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(creation);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            return;
        }
    }
}
