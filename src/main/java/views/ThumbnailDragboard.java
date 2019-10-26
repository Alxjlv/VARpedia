package views;

import java.net.URL;

public class ThumbnailDragboard {
    private static final ThumbnailDragboard instance = new ThumbnailDragboard();

    private URL url;

    public static ThumbnailDragboard getInstance() {
        return instance;
    }

    public void set(URL url) {
        this.url = url;
    }

    public URL get() {
        return url;
    }
}
