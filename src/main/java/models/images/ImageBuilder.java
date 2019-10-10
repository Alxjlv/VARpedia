package models.images;

import models.Builder;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.*;

public class ImageBuilder implements Builder<Map<URL,File>> {


    @Override
    public Map<URL, File> build() {



        return null;
    }

    public ImageBuilder setParams(URL url, File file){
        return this;
    }



}
