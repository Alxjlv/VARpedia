package constants;

import java.io.File;

/**
 * This enum is responsible for storing the filepaths for multiple folders that are used by VARpedia
 * @author Tait & Alex
 */
public enum Folder {
    CREATIONS(".bin/creations/"),
    TEMP(".bin/temp/"),
    TEMP_CHUNKS(".bin/temp/chunks/"),
    MUSIC(".bin/music/"),
    IMAGES(".bin/images/");

    private final File folder;

    Folder(String folder) {
        // Making a new folder with the folder path
        this.folder = new File(folder);
        this.folder.mkdirs();
    }

    public File get() {
        return folder;
    }
}
