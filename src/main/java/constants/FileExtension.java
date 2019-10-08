package constants;

public enum FileExtension {
    CHUNK_AUDIO("audio.wav"),
    COMBINED_AUDIO("combined.wav"),
    SEARCH_TEXT("search.txt"),
    VIDEO("video.mp4"),
    CHUNKS("chunks/"),
    IMAGES("images/");

    private String extension;

    FileExtension(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}
