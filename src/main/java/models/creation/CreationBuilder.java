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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/**
 * Implements a {@link Builder} for {@link Creation} objects
 */
public class CreationBuilder implements Builder<Creation> {

    // User set fields
    private String name;
    private String searchTerm;
    private String searchText;
    private File creationFolder;
    private ChunkManager chunkManager;
    private List<URL> images;

    private int numberOfImages; // TODO - Remove
    private List<File> imageFiles; // TODO - Remove

    // Builder fields
    private double imageDuration;
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

    // TODO - Integrate this into FormManager
    public CreationBuilder setSearchText(String searchText) {
        this.searchText = searchText;
        return this;
    }

    public CreationBuilder setCreationFolder(File creationFolder) {
        this.creationFolder = creationFolder;
        return this;
    }

    // TODO - Integrate this into FormManager
    public CreationBuilder setChunkManager(ChunkManager chunkManager) {
        this.chunkManager = chunkManager;
        return this;
    }

    // TODO - Integrate this into FormManager
    public CreationBuilder setImages(List<URL> images) {
        this.images = images;
        return this;
    }



    // TODO - Remove
    public CreationBuilder setNumberOfImages(int numberOfImages) {
        this.numberOfImages = numberOfImages;
        return this;
    }



    @Override
    public Creation build() {
        // TODO - Temporary until setSearchText is implemented into FormManager
        setSearchText("");

        // TODO - Temporary until setChunkManager(ChunkManager) is implemented into FormManager
        setChunkManager(ChunkManager.getInstance());

        // TODO - Temporary until setImages(List<URL>) is implemented into FormManager
        imageFiles = new ArrayList<>();
        for (int i = 1; i <= numberOfImages; i++) {
            imageFiles.add(new File(Folder.TEMP_IMAGES.get(), String.format("%d.jpg", i)));
        }
        setImages(new ArrayList<>());


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
            for (File image: imageFiles) {
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
        String cmnd = String.format(
                "ffmpeg -f concat -i '%s' -vf \"scale=1280:720:force_original_aspect_ratio=increase,crop=1280:720\" " +
                        "-vsync vfr -pix_fmt yuv420p '%s' -v quiet",
                slideshowConfig.toString(), slideshowVideo.toString());
        System.out.println(cmnd);
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
        File videoFile = new File(creationFolder, Filename.VIDEO.get());

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

        converter.setOnSucceeded(event -> {
            // TODO - progress sending
            System.out.println("exit value of converter: " + converter.getExitVal());
            // Create creation object
            List<Chunk> chunks = new ArrayList<>(ChunkManager.getInstance().getItems());
            Creation creation = new Creation(name, searchTerm, searchText, videoFile, chunks, images);

            // Inform CreationManager of success
            System.out.println("Save?");
            CreationManager.getInstance().save(creation, creationFolder);
        });
    }
}