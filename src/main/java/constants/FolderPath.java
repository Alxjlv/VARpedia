package constants;

import java.io.File;

public enum FolderPath {
    CREATIONS_FOLDER(".creations/"),
    TEMP_FOLDER(".temp/"),
    IMAGES_FOLDER(".images/");

    private File path;

    FolderPath(String path) {
        this.path = new File(path);
    }

    public File getPath() {
        return path;
    }
}
