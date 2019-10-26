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

    //Currently this implementation is slower (~6s) than the bulk download - but this downloads an image per thread
    public void downloadImages(List<URL> images) {
        long startTime = System.currentTimeMillis();
        AtomicLong endTime = new AtomicLong();
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
            task.setOnSucceeded(event -> {
                endTime.set(System.currentTimeMillis());
                System.out.println("current time taken: "+(endTime.get() - startTime));
            });
            threadPool.submit(task);
        }
    }

    //Downloads the images all in one thread (~1.5s)
    public void bulkDownLoadImages(List<URL> images) {
        long startTime = System.currentTimeMillis();
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                for (URL image : images) {
                    ImageFileBuilder builder = ImageFileManager.getInstance().getBuilder();
                    builder.setImage(image);
                    ImageFileManager.getInstance().create(builder);
                }
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            System.out.println("time taken: " + (System.currentTimeMillis()-startTime));
        });
        threadPool.submit(task);
    }
}
