package models.images;

import models.Builder;

import java.io.File;
import java.net.URL;
import java.util.Map;

public class ImageDownloader implements Builder<Map<URL,File>> {


    @Override
    public Map<URL, File> build() {



        return null;
    }

    public ImageDownloader setParams(URL url, File file){
        return this;
    }



}
