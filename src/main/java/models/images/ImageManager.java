package models.images;

import models.FormManager;
import models.Manager;
import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ImageManager extends Manager<Map<URL, File>> {

    private static ImageManager instance;
    private File imageFolder = new File(".images");



    private ImageManager(){
        imageFolder.mkdir();
    }

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

    public void search(){
        FormManager.getInstance().setCurrentDownloader(this.getBuilder());
        FormManager.getInstance().getCurrentDownloader().build();
    }

    public void search(int num){
        FormManager.getInstance().setCurrentDownloader(this.getBuilder());
        FormManager.getInstance().getCurrentDownloader().setParams(15).build();
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
