package models.creation;

import constants.Filename;
import constants.Folder;
import constants.Music;
import controllers.AdaptivePanel;
import javafx.beans.property.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import main.ProcessRunner;
import models.CallbackFileBuilder;
import models.FileManager;
import models.chunk.Chunk;
import models.chunk.ChunkFileManager;
import models.images.ImageFileManager;
import org.apache.commons.text.WordUtils;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Implements a {@link CallbackFileBuilder} for {@link Creation} objects. {@code build()} creates a video file
 * and thumbnail image for the users creation
 * @author Tait & Alex
 */
public class CreationFileBuilder implements CallbackFileBuilder<Creation> {
    /**
     * The progress states CreationFileBuilder may have after calling {@code build()}
     */
    public enum ProgressState {
        BUILDING,
        SUCCEEDED,
        FAILED;
    }

    /* Fields set before build() is called */
    private String name;
    private String searchTerm;
    private String searchText;
    private List<URL> images;
    private Music backgroundMusic;
    private File creationFolder;
    private boolean edit;

    /* Fields set by CreationFileBuilder */
    private File combinedAudio = new File(Folder.TEMP.get(), Filename.COMBINED_AUDIO.get());
    private File backgroundAudio = new File(Folder.TEMP.get(), "background.mp3");
    private File audio = new File(Folder.TEMP.get(), "audio.wav");
    private File slideshowConfig = new File(Folder.TEMP.get(), "slideshow_config.txt");
    private File slideshowVideo = new File(Folder.TEMP.get(), "slideshow.avi");
    private File combinedVideo = new File(Folder.TEMP.get(), "combined.avi");
    private File videoFile = null;
    private File thumbnailFile = null;
    private ReadOnlyObjectWrapper<ProgressState> state = new ReadOnlyObjectWrapper<>();
    private ReadOnlyStringWrapper progressMessage = new ReadOnlyStringWrapper();
    private double backgroundMusicVolume = 0.3;
    private double imageDuration;

    /**
     * Package-private constructor called by {@link CreationFileManager}
     */
    CreationFileBuilder() {}

    /**
     * Set the name of the creation to be built
     * @param name The name of the creation to be built
     * @return {@code this}
     */
    public CreationFileBuilder setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Set the search term of the creation to be built
     * @param searchTerm The search term of the creation to be built
     * @return {@code this}
     */
    public CreationFileBuilder setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
        return this;
    }

    /**
     * Set the search text of the creation to be built
     * @param searchText The search text of the creation to be built
     * @return {@code this}
     */
    public CreationFileBuilder setSearchText(String searchText) {
        this.searchText = searchText;
        return this;
    }

    /**
     * Set the images of the creation to be built
     * @param images The images of the creation to be built
     * @return {@code this}
     */
    public CreationFileBuilder setImages(List<URL> images) {
        this.images = new ArrayList<>(images);
        return this;
    }

    /**
     * Set the background music of the creation to be built
     * @param music The background music of the creation to be built
     * @return {@code this}
     */
    public CreationFileBuilder setBackgroundMusic(Music music){
        this.backgroundMusic = music;
        return this;
    }

    /**
     * Set the folder where the creation should be built. Package-private, called by {@link CreationFileManager}.
     * @param creationFolder The folder where the creation should be built
     * @return {@code this}
     */
    CreationFileBuilder setCreationFolder(File creationFolder) {
        this.creationFolder = creationFolder;
        videoFile = new File(creationFolder, Filename.VIDEO.get());
        thumbnailFile = new File(creationFolder, Filename.THUMBNAIL.get());
        return this;
    }

    /**
     * Set whether the Builder is creating an edited creation
     * @param edit True if the Builder is creating an edited creation
     * @return {@code this}
     */
    public CreationFileBuilder setEdit(boolean edit) {
        this.edit = edit;
        return this;
    }

    @Override
    public void build(FileManager<Creation> caller) {
        setState(ProgressState.BUILDING);

        combineAudio();
    }

    public void combineAudio() {
        setProgressMessage("Combining Snippets...");

        ChunkFileManager chunkFileManager = ChunkFileManager.getInstance();
        List<Chunk> chunks = chunkFileManager.getItems();

        File combinedAudio = new File(Folder.TEMP.get(), Filename.COMBINED_AUDIO.get());

        if (chunks.size() == 1) {
            chunkFileManager.getFile(chunks.get(0)).renameTo(combinedAudio);
            calculateImageDuration();
        } else {
            String command;

            List<String> commandList = new ArrayList<>();
            commandList.add("sox");
            for (Chunk chunk : chunks) {
                commandList.add(String.format("'%s'", chunkFileManager.getFile(chunk)));
            }
            commandList.add(combinedAudio.getPath());
            command = String.join(" ", commandList);

            ProcessRunner processRunner = new ProcessRunner(command);
            processRunner.setOnSucceeded(event -> calculateImageDuration());
            processRunner.setOnFailed(event -> setState(ProgressState.FAILED));

            Executors.newSingleThreadExecutor().submit(processRunner);
        }
    }

    private void calculateImageDuration() {
        setProgressMessage("Calculating duration...");

        Media media = new Media(combinedAudio.toURI().toString());

        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnReady(() -> {
            imageDuration = (media.getDuration().toSeconds() +0.1) / images.size();
            createSlideshow();
        });
    }

    private void createSlideshow() {
        setProgressMessage("Creating video...");

        /* Write FFmpeg slidehow configuration file */
        try {
            FileWriter writer = new FileWriter(slideshowConfig);

            String previous = null;
            ImageFileManager imageFileManager = ImageFileManager.getInstance();
            for (URL image: images) {
                File imageFile = imageFileManager.getFile(image);
                previous = String.format("file '%s'\n", imageFile.getAbsolutePath());
                writer.write(previous);
                writer.write(String.format("duration %f\n", imageDuration));
            }
            if (previous != null) {
                writer.write(previous);
            }

            writer.close();
        } catch (IOException e) {
            setState(ProgressState.FAILED);
            return;
        }

        String command = String.format(
                "ffmpeg -f concat -safe 0 -i '%s' -vsync vfr -pix_fmt yuv420p '%s' -v quiet",
                slideshowConfig.toString(), slideshowVideo.toString());

        ProcessRunner slideshowMaker = new ProcessRunner(command);
        slideshowMaker.setOnSucceeded(event -> createThumbnail());
        slideshowMaker.setOnFailed(event -> setState(ProgressState.FAILED));

        Executors.newSingleThreadExecutor().submit(slideshowMaker);
    }

    private void createThumbnail() {
        setProgressMessage("Creating thumbnail...");

        String command = String.format(
                "ffmpeg -i %s -vframes 1 -filter \"scale=80:60:force_original_aspect_ratio=increase,crop=80:60\" %s",
                slideshowVideo.toString(), thumbnailFile.toString());

        ProcessRunner processRunner = new ProcessRunner(command);
        processRunner.setOnSucceeded(event -> setBackgroundMusicVolume());
        processRunner.setOnFailed(event -> setState(ProgressState.FAILED));

        Executors.newSingleThreadExecutor().submit(processRunner);
    }

    private void setBackgroundMusicVolume() {
        setProgressMessage("Adding background music...");

        if (backgroundMusic != null && backgroundMusic != Music.TRACK_NONE) {
            String command = String.format("ffmpeg -i %s -filter:a \"volume=%s\" %s",
                    backgroundMusic.getMusicFile().toString(), backgroundMusicVolume, backgroundAudio.toString());

            ProcessRunner processRunner = new ProcessRunner(command);
            processRunner.setOnSucceeded(event -> addBackgroundMusic());
            processRunner.setOnFailed(event -> setState(ProgressState.FAILED));

            Executors.newSingleThreadExecutor().submit(processRunner);
        } else {
            addBackgroundMusic();
        }
    }

    private void addBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic != Music.TRACK_NONE) {
            String command = String.format("ffmpeg -i %s -i %s -filter_complex amix=inputs=2:duration=shortest %s",
                    combinedAudio.toString(), backgroundAudio.toString(), audio.toString());

            ProcessRunner processRunner = new ProcessRunner(command);
            processRunner.setOnSucceeded(event -> combineVideo());
            processRunner.setOnFailed(event -> setState(ProgressState.FAILED));

            Executors.newSingleThreadExecutor().submit(processRunner);
        } else {
            combinedAudio.renameTo(audio);
            combineVideo();
        }
    }

    private void combineVideo() {
        setProgressMessage("Adding audio...");

        String command = String.format("ffmpeg -i '%s' -i '%s' -c copy '%s' -v quiet",
                audio.toString(), slideshowVideo.toString(), combinedVideo.toString());

        ProcessRunner processRunner = new ProcessRunner(command);
        processRunner.setOnSucceeded(event -> convertVideo());
        processRunner.setOnFailed(event -> setState(ProgressState.FAILED));

        Executors.newSingleThreadExecutor().submit(processRunner);
    }

    private void convertVideo() {
        setProgressMessage("Saving creation...");

        String drawtextSubcommand = String.format(
                "\"drawtext=fontfile=.bin/Montserrat-Regular.ttf:fontsize=120:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:" +
                        "borderw=3:bordercolor=0x333333@0x33:text=%s\"", WordUtils.capitalizeFully(searchTerm));
        String command = String.format("ffmpeg -i %s -vf %s -c:v libx264 -crf 19 -preset slow -c:a libfdk_aac " +
                        "-b:a 192k -ac 2  -max_muxing_queue_size 4096 %s -v quiet",
                combinedVideo.getPath(), drawtextSubcommand, videoFile.toString());

        ProcessRunner processRunner = new ProcessRunner(command);

        processRunner.setOnSucceeded(event -> {
            List<Chunk> chunks = new ArrayList<>(ChunkFileManager.getInstance().getItems());
            Creation creation = new Creation(name, searchTerm, searchText, chunks, images, backgroundMusic);

            if (edit) {
                CreationFileManager.getInstance().edit(creation, creationFolder, AdaptivePanel.getSelectedCreation());
            } else {
                CreationFileManager.getInstance().save(creation, creationFolder);
            }

            setState(ProgressState.SUCCEEDED);
        });
//        processRunner.setOnFailed(event -> setState(ProgressState.FAILED));
        processRunner.setOnFailed(event -> {
            setState(ProgressState.FAILED);
            processRunner.getException().printStackTrace();
        });

        Executors.newSingleThreadExecutor().submit(processRunner);
    }

    /* JavaFX Beans (getters, setters, property) */

    public ProgressState getState() {
        return state.get();
    }
    private void setState(ProgressState progressState) {
        this.state.set(progressState);
    }
    public ReadOnlyObjectProperty<ProgressState> stateProperty() {
        return state.getReadOnlyProperty();
    }

    public String getProgressMessage() {
        return progressMessage.get();
    }
    private void setProgressMessage(String progressMessage) {
        this.progressMessage.set(progressMessage);
    }
    public ReadOnlyStringProperty progressMessageProperty() {
        return progressMessage.getReadOnlyProperty();
    }
}