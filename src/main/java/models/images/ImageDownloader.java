package models.images;

import javafx.concurrent.Task;
import models.Builder;
import models.FormManager;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This class is responsible for coordinating images, and storing relevant data for a creation.
 */
public class ImageDownloader implements Builder<Map<URL,File>> {

    private int imageNum = 10;
    private Map<URL,File> imageList;
    private ExecutorService threadPool = new ThreadPoolExecutor(0,imageNum,30, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>());

    @Override
    public Map<URL, File> build() {
        new ImageSearcher().Search(FormManager.getInstance().getCurrentSearchTerm(),imageNum);
        return null;
    }

    /**
     * This method is almost a callback from the XML parsing in order to actually begin the downloading of images
     * @param urlList - A list of URLs mapped to IDs
     * @return - returns the list of images - not currently relevant
     */
    public Map<URL,File> requestComplete(Map<URL,File> urlList){
        imageList = urlList;
        Map<URL,File> downloads = checkDownloaded();
        if(!downloads.isEmpty()){
            bulkDownLoadImages(downloads);
            //downloadImages(downloads);
        }
        return imageList;
    }

    //Checks whether the images for this creation are already downloaded
    public Map<URL,File> checkDownloaded(){
        Map<URL,File> missing = new HashMap<URL, File>();
        for (URL u:imageList.keySet()){
            if(!imageList.get(u).exists()){
                missing.put(u,imageList.get(u));
            }
        }
        return missing;
    }

    //Currently this implementation is slower (~6s) than the bulk download - but this downloads an image per thread
    public void downloadImages(Map<URL,File> urlList){
        long startTime = System.currentTimeMillis();
        AtomicLong endTime = new AtomicLong();
        for(URL u:urlList.keySet()){
            ImageDownload download = new ImageDownload(u,urlList.get(u));
            threadPool.submit(download);
            download.setOnSucceeded(event -> {
                endTime.set(System.currentTimeMillis());
                System.out.println("current time taken: "+(endTime.get() - startTime));
            });
        }
    }

    //Downloads the images all in one thread (~1.5s)
    public void bulkDownLoadImages(Map<URL,File> urlList){
        long startTime = System.currentTimeMillis();
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                for(URL u:urlList.keySet()){
                    try(InputStream in = u.openStream()){
                        Files.copy(in, Paths.get(urlList.get(u).toString()));
                        System.out.println("downloaded image with id "+urlList.get(u).getName());
                    }
                }
                return null;
            }
        };
        threadPool.submit(task);
        task.setOnSucceeded(event -> {
            System.out.println("time taken: " + (System.currentTimeMillis()-startTime));
        });
    }

    //Setting the number of images needed
    public ImageDownloader setParams(int num){
        imageNum = num;
        return this;
    }

    public Map<URL,File> getImageList(){
        return new HashMap<>(imageList);
    }
}
