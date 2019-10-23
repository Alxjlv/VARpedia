package models.images;

import javafx.scene.control.ListCell;

import java.io.File;
import java.net.URL;

public class Thumbnail extends ListCell<Thumbnail> {

    private URL url;
    private File imageFile;

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

}
