package models;

import java.io.File;

public class Creation { // TODO - Make serializable?
    private String name;
    private File videoFile; // TODO - Add Media
    // TODO - Add chunks
    // TODO - Add images
    // TODO - Add creation time?

    public Creation(String name, File videoFile) {
        this.name = name;
        this.videoFile = videoFile;
    }

    public String getName() {
        return name;
    }

    public File getVideoFile() {
        return videoFile;
    }

    public long getLastModified() {
        return videoFile.lastModified();
    }

    public long getDuration() {
        return 0;
    }
}
