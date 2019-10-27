package models;

import constants.Folder;
import constants.Music;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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

public class FormManager {

    public enum Mode {
        CREATE,
        EDIT;
    }

    private static FormManager instance;
    private Mode mode;
    private ReadOnlyStringWrapper progressMessage = new ReadOnlyStringWrapper();
    private ReadOnlyObjectWrapper<CreationFileBuilder.State> progressState = new ReadOnlyObjectWrapper<>();

    // Fields
    private StringProperty name = new SimpleStringProperty();
    private StringProperty searchTerm = new SimpleStringProperty();
    private StringProperty searchResult = new SimpleStringProperty();
    private ListProperty<URL> images = new SimpleListProperty<>();
    private ObjectProperty<Music> backgroundMusic = new SimpleObjectProperty<>();

    private FormManager() {
        mode = Mode.CREATE;

        setName("");
        setSearchTerm("");
        setSearchResult("");
        setImages(FXCollections.observableArrayList());
        setBackgroundMusic(Music.TRACK_NONE);
    }

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

    public void reset() {
        ChunkFileManager.getInstance().reset();

        File tempFolder = Folder.TEMP.get();
        recursiveDelete(tempFolder);
        tempFolder.mkdirs();

        setMode(Mode.CREATE);

        instance.searchTerm.set(null);
        instance.searchResult.set(null);
        instance.images.clear();
        instance.name.set(null);
        instance.backgroundMusic.set(null);
    }

    public Mode getMode() {
        return mode;
    }
    private void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setEdit(Creation creation) {
        reset();
        setMode(mode.EDIT);

        ChunkFileManager chunkManager = ChunkFileManager.getInstance();
        for (Chunk chunk : creation.getChunks()) {
            ChunkFileBuilder builder = chunkManager.getBuilder();
            builder.setText(chunk.getText());
            builder.setSynthesizer(chunk.getSynthesizer());
            chunkManager.create(builder);
        }

        setSearchTerm(creation.getSearchTerm());
        setSearchResult(creation.getSearchResult());
        setImages(FXCollections.observableArrayList(creation.getImages()));
        setName(creation.getName());
        setBackgroundMusic(creation.getBackgroundMusic());

        ImageSearcher imageSearcher = new ImageSearcher();
        imageSearcher.search(getSearchTerm(), 15);

        getImages().addListener(new ListChangeListener<URL>() {
            @Override
            public void onChanged(Change<? extends URL> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        System.out.println("image added: "+c.getList().get(c.getFrom()));
                    }
                }
            }
        });
    }

    public void build() {
        CreationFileBuilder builder = CreationFileManager.getInstance().getBuilder();
        builder.setName(getName());
        builder.setSearchTerm(getSearchTerm());
        builder.setSearchResult(getSearchResult());
        builder.setImages(getImages());
        builder.setBackgroundMusic(getBackgroundMusic());
        builder.setEdit(getMode() == Mode.EDIT);

        progressMessage.bind(builder.progressMessageProperty());
        progressState.bind(builder.stateProperty());

        CreationFileManager.getInstance().create(builder);
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

    public String getSearchResult() {
        return searchResult.get();
    }
    public void setSearchResult(String searchResult) {
        this.searchResult.set(searchResult);
    }
    public StringProperty searchResultProperty() {
        return searchResult;
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
