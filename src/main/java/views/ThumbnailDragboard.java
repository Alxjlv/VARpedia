package views;

import java.net.URL;

public class ThumbnailDragboard extends ObjectDragboard<URL> {
    private static final ThumbnailDragboard instance = new ThumbnailDragboard();

    public static ThumbnailDragboard getInstance() {
        return instance;
    }
}
