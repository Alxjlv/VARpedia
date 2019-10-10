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

public class ImageDownloader implements Builder<Map<URL,File>> {

    private int imageNum = 10;
    private Map<URL,File> imageList;
    private ExecutorService threadPool = new ThreadPoolExecutor(0,imageNum,30, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>());

    @Override
    public Map<URL, File> build() {
        new ImageSearcher().Search(FormManager.getInstance().getCurrentSearchTerm(),imageNum);
        return null;
    }

    public Map<URL,File> requestComplete(Map<URL,File> urlList){

        imageList = urlList;
        Map<URL,File> downloads = checkDownloaded();
        if(!downloads.isEmpty()){
            bulkDownLoadImages(downloads);
            //downloadImages(downloads);
        }


        return imageList;
    }

    public Map<URL,File> checkDownloaded(){
        Map<URL,File> missing = new HashMap<URL, File>();
        for (URL u:imageList.keySet()){
            if(!imageList.get(u).exists()){
                missing.put(u,imageList.get(u));
            }
        }
        return missing;
    }

    //Currently this implementation is much slower
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

    public ImageDownloader setParams(int num){
        imageNum = num;
        return this;
    }

    public Map<URL,File> getImageList(){
        return new HashMap<>(imageList);
    }
}
