package models.images;

import constants.Folder;
import models.AsynchronousFileBuilder;
import models.FileManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
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
            Files.copy(in, Paths.get(imageFile.getPath())); // TODO - Use OutputSteam?
            System.out.println("downloaded image to: "+imageFile.getPath()); // TODO - Remove
        } catch (IOException e) {
            e.printStackTrace();
        }

        caller.save(image, imageFile);
    }
}
