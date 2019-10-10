package models.images;

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
    private ExecutorService threadPool = new ThreadPoolExecutor(0,10,30, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>());



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

    public void search(String search,int num){
        new ImageSearcher().Search(search,num);
    }

    public Map<URL,File> requestComplete(Map<URL,File> urlList){
        for(URL u:urlList.keySet()){
            if(!urlList.get(u).exists()){
                ImageDownload download = new ImageDownload(u,urlList.get(u));
                threadPool.submit(download);
            }
        }
        return urlList;
    }

    public void clearImages(){
        recursiveDelete(imageFolder); //TODO - refactor to use constants
    }

    @Override
    public ImageDownloader getBuilder() {
        return new ImageDownloader();
    }
}
