package constants;

public enum Filename {
    CHUNK_AUDIO("audio.wav"),
    COMBINED_AUDIO("combined.wav"),
    SEARCH_TEXT("search.txt");

    private final String extension;

    Filename(String extension) {
        this.extension = extension;
    }

    public String get() {
        return extension;
    }
}
