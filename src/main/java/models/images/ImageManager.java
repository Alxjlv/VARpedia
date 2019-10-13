package models.images;

import constants.Folder;
import models.FormManager;
import models.Manager;
import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Overall manager class for Image processing and downloading. It is a singleton responsible for managing the images
 * folder and calling searches.
 */
public class ImageManager extends Manager<Map<URL, File>> {

    private static ImageManager instance;
    private File imageFolder = Folder.IMAGES.get();

//    //Making sure the image folder exists
//    private ImageManager(){
//        imageFolder.mkdir();
//    }

    //Thread-safe access to the singleton
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

    //Sends data to the FormManager Singleton
    public void search(){
        FormManager.getInstance().setCurrentDownloader(this.getBuilder());
        FormManager.getInstance().getCurrentDownloader().build();
    }

    //Overloaded search - allows setting of the number of images downloaded to greater than 10
    public void search(int num){
        FormManager.getInstance().setCurrentDownloader(this.getBuilder());
        FormManager.getInstance().getCurrentDownloader().setParams(num).build();
        //
    }

    public void clearImages(){
        recursiveDelete(imageFolder); //TODO - refactor to use constants
    }

    @Override
    public ImageDownloader getBuilder() {
        return new ImageDownloader();
    }
}
