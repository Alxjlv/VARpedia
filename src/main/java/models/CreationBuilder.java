package models;

import constants.Filename;
import constants.Folder;
import events.NewCreationEvent;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import main.ProcessRunner;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Implements a {@link Builder} for {@link Creation} objects
 */
public class CreationBuilder implements Builder<Creation> {
    private String name;
    private String searchTerm;
//    private File videoFile;
//    private List<Chunk> chunks;
    private int numberOfImages;
    private CreationManager listener;
    private ChunkManager chunkManager;

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

    public CreationBuilder setListener(CreationManager listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public Creation build() {
        // TODO - Validate fields

        // TODO - Validate creation path/folder

        ChunkManager.getInstance().combine(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                File combinedAudioFile = new File(Folder.TEMP.get(), Filename.COMBINED_AUDIO.get());

                Media combinedAudio = new Media(combinedAudioFile.toURI().toString());
                MediaPlayer load = new MediaPlayer(combinedAudio);
                load.setOnReady(new Runnable() {
                    @Override
                    public void run() {
                        Duration creationDuration = combinedAudio.getDuration();
                        double duration = creationDuration.toSeconds();
                        duration += 1;
                        double imageDuration = duration / numberOfImages;
                        System.out.println("Creation duration: " + duration);
                        System.out.println("Image duration: " + imageDuration);
//                        File combinedAudioFile = new File(Folder.TEMP.get(), Filename.COMBINED_AUDIO.get());
                        File slideshow = new File(Folder.TEMP.get(), "slideshow.txt");
                        try {
                            FileWriter writer = new FileWriter(slideshow);
                            String last = null;
                            for (int i = 1; i <= numberOfImages; i++) { //don't actually need File types for images, can just iterate through each image in the folder
                                last = String.format("file 'images/%d.jpg'\n", i);
                                writer.write(last);
                                writer.write(String.format("duration %f\n", imageDuration));
                            }
                            if (last != null) {
                                writer.write(last);
                            }
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace(); // TODO - Remove?
                        }
                        File slideshowVideo = new File(Folder.TEMP.get(), "slideshow.avi");
                        String slideshowCommand = "ffmpeg -f concat -i " + slideshow.toString() + " -vf scale=500:-2 -vsync vfr -pix_fmt yuv420p " + slideshowVideo.toString() + " -v quiet";
                        System.out.println("Slideshow command: " + slideshowCommand);
                        ProcessRunner slideshowMaker = new ProcessRunner(slideshowCommand);
                        Executors.newSingleThreadExecutor().submit(slideshowMaker);
                        slideshowMaker.setOnSucceeded(event1 -> {
                            //TODO - progress sending
                            System.out.println("exit value of slideshowMaker: " + slideshowMaker.getExitVal());

                            File combinedVideo = new File(Folder.TEMP.get(), "combined.avi");
                            String combineCommand = "ffmpeg -i " + combinedAudioFile.toString() + " -i " + slideshowVideo.toString() + " -c copy " + combinedVideo.toString() + " -v quiet";
                            System.out.println("Combine command: " + combineCommand);
                            ProcessRunner combiner = new ProcessRunner(combineCommand);
                            Executors.newSingleThreadExecutor().submit(combiner);
                            combiner.setOnSucceeded(event2 -> {
                                //TODO - progress sending
                                System.out.println("exit value of combiner: " + combiner.getExitVal());

                                File videoFile = new File(Folder.CREATIONS_VIDEO.get(), String.format("%s.mp4", name));
                                String drawtext = "\"drawtext=fontfile=Montserrat-Regular:fontsize=60:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text=\'" + searchTerm + "\'\"";
                                String convertCommand = "ffmpeg -i " + combinedVideo.getPath() + " -vf " + drawtext + " -c:v libx264 -crf 19 -preset slow -c:a libfdk_aac -b:a 192k -ac 2  -max_muxing_queue_size 4096 " + videoFile.toString() + " -v quiet";
                                System.out.println("Convert Command: " + convertCommand);
                                ProcessRunner converter = new ProcessRunner(convertCommand);
                                Executors.newSingleThreadExecutor().submit(converter);
                                converter.setOnSucceeded(event3 -> {
                                    //TODO - progress sending
                                    System.out.println("exit value of converter: " + converter.getExitVal());
                                });

                                System.out.println("Creation name:" + name);
                                System.out.println("Creation file: " + videoFile.toString());

                                List<Chunk> chunks = new ArrayList<>(ChunkManager.getInstance().getItems());

                                Creation creation = new Creation(name, videoFile, chunks);
                                listener.handle(new NewCreationEvent(this, creation));
                            });
                        });
                    }
                });
            }
        });
        return null;
    }
}