package models;

import java.io.File;

public class Creation {
    private String name;
    private File videoFile;

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
