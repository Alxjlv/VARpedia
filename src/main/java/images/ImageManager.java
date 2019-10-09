package images;

import models.Builder;
import models.Manager;
import java.io.File;

public class ImageManager extends Manager<File> {

    private static ImageManager instance;

    private ImageManager(){}

    public static ImageManager getInstance() {
        if (instance == null) {
            synchronized (ImageManager.class) {
                if (instance == null) {
                    instance = new ImageManager();
                }
            }
        }
        return instance;
    }

    

    @Override
    public Builder getBuilder() {
        return null;
    }
}
