package models.images;

import constants.Folder;
import main.ProcessRunner;
import models.AsynchronousFileBuilder;
import models.FileManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageFileBuilder implements AsynchronousFileBuilder<URL> {
    private URL image;

    private static Pattern urlPattern = Pattern.compile(".*/(.*)$");

    /**
     * Set the Image URL that will have a File built
     * @param image
     * @return
     */
    public ImageFileBuilder setImage(URL image) {
        this.image = image;
        return this;
    }

    @Override
    public void build(FileManager<URL> caller) {
        if (ImageFileManager.getInstance().getItems().contains(image)) {
            return;
        }

        Matcher matcher = urlPattern.matcher(image.getFile());
        File imageFile = null;
        if (matcher.find()) {
            imageFile = new File(Folder.IMAGES.get(), matcher.group(1));
        }

        try (InputStream in = image.openStream()) {
            if (!imageFile.exists()) {
                Files.copy(in, Paths.get(imageFile.getPath()));
            }
            if (imageFile.exists()) {
                crop(imageFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        caller.save(image, imageFile);
    }

    private void crop(File image) {
        String command = String.format(
                "ffmpeg -i %s -filter \"scale=1280:720:force_original_aspect_ratio=increase,crop=1280:720\" %s -y",
                image.toString(),image.toString());
        ProcessRunner crop = new ProcessRunner(command);
        Executors.newSingleThreadExecutor().submit(crop);
    }
}
