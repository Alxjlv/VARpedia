package images;

import javafx.concurrent.Task;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class ImageDownload extends Task<Void> {
    private HashMap<File,URL> urlList;

    public ImageDownload(HashMap<File,URL> urls){
        urlList=urls;
    }

    @Override
    protected Void call() throws Exception {
        System.out.println("Starting download");
        for(File f:urlList.keySet()){
            try(InputStream in = urlList.get(f).openStream()){
                Files.copy(in, Paths.get(f.toString()));
                System.out.println("downloaded image with id "+f.getName());
            }
        }
        return null;
    }
}
