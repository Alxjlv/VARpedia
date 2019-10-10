package models;

import models.creation.Creation;

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

    private Creation creation;

    public void setCreation(Creation creation) {
        this.creation = creation;
    }

    public Creation getCreation() {
        return creation;
    }
}
