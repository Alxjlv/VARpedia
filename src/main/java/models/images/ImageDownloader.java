package models.images;

import models.Builder;
import models.FormManager;

import java.io.File;
import java.net.URL;
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
            downloadImages(downloads);
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

    public ImageDownloader setParams(int num){
        imageNum = num;
        return this;
    }

    public Map<URL,File> getImageList(){
        return new HashMap<>(imageList);
    }
}
