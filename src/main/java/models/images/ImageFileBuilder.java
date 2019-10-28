package models.images;

import constants.Folder;
import main.ProcessRunner;
import models.CallbackFileBuilder;
import models.FileManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ImageFileBuilder implements {@link CallbackFileBuilder} for {@link ImageFileManager}. {@code build()} downloads
 * the given image, crops it, and saves it to the filesystem
 * @author Tait & Alex
 */
public class ImageFileBuilder implements CallbackFileBuilder<URL> {
    /**
     * The image URL to build a File for
     */
    private URL image;

    /**
     * Pattern to match the filename of a URL
     */
    private static Pattern urlPattern = Pattern.compile(".*/(.*)$");

    /**
     * Package-private constructor called by {@link ImageFileManager}
     */
    ImageFileBuilder() {}

    /**
     * Set the Image URL that will have a File built
     * @param image The image URL to build a File for
     * @return The builder
     */
    public ImageFileBuilder setImage(URL image) {
        this.image = image;
        return this;
    }

    /* Downloads an image and crops it */
    @Override
    public void build(FileManager<URL> caller) {
        if (ImageFileManager.getInstance().getItems().contains(image)) {
            return;
        }

        /* Get the filename from the URL */
        if (image == null) {
            return;
        }
        Matcher matcher = urlPattern.matcher(image.getFile());
        File imageFile = null;
        if (matcher.find()) {
            imageFile = new File(Folder.IMAGES.get(), matcher.group(1));
        }

        try (InputStream in = image.openStream()) {
            /* Download the image */
            if (!imageFile.exists()) {
                Files.copy(in, Paths.get(imageFile.getPath()));
            }

            /* Crop the image */
            String command = String.format(
                    "ffmpeg -i %s -filter \"scale=1280:720:force_original_aspect_ratio=increase,crop=1280:720\" %s -y",
                    imageFile.getPath(), imageFile.getPath());
            ProcessRunner crop = new ProcessRunner(command);
            Executors.newSingleThreadExecutor().submit(crop);
        } catch (IOException e) {
            return;
        }

        /* Save image to ImageFileManager */
        caller.save(image, imageFile);
    }
}
