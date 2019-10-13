package models.creation;

import constants.Filename;
import constants.Folder;
import constants.Music;
import constants.View;
import controllers.ProgressPopup;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import main.ProcessRunner;
import models.Builder;
import models.FormManager;
import models.chunk.Chunk;
import models.chunk.ChunkManager;
import org.apache.commons.text.WordUtils;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private Map<URL,File> imagesMap;

    private File combinedAudio = new File(Folder.TEMP.get(), Filename.COMBINED_AUDIO.get());
    private File slideshowConfig = new File(Folder.TEMP.get(), "slideshow_config.txt");
    private File slideshowVideo = new File(Folder.TEMP.get(), "slideshow.avi");
    private File combinedVideo = new File(Folder.TEMP.get(), "combined.avi");
    private Music backgroundMusic = null;
    private File videoFile = null;
    private File thumbnailFile = null;

    private ProgressPopup progressPopup;
    private Window progressPopupOwner;

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
        videoFile = new File(creationFolder, Filename.VIDEO.get());
        thumbnailFile = new File(creationFolder, Filename.THUMBNAIL.get());
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

    public CreationBuilder setProgressPopupOwner(Window owner) {
        progressPopupOwner = owner;
        return this;
    }

    public CreationBuilder setBackgroundMusic(Music music){
        this.backgroundMusic = music;
        return this;
    }



    @Override
    public Creation build() {
        // TODO - Temporary until setSearchText is implemented into FormManager
        setSearchText("");

        // TODO - Temporary until setChunkManager(ChunkManager) is implemented into FormManager
        setChunkManager(ChunkManager.getInstance());

        // TODO - Temporary until setImages(List<URL>) is implemented into FormManager
        imageFiles = new ArrayList<>(); // TODO - Temporary until setImages(List<File/Images>) is implemented
//        for (int i = 1; i <= numberOfImages; i++) {
//            imageFiles.add(new File(Folder.TEMP_IMAGES.get(), String.format("%d.jpg", i)));
//
//        }

        imagesMap = new HashMap<>();
        Map<URL, File> allImages = FormManager.getInstance().getCurrentDownloader().getImageList();
        int iterator = 0;
        for (URL u : allImages.keySet()) {
            if (iterator < numberOfImages) {
                imagesMap.put(u, allImages.get(u));
                iterator++;
            } else {
                break;
            }
        }
        setImages(new ArrayList<>());


//        for (int i = 1; i <= numberOfImages; i++) {
//
//            images.add(new File(Folder.TEMP_IMAGES.get(), String.format("%d.jpg", i)));
//        }
        // TODO - Validate fields

        // TODO - Validate creation path/folder


        Stage popup = new Stage();
        popup.initOwner(progressPopupOwner);
        popup.initModality(Modality.WINDOW_MODAL);

        FXMLLoader loader = new FXMLLoader(View.PROGRESS_POPUP.get());
        try {
            Parent root = loader.load();
            progressPopup = loader.getController();

            Scene scene = new Scene(root);
            popup.setScene(scene);
            popup.setTitle("Creating Your Creation");
            popup.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Combine audio
        ChunkManager.getInstance().combine(event -> {
            calculateImageDuration();
        });


        return null; // TODO - Make Future<Creation>?
    }

    private void calculateImageDuration() {
        progressPopup.setMessage("Combining snippets...");

        Media media = new Media(combinedAudio.toURI().toString());

        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnReady(() -> {
            imageDuration = (media.getDuration().toSeconds() +1) / numberOfImages;

            createSlideshow();
        });
    }

    private void createSlideshow() {
        progressPopup.setMessage("Creating video...");

        // Write config file
        try {
            FileWriter writer = new FileWriter(slideshowConfig);

            String previous = null;
//            Pattern pattern = Pattern.compile("^"+Folder.IMAGES.get()+"/");
            for (URL u: imagesMap.keySet()) {
                File image = imagesMap.get(u);
//                previous = String.format("file '%s'\n", "../"+pattern.matcher(image.getPath()).replaceAll(""));
                previous = String.format("file '%s'\n", "../"+image.toString());
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
                "ffmpeg -f concat -safe 0 -i '%s' -vf \"scale=1280:720:force_original_aspect_ratio=increase,crop=1280:720\" " +
                        "-vsync vfr -pix_fmt yuv420p '%s' -v quiet",
                slideshowConfig.toString(), slideshowVideo.toString());
        ProcessRunner slideshowMaker = new ProcessRunner(cmnd);
        Executors.newSingleThreadExecutor().submit(slideshowMaker);
        slideshowMaker.setOnSucceeded(event -> createThumbnail());
        slideshowMaker.setOnFailed(event -> {
            slideshowMaker.getException().printStackTrace(); // TODO - Error popup
            progressPopup.close();
        });
    }

    private void createThumbnail() {
        progressPopup.setMessage("Creating thumbnail...");

        String command = String.format(
                "ffmpeg -i %s -vframes 1 -filter \"scale=80:60:force_original_aspect_ratio=increase,crop=80:60\" %s",
                slideshowVideo.toString(), thumbnailFile.toString());
        ProcessRunner thumbnailMaker = new ProcessRunner(command);
        Executors.newSingleThreadExecutor().submit(thumbnailMaker);

        thumbnailMaker.setOnSucceeded(event -> combineVideo());
        thumbnailMaker.setOnFailed(event -> {
            thumbnailMaker.getException().printStackTrace(); // TODO - Error popup
            progressPopup.close();
        });
    }

    private void combineVideo() {
        progressPopup.setMessage("Adding audio...");

        // Run command
        String combineCommand = String.format("ffmpeg -i '%s' -i '%s' -c copy '%s' -v quiet",
                combinedAudio.toString(), slideshowVideo.toString(), combinedVideo.toString());
        ProcessRunner combiner = new ProcessRunner(combineCommand);
        Executors.newSingleThreadExecutor().submit(combiner);
        combiner.setOnSucceeded(event -> convertVideo()); //TODO - progress sending
        combiner.setOnFailed(event -> {
            combiner.getException().printStackTrace(); // TODO - Error popup
            progressPopup.close();
        });
    }

    private void convertVideo() {
        progressPopup.setMessage("Saving creation...");

        // Run command
        String drawtext = String.format(
                "\"drawtext=fontfile=Montserrat-Regular:fontsize=60:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text=%s\"",
                WordUtils.capitalizeFully(searchTerm));
        String cmnd = String.format("ffmpeg -i %s -vf %s -c:v libx264 -crf 19 -preset slow -c:a libfdk_aac " +
                        "-b:a 192k -ac 2  -max_muxing_queue_size 4096 %s -v quiet",
                combinedVideo.getPath(), drawtext, videoFile.toString());
        System.out.println(cmnd);
        ProcessRunner converter = new ProcessRunner(cmnd);
        Executors.newSingleThreadExecutor().submit(converter);

        // TODO - progress sending
        converter.setOnSucceeded(event -> {
            // Create creation object
            List<Chunk> chunks = new ArrayList<>(ChunkManager.getInstance().getItems());
            Creation creation = new Creation(name, searchTerm, searchText, videoFile, thumbnailFile, chunks, images);

            // Inform CreationManager of success
            CreationManager.getInstance().save(creation, creationFolder);
            progressPopup.close();
        });
        converter.setOnFailed(event -> {
            converter.getException().printStackTrace(); // TODO - Error popup
            progressPopup.close();
        });
    }
}