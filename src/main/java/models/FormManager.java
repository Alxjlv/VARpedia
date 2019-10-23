package models;

import constants.Folder;
import constants.Music;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.chunk.Chunk;
import models.chunk.ChunkBuilder;
import models.chunk.ChunkManager;
import models.creation.Creation;
import models.images.ImageDownloader;
import models.images.ImageManager;

import java.io.File;
import java.net.URL;

public class FormManager {

    public enum State {
        CREATE,
        EDIT;
    }

    private ImageDownloader currentDownloader;
    private static FormManager instance;
    private State state;

    // Fields
    private StringProperty name = new SimpleStringProperty();
    private StringProperty searchTerm = new SimpleStringProperty();
    private StringProperty searchText = new SimpleStringProperty();
    private ListProperty<URL> images = new SimpleListProperty<>();
    private ObjectProperty<URL> thumbnail = new SimpleObjectProperty<>();
    private ObjectProperty<Music> backgroundMusic = new SimpleObjectProperty<>();
    private StringProperty numberOfImages = new SimpleStringProperty();


    private FormManager() {
        state = State.CREATE;
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
        ChunkManager.getInstance().reset();

        File tempFolder = Folder.TEMP.get();
        recursiveDelete(tempFolder);
        tempFolder.mkdirs();

        setState(State.CREATE);

        instance.searchTerm.set(null);
        instance.searchText.set(null);
        instance.images.clear();
        instance.thumbnail.set(null);
        instance.name.set(null);
        instance.backgroundMusic.set(null);
    }

    public State getState() {
        return state;
    }
    private void setState(State state) {
        this.state = state;
    }

    public void setEdit(Creation creation) {
        reset();
        setState(state.EDIT);

        ChunkManager chunkManager = ChunkManager.getInstance();
        for (Chunk chunk : creation.getChunks()) {
            ChunkBuilder builder = chunkManager.getBuilder();
            builder.setText(chunk.getText());
            builder.setSynthesizer(chunk.getSynthesizer());
            chunkManager.create(builder);
        }

        setSearchTerm(creation.getSearchTerm());
        setSearchText(creation.getSearchText());
        setImages(FXCollections.observableArrayList(creation.getImages()));
        setThumbnail(creation.getThumbnail());
        setName(creation.getName());
        setBackgroundMusic(creation.getBackgroundMusic());
        setNumberOfImages(Integer.toString(creation.getNumberOfImages()));

        ImageManager.getInstance().search(15);
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

    public URL getThumbnail() {
        return thumbnail.get();
    }
    public void setThumbnail(URL thumbnail) {
        this.thumbnail.set(thumbnail);
    }
    public ObjectProperty<URL> thumbnailProperty() {
        return thumbnail;
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

    public String getNumberOfImages(){
        return numberOfImages.get();
    }
    public void setNumberOfImages(String numberOfImages) {
        this.numberOfImages.set(numberOfImages);
    }
    public StringProperty numberOfImagesProperty(){
        return numberOfImages;
    }

    public void setCurrentDownloader(ImageDownloader downloader){
        currentDownloader = downloader;
    }

    public ImageDownloader getCurrentDownloader(){
        return currentDownloader;
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
