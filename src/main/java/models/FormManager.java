package models;

import constants.Folder;
import constants.Music;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.chunk.Chunk;
import models.chunk.ChunkFileBuilder;
import models.chunk.ChunkFileManager;
import models.creation.Creation;
import models.creation.CreationFileBuilder;
import models.creation.CreationFileManager;
import models.images.ImageSearcher;

import java.io.File;
import java.net.URL;

/**
 * FormManager is a singleton that maintains the state of the form to Create and Edit {@link Creation}'s across the
 * pages {@link controllers.SearchView}, {@link controllers.ChunkView}, {@link controllers.ImagePreView} &
 * {@link controllers.NameView}. FormManager has two different {@link Mode}'s; {@code CREATE} &
 * {@code EDIT}, which alters the behaviour of these pages.
 * @author Tait & Alex
 */
public class FormManager {

    /**
     * The Mode's a FormManager may have
     */
    public enum Mode {
        CREATE,
        EDIT;
    }

    /**
     * Singleton instance
     */
    private static FormManager instance;

    /* Form state fields */
    /**
     * The {@link Mode} of the FormManager
     */
    private ReadOnlyObjectWrapper<Mode> mode = new ReadOnlyObjectWrapper<>();
    /**
     * This field maintains the progress message of {@link CreationFileBuilder}
     */
    private ReadOnlyStringWrapper progressMessage = new ReadOnlyStringWrapper();
    /**
     * This field maintains the progress state of {@link CreationFileBuilder}
     */
    private ReadOnlyObjectWrapper<CreationFileBuilder.State> progressState = new ReadOnlyObjectWrapper<>();

    /* Form data fields */
    /**
     * The name for the {@link Creation} to be built
     */
    private StringProperty name = new SimpleStringProperty();
    /**
     * The search term for the {@link Creation} to be built
     */
    private StringProperty searchTerm = new SimpleStringProperty();
    /**
     * The search text for the {@link Creation} to be built
     */
    private StringProperty searchText = new SimpleStringProperty();
    /**
     * The images for the {@link Creation} to be built
     */
    private ListProperty<URL> images = new SimpleListProperty<>();
    /**
     * The background music for the {@link Creation} to be built
     */
    private ObjectProperty<Music> backgroundMusic = new SimpleObjectProperty<>();

    /**
     * Constructs a FormManager with default data fields
     */
    private FormManager() {
        setMode(Mode.CREATE);

        setName("");
        setSearchTerm("");
        setSearchText("");
        setImages(FXCollections.observableArrayList());
        setBackgroundMusic(Music.TRACK_NONE);
    }

    /**
     * Get the singleton FormManager
     * @return The singleton FormManager
     */
    public static FormManager getInstance() {
        if (instance == null) {
            synchronized (FormManager.class) {
                if (instance == null) {
                    instance = new FormManager();
                }
            }
        }
        return instance;
    }

    /**
     * Resets the data fields of the FormManager
     */
    public void reset() {
        /* Reset ChunkFileManager */
        ChunkFileManager.getInstance().reset();

        /* Remove any temporary files */
        File tempFolder = Folder.TEMP.get();
        recursiveDelete(tempFolder);
        tempFolder.mkdirs();

        /* Set mode of FormManger */
        setMode(Mode.CREATE);

        /* Clear data fields */
        setName("");
        setSearchTerm("");
        setSearchText("");
        getImages().clear();
        setBackgroundMusic(Music.TRACK_NONE);
    }

    /**
     * Set the FormManger to edit a creation. The data fields of the FormManager will be set to match the provided
     * {@link Creation}
     * @param creation The {@link Creation} to edit
     */
    public void setEdit(Creation creation) {
        /* Reset FormManager */
        reset();
        setMode(Mode.EDIT);

        /* Create temporary Chunk audio files */
        ChunkFileManager chunkManager = ChunkFileManager.getInstance();
        for (Chunk chunk : creation.getChunks()) {
            ChunkFileBuilder builder = chunkManager.getBuilder();
            builder.setText(chunk.getText());
            builder.setSynthesizer(chunk.getSynthesizer());
            chunkManager.create(builder);
        }

        /* Setup FormManager fields */
        setSearchTerm(creation.getSearchTerm());
        setSearchText(creation.getSearchText());
        setImages(FXCollections.observableArrayList(creation.getImages()));
        setName(creation.getName());
        setBackgroundMusic(creation.getBackgroundMusic());

        /* Download images for the creation's search term */
        ImageSearcher imageSearcher = new ImageSearcher();
        imageSearcher.Search(getSearchTerm(), 15);
    }

    /**
     * Builds a {@link Creation} object using the data fields contained by the FormManger using
     * {@link CreationFileManager} and {@link CreationFileBuilder}.
     */
    public void build() {
        /* Setup the builder */
        CreationFileBuilder builder = CreationFileManager.getInstance().getBuilder();
        builder.setName(getName());
        builder.setSearchTerm(getSearchTerm());
        builder.setSearchText(getSearchText());
        builder.setImages(getImages());
        builder.setBackgroundMusic(getBackgroundMusic());
        builder.setEdit(getMode() == Mode.EDIT);

        /* Observe the progress of the builder */
        progressMessage.bind(builder.progressMessageProperty());
        progressState.bind(builder.stateProperty());

        /* Build the creation */
        CreationFileManager.getInstance().create(builder);
    }

    /* JavaFX Beans (getters, setters and properties) */

    public Mode getMode() {
        return mode.get();
    }
    private void setMode(Mode mode) {
        this.mode.set(mode);
    }
    private ReadOnlyObjectProperty<Mode> modeProperty() {
        return mode.getReadOnlyProperty();
    }

    public String getName() {
        return name.get();
    }
    public void setName(String name) {
        this.name.set(name);
    }
    public StringProperty nameProperty() {
        return name;
    }

    public String getSearchTerm() {
        return searchTerm.get();
    }
    public void setSearchTerm(String searchTerm) {
        this.searchTerm.set(searchTerm);
    }
    public StringProperty searchTermProperty() {
        return searchTerm;
    }

    public String getSearchText() {
        return searchText.get();
    }
    public void setSearchText(String searchText) {
        this.searchText.set(searchText);
    }
    public StringProperty searchTextProperty() {
        return searchText;
    }

    public ObservableList<URL> getImages() {
        return images.get();
    }
    public void setImages(ObservableList<URL> images) {
        this.images.set(images);
    }
    public ListProperty<URL> imagesProperty() {
        return images;
    }

    public Music getBackgroundMusic() {
        return backgroundMusic.get();
    }
    public void setBackgroundMusic(Music backgroundMusic) {
        this.backgroundMusic.set(backgroundMusic);
    }
    public ObjectProperty<Music> backgroundMusicProperty() {
        return backgroundMusic;
    }

    public String getProgressMessage() {
        return progressMessage.get();
    }
    public void setProgressMessage(String progressMessage) {
        this.progressMessage.set(progressMessage);
    }
    public ReadOnlyStringProperty progressMessageProperty() {
        return progressMessage.getReadOnlyProperty();
    }

    public CreationFileBuilder.State getProgressState() {
        return progressState.get();
    }
    public ReadOnlyObjectProperty<CreationFileBuilder.State> progressStateProperty() {
        return progressState.getReadOnlyProperty();
    }

    /**
     * Helper method to recursively delete a directory
     * @param directory The directory to delete
     * @return True if the deletion succeeded
     */
    private boolean recursiveDelete(File directory) {
        if (directory.isDirectory()) {
            File[] children = directory.listFiles();
            for (File child: children) {
                boolean status = recursiveDelete(child);
                if (!status) {
                    return false;
                }
            }
        }
        return directory.delete();
    }
}
