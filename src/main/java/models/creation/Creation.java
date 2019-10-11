package models.creation;

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

    private StringProperty name;
    private ReadOnlyStringWrapper searchTerm;
    private StringProperty searchText;
    private IntegerProperty confidenceRating;
    private ReadOnlyIntegerWrapper viewCount;
    private ReadOnlyObjectWrapper<File> videoFile; // TODO - Make media?
    private ReadOnlyObjectWrapper<List<Chunk>> chunks;
    private ReadOnlyObjectWrapper<List<URL>> images;
    // TODO - Add creation time?

    /**
     * Constructor only to be called by deserializer
     */
    public Creation() {
        this(null, null, null, null, null, null);
    }

    /**
     * Constructor to be called by CreationBuilder. Package private.
     * @param name
     * @param searchTerm
     * @param searchText
     * @param videoFile
     * @param chunks
     * @param images
     */
    Creation(String name, String searchTerm, String searchText, File videoFile, List<Chunk> chunks, List<URL> images) {
        this.name = new SimpleStringProperty(name);
        this.searchTerm = new ReadOnlyStringWrapper(searchTerm);
        this.searchText = new SimpleStringProperty(searchText);
        this.confidenceRating = new SimpleIntegerProperty(0);
        this.viewCount = new ReadOnlyIntegerWrapper(0);
        this.videoFile = new ReadOnlyObjectWrapper<>(videoFile);
        this.chunks = new ReadOnlyObjectWrapper<>(chunks);
        this.images = new ReadOnlyObjectWrapper<>(images);
    }

    /**
     * Gets the name of the creation
     * @return The name of the creation
     */
    public String getName() {
        return nameProperty().get();
    }

    public void setName(String name) {
        nameProperty().set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getSearchTerm() {
        return searchTermProperty().get();
    }

    public ReadOnlyStringProperty searchTermProperty() {
        return searchTerm.getReadOnlyProperty();
    }

    public String getSearchText() {
        return searchTextProperty().get();
    }

    public void setSearchText(String searchText) {
        searchTextProperty().set(searchText);
    }

    public StringProperty searchTextProperty() {
        return searchText;
    }

    public int getConfidenceRating() {
        return confidenceRatingProperty().get();
    }

    public void setConfidenceRating(int confidenceRating) {
        confidenceRatingProperty().set(confidenceRating);
    }

    public IntegerProperty confidenceRatingProperty() {
        return confidenceRating;
    }

    public int getViewCount() {
        return viewCountProperty().get();
    }

    public void incrementViewCount() {
        int count = getViewCount();
        viewCount.set(count++);
    }

    public ReadOnlyIntegerProperty viewCountProperty() {
        return viewCount.getReadOnlyProperty();
    }

    public File getVideoFile() {
        return videoFileProperty().get();
    }

    public ReadOnlyObjectProperty<File> videoFileProperty() {
        return videoFile.getReadOnlyProperty();
    }

    public List<Chunk> getChunks() {
        return chunksProperty().get();
    }

    public ReadOnlyObjectProperty<List<Chunk>> chunksProperty() {
        return chunks.getReadOnlyProperty();
    }

    public List<URL> getImages() {
        return imagesProperty().get();
    }

    public ReadOnlyObjectProperty<List<URL>> imagesProperty() {
        return images.getReadOnlyProperty();
    }

    /**
     * Gets the last modified time of the creation
     * @return The last modified time as seconds since Epoch
     */
    public long getLastModified() {
        return getVideoFile().lastModified();
    }

    /**
     * Gets the duration of the creation
     * @return The duration of the creation
     */
    public long getDuration() {
        return 0;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(name.get());
        out.writeUTF(searchTerm.get());
        out.writeUTF(searchText.get());
        out.writeInt(confidenceRating.get());
        out.writeInt(viewCount.get());
        out.writeObject(videoFile.get());
        out.writeObject(chunks.get());
        out.writeObject(images.get());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name.set(in.readUTF());
        searchTerm.set(in.readUTF());
        searchText.set(in.readUTF());
        confidenceRating.set(in.readInt());
        viewCount.set(in.readInt());
        videoFile.set((File) in.readObject());
        chunks.set((List<Chunk>) in.readObject());
        images.set((List<URL>) in.readObject());
    }
}
