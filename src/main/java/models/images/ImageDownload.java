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
        try(InputStream in = url.openStream()){
            System.out.println("downloading image with id "+image.getName());
            System.out.println(url.toString());
            Files.copy(in, Paths.get(image.toString()));
            while (!image.exists()){
                Thread.sleep(10);
                System.out.println("sleeping");
            }
            System.out.println("scaling image");
            String command = String.format(
                    "ffmpeg -i %s -filter \"scale=1280:720:force_original_aspect_ratio=increase,crop=1280:720\" %s -y",
                    image.toString(),image.toString());
            ProcessRunner crop = new ProcessRunner(command);
            Executors.newSingleThreadExecutor().submit(crop);
            crop.setOnSucceeded(event -> {
                if(crop.getExitValue()!=0){
                    System.out.println(command);
                    System.out.println("crop exit val = "+crop.getExitValue());
                }
            });
        }
        return null;
    }
}
