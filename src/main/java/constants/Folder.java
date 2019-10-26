package constants;

import java.io.File;

public enum Folder {
    CREATIONS(".bin/creations/"),
    TEMP(".bin/temp/"),
    TEMP_CHUNKS(".bin/temp/chunks/"),
    MUSIC(".bin/music/"),
    IMAGES(".bin/images/");

    private final File folder;

    Folder(String folder) {
        this.folder = new File(folder);
        this.folder.mkdirs();
    }

    public File get() {
        return folder;
    }
}
