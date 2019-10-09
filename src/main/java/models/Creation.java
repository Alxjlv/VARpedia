package models;

import constants.FileExtension;

import java.io.File;

/**
 * Represents a users Creation
 */
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

    /**
     * Gets the name of the creation
     * @return The name of the creation
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the video file of the creation
     * @return The video file of the creation
     */
//    public File getFolder() {
//        return videoFile;
//    }

    public File getVideoFile() {
        return videoFile;
    }

    /**
     * Gets the last modified time of the creation
     * @return The last modified time as seconds since Epoch
     */
    public long getLastModified() {
        return videoFile.lastModified();
    }

    /**
     * Gets the duration of the creation
     * @return The duration of the creation
     */
    public long getDuration() {
        return 0;
    }
}
