package models.images;

import javafx.concurrent.Task;

import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This class is responsible for coordinating images, and storing relevant data for a creation.
 */
public class ImageDownloader { // TODO - Implement into ImageFileManager?
    private ExecutorService threadPool = Executors.newFixedThreadPool(8);

    public void downloadImages(List<URL> images) {
        for (URL image : images) {
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() {
                    ImageFileBuilder builder = ImageFileManager.getInstance().getBuilder();
                    builder.setImage(image);
                    ImageFileManager.getInstance().create(builder);
                    return null;
                }
            };
            threadPool.submit(task);
        }
    }
}
