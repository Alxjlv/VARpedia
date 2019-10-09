package models;

import constants.FileExtension;

import java.io.File;

/**
 * Represents a users Creation
 */
public class Creation { // TODO - Make serializable?
    private String name;
    private File folder; // TODO - Add Media
    // TODO - Add chunks
    // TODO - Add images
    // TODO - Add creation time?

    public Creation(String name, File folder) {
        this.name = name;
        this.folder = folder;
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
    public File getFolder() {
        return folder;
    }

    public File getVideo() {
        return new File(folder, FileExtension.VIDEO.getExtension());
    }

    /**
     * Gets the last modified time of the creation
     * @return The last modified time as seconds since Epoch
     */
    public long getLastModified() {
        return folder.lastModified();
    }

    /**
     * Gets the duration of the creation
     * @return The duration of the creation
     */
    public long getDuration() {
        return 0;
    }
}
