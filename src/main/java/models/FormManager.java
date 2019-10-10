package models;

import models.images.ImageDownload;
import models.images.ImageDownloader;

public class FormManager {

    private ImageDownloader currentDownloader;
    private static FormManager instance;
    private String currentSearchTerm;

    private FormManager(){}

    public static FormManager getInstance() {
        if (instance == null) {
            synchronized (FormManager.class) {
                if (instance == null) {
                    instance = new FormManager();
                }
            }
        }
        return instance;
    }

    public void setCurrentDownloader(ImageDownloader downloader){
        currentDownloader = downloader;
    }

    public ImageDownloader getCurrentDownloader(){
        return currentDownloader;
    }

    public void setCurrentSearchTerm(String searchTerm){
        currentSearchTerm = searchTerm;
    }

    public String getCurrentSearchTerm() {
        return currentSearchTerm;
    }
}
