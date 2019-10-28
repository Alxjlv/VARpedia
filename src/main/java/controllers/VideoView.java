package controllers;

import constants.View;
import events.SwitchSceneEvent;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.util.Duration;
import models.creation.Creation;
import models.creation.CreationFileManager;

/**
 * The VideoView is responsible for playing the creation the ser has selected, alongside controls for the user to control
 * where they are in the video, play/pause & mute.
 * There is also the confidence rating feature which allows users to rate how confident they feel about a video
 * Code based on: https://docs.oracle.com/javafx/2/media/playercontrol.htm
 * @author Tait & Alex
 */
public class VideoView extends Controller {

    // The part of the scene the media is displayed in
    @FXML private MediaView mediaView;
    @FXML private VBox mediaBox;

    @FXML private Slider timeSlider; // Slider to control the time (allowing the user to scrub)
    @FXML private Slider confidenceSlider; // Confidence slider between 1-5

    // Text showing how far through the video the user is
    @FXML private Text elapsedTime;
    @FXML private Text totalTime;

    // Play/Pause and Mute controls
    @FXML private Button playButton;
    @FXML private ToggleButton muteButton;

    private MediaPlayer mediaPlayer; // Responsible for loading and playing the video

    private Duration duration; // Duration characteristic of the video

    /**
     * Initialize is responsible for setting up the {@link MediaView} & {@link MediaPlayer} to playback the creation,
     * while also binding properties to the {@link models.FormManager} singleton
     */
    @FXML public void initialize() {
        Creation creation = AdaptivePanel.getSelectedCreation();

        // Sets up the MediaPlayer and MediaView within the scene
        Media media = new Media(CreationFileManager.getInstance().getVideoFile(creation).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        AdaptivePanel.setSelectedCreationMediaPlayer(mediaPlayer);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaView.setMediaPlayer(mediaPlayer);

        // Binds the height of the MediaView to it's parent so it can resize up
        mediaView.fitHeightProperty().bind(mediaBox.heightProperty());
        mediaView.fitWidthProperty().bind(mediaBox.widthProperty());

        // Finds the duration of the video once the media is ready
        mediaPlayer.setOnReady(() -> {
            duration = mediaPlayer.getMedia().getDuration();
            if (totalTime != null) {
                totalTime.setText(formatTime(duration));
            }
            updateValues();
        });

        // Updates button text depending on the state of the player
        mediaPlayer.setOnPlaying(() -> playButton.setText("Pause"));
        mediaPlayer.setOnPaused(() -> playButton.setText("Play"));
        mediaPlayer.setOnStopped(() -> playButton.setText("Play"));

        // Increases the viewcount & resets scene attributes when played again
        mediaPlayer.setOnRepeat(() -> {
            creation.incrementViewCount();
            elapsedTime.setText(formatTime(duration));
            timeSlider.adjustValue(100);
            mediaPlayer.pause();
        });

        // Listener to allow the slider to scrub as the user wants
        timeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (timeSlider.isPressed()) {
                mediaPlayer.seek(duration.multiply(timeSlider.getValue() / 100.0));
            }
        });

        // If the play button is pressed again, it will toggle the player between playing and pausing
        playButton.setOnAction(event -> {
            MediaPlayer.Status status = mediaPlayer.getStatus();
            if (status == MediaPlayer.Status.UNKNOWN || status == MediaPlayer.Status.HALTED) {
                return;
            }
            if (status == MediaPlayer.Status.PAUSED || status == MediaPlayer.Status.READY || status == MediaPlayer.Status.STOPPED) {
                mediaPlayer.play();
            } else {
                mediaPlayer.pause();
            }
        });

        // Toggles the mute button
        muteButton.setOnAction(event -> mediaPlayer.setMute(!mediaPlayer.isMute()));
        muteButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.booleanValue()) {
                muteButton.setText("Muted");
            } else {
                muteButton.setText("Mute");
            }
        });

        // Setting the time text to a default before it's been loaded
        elapsedTime.setText("--:--");
        totalTime.setText("--:--");

        // Updating the current time of the video
        mediaPlayer.currentTimeProperty().addListener(observable -> updateValues());

        // Updating the confidence properties of the creation
        confidenceSlider.setValue(creation.getConfidenceRating());
        confidenceSlider.valueProperty().addListener((observable, oldValue, newValue) -> creation.setConfidenceRating(newValue.intValue()));

        // Disable the confidence rating until the creation ahs been fully viewed at least once
        if (AdaptivePanel.getSelectedCreation().getViewCount() == 0) {
            confidenceSlider.setDisable(true);
            AdaptivePanel.getSelectedCreation().viewCountProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.intValue() > 0) {
                    confidenceSlider.setDisable(false);
                }
            });
        }
    }

    /**
     * Returns to the welcome screen
     */
    @FXML public void pressClose() {
        listener.handle(new SwitchSceneEvent(this, View.WELCOME.get()));
    }

    /**
     * Calculates the time to the end of the video & updates the elapsed value and the slide to reflect that
     */
    private void updateValues() {
        if (elapsedTime != null && timeSlider != null && muteButton != null) {
            Platform.runLater(() -> {
                Duration currentTime = mediaPlayer.getCurrentTime();

                elapsedTime.setText(formatTime(currentTime));

                timeSlider.setDisable(duration.isUnknown());

                if (!timeSlider.isDisabled()
                        && duration.greaterThan(Duration.ZERO)
                        && !timeSlider.isValueChanging()) {
                    timeSlider.adjustValue(currentTime.divide(duration.toMillis()).toMillis() * 100);
                }
            });
        }
    }

    /**
     * Converts a duration object to a formatted string which can be displayed and is human readable
     * @param time - a {@link Duration} which needs to be converted to a formatted string-
     * @return a formatted time string
     */
    private static String formatTime(Duration time) {
        int intTime = (int) Math.floor(time.toSeconds());
        int timeHours = intTime / (60 * 60);
        if (timeHours > 0) {
            intTime -= timeHours * 60 * 60;
        }
        int timeMinutes = intTime / 60;
        int timeSeconds = intTime - timeHours * 60 * 60 - timeMinutes * 60;

        if (timeHours > 0) {
            return String.format("%d:%02d:%02d", timeHours, timeMinutes, timeSeconds);
        } else {
            return String.format("%02d:%02d", timeMinutes, timeSeconds);
        }
    }
}
