package models.creation;

import constants.Music;
import javafx.beans.property.*;
import models.chunk.Chunk;

import java.io.*;
import java.net.URL;
import java.util.List;

/**
 * Represents a users Creation
 */
public class Creation implements Externalizable {
    private static final long serialVersionUID = 361870838792448692L;

    // Mutable properties
    private StringProperty name = new SimpleStringProperty();
    private IntegerProperty confidenceRating = new SimpleIntegerProperty();
    private ReadOnlyIntegerWrapper viewCount = new ReadOnlyIntegerWrapper();
    private StringProperty searchText = new SimpleStringProperty();
    // TODO - Date last reviewed

    // Immutable properties
    private ReadOnlyStringWrapper searchTerm = new ReadOnlyStringWrapper();
    private ReadOnlyObjectWrapper<List<Chunk>> chunks = new ReadOnlyObjectWrapper<>();
    private ReadOnlyObjectWrapper<List<URL>> images = new ReadOnlyObjectWrapper<>();
    private ReadOnlyObjectWrapper<Music> backgroundMusic = new ReadOnlyObjectWrapper<>();
    // TODO - Date created

    /**
     * Public Default Constructor only to be called by deserializer
     */
    public Creation() {
        this(null, null, null, null, null, null);
    }

    /**
     * Constructor to be called by CreationBuilder. Package private.
     * @param name
     * @param searchTerm
     * @param searchText
     * @param chunks
     * @param images
     * @param backgroundMusic
     */
    Creation(String name, String searchTerm, String searchText, List<Chunk> chunks, List<URL> images,
             Music backgroundMusic) {
        setName(name);
        setSearchTerm(searchTerm);
        setSearchText(searchText);
        setChunks(chunks);
        setImages(images);
        setBackgroundMusic(backgroundMusic);
        setConfidenceRating(0);
        setViewCount(0);
    }

    /**
     * Gets the last modified time of the creation
     * @return The last modified time as seconds since Epoch
     */
    public long getLastModified() {
        return 0; // TODO
    }

    public void incrementViewCount() {
        setViewCount(getViewCount()+1);
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
    private void setSearchTerm(String searchTerm) {
        this.searchTerm.set(searchTerm);
    }
    public ReadOnlyStringProperty searchTermProperty() {
        return searchTerm.getReadOnlyProperty();
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

    public int getConfidenceRating() {
        return confidenceRating.get();
    }
    public void setConfidenceRating(int confidenceRating) {
        this.confidenceRating.set(confidenceRating);
    }
    public IntegerProperty confidenceRatingProperty() {
        return confidenceRating;
    }

    public int getViewCount() {
        return viewCount.get();
    }
    private void setViewCount(int viewCount) {
        this.viewCount.set(viewCount);
    }
    public ReadOnlyIntegerProperty viewCountProperty() {
        return viewCount.getReadOnlyProperty();
    }

    public List<Chunk> getChunks() {
        return chunks.get();
    }
    private void setChunks(List<Chunk> chunks) {
        this.chunks.set(chunks);
    }
    public ReadOnlyObjectProperty<List<Chunk>> chunksProperty() {
        return chunks.getReadOnlyProperty();
    }

    public List<URL> getImages() {
        return images.get();
    }
    private void setImages(List<URL> images) {
        this.images.set(images);
    }
    public ReadOnlyObjectProperty<List<URL>> imagesProperty() {
        return images.getReadOnlyProperty();
    }

    public Music getBackgroundMusic() {
        return backgroundMusic.get();
    }
    private void setBackgroundMusic(Music backgroundMusic) {
        this.backgroundMusic.set(backgroundMusic);
    }
    public ReadOnlyObjectProperty<Music> backgroundMusicProperty() {
        return backgroundMusic.getReadOnlyProperty();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(getName());
        out.writeUTF(getSearchTerm());
        out.writeUTF(getSearchText());
        out.writeObject(getChunks());
        out.writeObject(getImages());
        out.writeObject(getBackgroundMusic());
        out.writeInt(getConfidenceRating());
        out.writeInt(getViewCount());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        setName(in.readUTF());
        setSearchTerm(in.readUTF());
        setSearchText(in.readUTF());
        setChunks((List<Chunk>) in.readObject());
        setImages((List<URL>) in.readObject());
        setBackgroundMusic((Music) in.readObject());
        setConfidenceRating(in.readInt());
        setViewCount(in.readInt());
    }
}
