package models.creation;

import constants.Filename;
import constants.Folder;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import main.ProcessRunner;
import models.Builder;
import models.chunk.Chunk;
import models.chunk.ChunkManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/**
 * Implements a {@link Builder} for {@link Creation} objects
 */
public class CreationBuilder implements Builder<Creation> {
    private String name;
    private String searchTerm;
    private int numberOfImages;
    private File folder;


    private double imageDuration;
    private List<File> images;

    private File combinedAudio = new File(Folder.TEMP.get(), Filename.COMBINED_AUDIO.get());
    private File slideshowConfig = new File(Folder.TEMP.get(), "slideshow_config.txt");
    private File slideshowVideo = new File(Folder.TEMP.get(), "slideshow.avi");
    private File combinedVideo = new File(Folder.TEMP.get(), "combined.avi");

    /**
     * Set the name of the creation to be built
     *
     * @param name The name of the creation to be built
     * @return {@code this}
     */
    public CreationBuilder setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Set the search term of the creation to be built
     *
     * @param searchTerm The search term of the creation to be built
     * @return {@code this}
     */
    public CreationBuilder setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
        return this;
    }

    // TODO - Change to setImages(List<Image/File> images)
    public CreationBuilder setNumberOfImages(int numberOfImages) {
        this.numberOfImages = numberOfImages;
        return this;
    }

    public CreationBuilder setFolder(File folder) {
        this.folder = folder;
        return this;
    }

    @Override
    public Creation build() {
        // TODO - Temporary until setImages(List<File/Images>) is implemented
        images = new ArrayList<>();
        for (int i = 1; i <= numberOfImages; i++) {
            images.add(new File(Folder.TEMP_IMAGES.get(), String.format("%d.jpg", i)));
        }


        // TODO - Validate fields

        // TODO - Validate creation path/folder

        // Combine audio
        ChunkManager.getInstance().combine(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                calculateImageDuration();
            }
        });

        return null; // TODO - Make Future<Creation>?
    }

    private void calculateImageDuration() {
        Media media = new Media(combinedAudio.toURI().toString());

        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnReady(() -> {
            imageDuration = (media.getDuration().toSeconds() +1) / numberOfImages;

            createSlideshow();
        });
    }

    private void createSlideshow() {
        // Write config file
        try {
            FileWriter writer = new FileWriter(slideshowConfig);

            String previous = null;
            Pattern pattern = Pattern.compile("^"+Folder.TEMP.get()+"/");
            for (File image: images) {
                previous = String.format("file '%s'\n", pattern.matcher(image.getPath()).replaceAll(""));
                writer.write(previous);
                writer.write(String.format("duration %f\n", imageDuration));
            }
            if (previous != null) {
                writer.write(previous);
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace(); // TODO - Remove?
        }

        // Run command
        String cmnd = String.format("ffmpeg -f concat -i '%s' -vf scale=500:-2 -vsync vfr -pix_fmt yuv420p '%s' -v quiet",
                slideshowConfig.toString(), slideshowVideo.toString());
        ProcessRunner slideshowMaker = new ProcessRunner(cmnd);
        Executors.newSingleThreadExecutor().submit(slideshowMaker);
        slideshowMaker.setOnSucceeded(event -> combineVideo());
    }

    private void combineVideo() {
        // Run command
        String combineCommand = String.format("ffmpeg -i '%s' -i '%s' -c copy '%s' -v quiet",
                combinedAudio.toString(), slideshowVideo.toString(), combinedVideo.toString());
        ProcessRunner combiner = new ProcessRunner(combineCommand);
        Executors.newSingleThreadExecutor().submit(combiner);
        combiner.setOnSucceeded(event -> convertVideo()); //TODO - progress sending
    }

    private void convertVideo() {
        File videoFile = new File(folder, Filename.VIDEO.get());

        // Run command
        String drawtext = String.format(
                "\"drawtext=fontfile=Montserrat-Regular:fontsize=60:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text=%s\"",
                searchTerm);
        String cmnd = String.format("ffmpeg -i %s -vf %s -c:v libx264 -crf 19 -preset slow -c:a libfdk_aac " +
                        "-b:a 192k -ac 2  -max_muxing_queue_size 4096 %s -v quiet",
                combinedVideo.getPath(), drawtext, videoFile.toString());
        System.out.println(cmnd);
        ProcessRunner converter = new ProcessRunner(cmnd);
        Executors.newSingleThreadExecutor().submit(converter);

        // TODO - progress sending
        converter.setOnSucceeded(event -> System.out.println("exit value of converter: " + converter.getExitVal()));

        // Create creation object
        List<Chunk> chunks = new ArrayList<>(ChunkManager.getInstance().getItems());
        Creation creation = new Creation(name, videoFile, chunks);

        // Inform CreationManager of success
        CreationManager.getInstance().save(creation, folder);
    }
}