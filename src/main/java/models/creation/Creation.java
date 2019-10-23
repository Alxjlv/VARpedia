package models.creation;

import constants.Music;
import javafx.beans.property.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import models.chunk.Chunk;

import java.io.*;
import java.net.URL;
import java.util.List;

/**
 * Represents a users Creation
 */
public class Creation implements Externalizable {
    private static final long serialVersionUID = 361870838792448692L;

    private StringProperty name = new SimpleStringProperty();
    private ReadOnlyStringWrapper searchTerm = new ReadOnlyStringWrapper();
    private StringProperty searchText = new SimpleStringProperty();
    private ReadOnlyObjectWrapper<List<Chunk>> chunks = new ReadOnlyObjectWrapper<>();
    private ReadOnlyObjectWrapper<List<URL>> images = new ReadOnlyObjectWrapper<>();
    private ReadOnlyObjectWrapper<URL> thumbnail = new ReadOnlyObjectWrapper<>();
    private ReadOnlyObjectWrapper<Music> backgroundMusic = new ReadOnlyObjectWrapper<>();
    // TODO - Add creation time?
    private ReadOnlyObjectWrapper<File> videoFile = new ReadOnlyObjectWrapper<File>() {
        @Override
        public void set(File file) {
            super.set(file);
            if (file != null) {
                MediaPlayer mediaPlayer = new MediaPlayer(new Media(file.toURI().toString()));
                mediaPlayer.setOnReady(() -> setDuration(mediaPlayer.getMedia().getDuration()));
            }
        }
    }; // TODO - Make media?
    private ReadOnlyObjectWrapper<File> thumbnailFile = new ReadOnlyObjectWrapper<>();
    private IntegerProperty confidenceRating = new SimpleIntegerProperty();
    private ReadOnlyIntegerWrapper viewCount = new ReadOnlyIntegerWrapper();

//    private MediaPlayer videoPlayer;
    private ReadOnlyObjectWrapper<Duration> duration = new ReadOnlyObjectWrapper<>();

    /**
     * Constructor only to be called by deserializer
     */
    public Creation() {
        this(null, null, null, null, null, null, null, null, null);
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
    Creation(String name, String searchTerm, String searchText, List<Chunk> chunks, List<URL> images, URL thumbnail,
             Music backgroundMusic, File videoFile, File thumbnailFile) {
        setName(name);
        setSearchTerm(searchTerm);
        setSearchText(searchText);
        setChunks(chunks);
        setImages(images);
        setThumbnail(thumbnail);
        setBackgroundMusic(backgroundMusic);
        setVideoFile(videoFile);
        setThumbnailFile(thumbnailFile);
        setConfidenceRating(0);
        setViewCount(0);
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
    public Duration getDuration() {
        return duration.get();
    }
    private void setDuration(Duration duration) {
        this.duration.set(duration);
    }
    public ReadOnlyObjectProperty<Duration> durationProperty() {
        return duration.getReadOnlyProperty();
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

    public File getVideoFile() {
        return videoFile.get();
    }
    private void setVideoFile(File videoFile) {
        this.videoFile.set(videoFile);
    }
    public ReadOnlyObjectProperty<File> videoFileProperty() {
        return videoFile.getReadOnlyProperty();
    }

    public File getThumbnailFile() {
        return thumbnailFile.get();
    }
    private void setThumbnailFile(File videoFile) {
        this.thumbnailFile.set(videoFile);
    }
    public ReadOnlyObjectProperty<File> thumbnailFileProperty() {
        return thumbnailFile.getReadOnlyProperty();
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

    public URL getThumbnail() {
        return thumbnail.get();
    }
    private void setThumbnail(URL thumbnail) {
        this.thumbnail.set(thumbnail);
    }
    public ReadOnlyObjectProperty<URL> thumbnailProperty() {
        return thumbnail.getReadOnlyProperty();
    }

    public Music getBackgroundMusic() {
        return backgroundMusic.get();
    }
    public void setBackgroundMusic(Music backgroundMusic) {
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
        out.writeObject(getThumbnail());
        out.writeObject(getBackgroundMusic());
        out.writeObject(getVideoFile());
        out.writeObject(getThumbnailFile());
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
        setThumbnail((URL) in.readObject());
        setBackgroundMusic((Music) in.readObject());
        setVideoFile((File) in.readObject());
        setThumbnailFile((File) in.readObject());
        setConfidenceRating(in.readInt());
        setViewCount(in.readInt());
    }
}
