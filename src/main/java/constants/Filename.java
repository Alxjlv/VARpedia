package constants;

/**
 * This enum is responsible for storing the file names for multiple files that are used during operation of this VARpedia
 * @author Tait & Alex
 */
public enum Filename {
    CHUNK_AUDIO("audio.wav"),
    COMBINED_AUDIO("combined.wav"),
    SEARCH_TEXT("search.txt"),
    VIDEO("video.mp4"),
    CREATION("creation.ser"),
    THUMBNAIL("thumbnail.jpg");

    private final String extension;

    Filename(String extension) {
        this.extension = extension;
    }

    public String get() {
        return extension;
    }
}
