package models.images;

import constants.Folder;
import models.FileManager;

import java.io.File;
import java.net.URL;

/**
 * Overall manager class for Image processing and downloading. It is a singleton responsible for managing the images
 * folder and calling searches.
 */
public class ImageFileManager extends FileManager<URL> {

    private static ImageFileManager instance;
    private File imageFolder = Folder.IMAGES.get();

    private ImageFileManager() {
        super();
    }

    //Thread-safe access to the singleton
    public static ImageFileManager getInstance() {
        if (instance == null) {
            synchronized (ImageFileManager.class) {
                if (instance == null) {
                    instance = new ImageFileManager();
                }
            }
        }
        return instance;
    }

    @Override
    public File getFile(URL image) {
        if (files.containsKey(image)) {
            return super.getFile(image);
        } else {
            ImageFileBuilder builder = getBuilder().setImage(image);
            create(builder);
            return getFile(image);
        }
    }

    public void clearImages(){
        recursiveDelete(imageFolder); //TODO - refactor to use constants
        Folder.IMAGES.get().mkdirs();
    }

    @Override
    public ImageFileBuilder getBuilder() {
        return new ImageFileBuilder();
    }

    public void reorder(URL source, URL target) {
        items.add(items.indexOf(target), items.remove(items.indexOf(source)));
    }
}
