package models.images;

import constants.Folder;
import javafx.concurrent.Task;
import models.FileManager;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ImageFileManager is a singleton {@link FileManager} for images of type {@link URL}. It manages downloading, caching
 * and removal of image files.
 * @author Tait & Alex
 */
public class ImageFileManager extends FileManager<URL> {
    /**
     * The singleton instance
     */
    private static ImageFileManager instance;

    /**
     * A ThreadPool of 8 threads to download multiple images concurrently
     */
    private ExecutorService downloadThreads = Executors.newFixedThreadPool(8);

    /**
     * Private constructor for singleton
     */
    private ImageFileManager() {
        super();
    }

    /**
     * Get the singleton instance of ImageFileManager
     * @return The singleton instance
     */
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

    /**
     * Downloads multiple images concurrently
     * @param images The images to download
     */
    public void downloadImages(List<URL> images) {
        for (URL image : images) {
            Task<Void> downloader = new Task<Void>() {
                @Override
                protected Void call() {
                    ImageFileBuilder builder = getBuilder();
                    builder.setImage(image);
                    create(builder);
                    return null;
                }
            };
            downloadThreads.submit(downloader);
        }
    }

    /**
     * Removes all downloaded image files
     */
    public void clearImages(){
        recursiveDelete(Folder.IMAGES.get());
        Folder.IMAGES.get().mkdirs();
    }

    /* Override getFile() to download images if they do not exist */
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

    /* Return an ImageFileBuilder */
    @Override
    public ImageFileBuilder getBuilder() {
        return new ImageFileBuilder();
    }
}
