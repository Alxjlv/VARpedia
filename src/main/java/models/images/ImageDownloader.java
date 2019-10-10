package models.images;

import models.Builder;
import models.FormManager;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ImageDownloader implements Builder<Map<URL,File>> {

    private int imageNum = 10;
    private ExecutorService threadPool = new ThreadPoolExecutor(0,imageNum,30, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>());

    @Override
    public Map<URL, File> build() {
        new ImageSearcher().Search(FormManager.getInstance().getCurrentSearchTerm(),imageNum);


        return null;
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

    public ImageDownloader setParams(int num){
        imageNum = num;
        return this;
    }



}
