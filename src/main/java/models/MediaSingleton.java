package models;

import javafx.scene.media.Media;

public class MediaSingleton {
    private static MediaSingleton instance;

    private MediaSingleton() {
    }

    public static MediaSingleton getInstance() {
        if (instance == null) {
            synchronized (MediaSingleton.class) {
                if (instance == null) {
                    instance = new MediaSingleton();
                }
            }
        }
        return instance;
    }

    private Media media;

    public void setMedia(Media media) {
        this.media = media;
    }

    public Media getMedia() {
        return media;
    }
}
