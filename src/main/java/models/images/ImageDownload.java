package models.images;

import javafx.concurrent.Task;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class ImageDownload extends Task<Void> {
    private URL url;
    private File image;

    ImageDownload(URL u, File f){
        url = u;
        image = f;
    }

    @Override
    protected Void call() throws Exception {
        System.out.println("Starting download");
        try(InputStream in = url.openStream()){
            Files.copy(in, Paths.get(image.toString()));
            System.out.println("downloaded image with id "+image.getName());
        }
        return null;
    }
}
