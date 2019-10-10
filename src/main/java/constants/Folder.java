package constants;

import java.io.File;

public enum Folder {
    CREATIONS(".creations"),
//    CREATIONS_VIDEO(".creations/videos/"),
//    CREATIONS_SERIALIZED(".creations/serialized/"),
    TEMP(".temp"),
    TEMP_CHUNKS(".temp/chunks/"),
    TEMP_IMAGES(".temp/images/"),
    IMAGES(".images/");

    private final File folder;

    Folder(String folder) {
        this.folder = new File(folder);
    }

    public File get() {
        return folder;
    }
}
