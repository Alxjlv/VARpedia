package models.images;

import javafx.concurrent.Task;
import main.ProcessRunner;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.Executors;

/**
 * This is a Task class for image downloading which downloads a single image on a thread
 */
public class ImageDownload extends Task<Void> {
    private URL url;
    private File image;

    ImageDownload(URL u, File f){
        url = u;
        image = f;
    }

    @Override
    protected Void call() throws Exception {
//        System.out.println("Starting download");
        try(InputStream in = url.openStream()){
            Files.copy(in, Paths.get(image.toString()));
            String command = String.format(
                    "ffmpeg -i %s -filter \"scale=1280:720:force_original_aspect_ratio=increase,crop=1280:720\" %s",
                    image.toString(),image.toString());
            ProcessRunner crop = new ProcessRunner(command);
            Executors.newSingleThreadExecutor().submit(crop);

//            System.out.println("downloaded image with id "+image.getName());
        }
        return null;
    }
}
