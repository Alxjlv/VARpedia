package controllers;

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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.util.Duration;
import models.creation.Creation;
import models.MediaSingleton;

/**
 * Code based on: https://docs.oracle.com/javafx/2/media/playercontrol.htm
 */
public class VideoView extends Controller {

    @FXML
    private MediaView mediaView;

    @FXML
    private Button playButton;
    @FXML
    private ToggleButton muteButton;
    @FXML
    private Slider timeSlider;

    @FXML
    private Text creationName;
    @FXML
    private Text elapsedTime;
    @FXML
    private Text totalTime;

    private Media media;
    private MediaPlayer mediaPlayer;

    private Duration duration;

    @FXML
    public void initialize() {
        Creation creation = MediaSingleton.getInstance().getCreation();

        media = new Media(creation.getVideoFile().toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaView.setMediaPlayer(mediaPlayer);

        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                duration = mediaPlayer.getMedia().getDuration();
                if (totalTime != null) {
                    totalTime.setText(formatTime(duration));
                }

                updateValues();
            }
        });

        mediaPlayer.setOnPlaying(new Runnable() {
            @Override
            public void run() {
                playButton.setText("||");
            }
        });

        mediaPlayer.setOnPaused(new Runnable() {
            @Override
            public void run() {
                playButton.setText("|>");
            }
        });

        mediaPlayer.setOnStopped(new Runnable() {
            @Override
            public void run() {
                playButton.setText("|>");
            }
        });

        mediaPlayer.setOnRepeat(new Runnable() {
            @Override
            public void run() {
                elapsedTime.setText(formatTime(duration));
                timeSlider.adjustValue(100);

                mediaPlayer.pause();
            }
        });

        timeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (timeSlider.isPressed()) {
                    mediaPlayer.seek(duration.multiply(timeSlider.getValue() / 100.0));
                }
            }
        });

        playButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                MediaPlayer.Status status = mediaPlayer.getStatus();

                if (status == MediaPlayer.Status.UNKNOWN
                        || status == MediaPlayer.Status.HALTED) {
                    return;
                }

                if (status == MediaPlayer.Status.PAUSED
                        || status == MediaPlayer.Status.READY
                        || status == MediaPlayer.Status.STOPPED) {
                    mediaPlayer.play();
                } else {
                    mediaPlayer.pause();
                }
            }
        });

        muteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mediaPlayer.setMute(!mediaPlayer.isMute());
            }
        });

        creationName.setText(creation.getName());

        elapsedTime.setText("--:--");
        totalTime.setText("--:--");

        mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                updateValues();
            }
        });
    }

    protected void updateValues() {
        if (elapsedTime != null && timeSlider != null && muteButton != null) {
            Platform.runLater(new Runnable() {
                public void run() {
                    Duration currentTime = mediaPlayer.getCurrentTime();

                    elapsedTime.setText(formatTime(currentTime));

                    timeSlider.setDisable(duration.isUnknown());

                    if (!timeSlider.isDisabled()
                            && duration.greaterThan(Duration.ZERO)
                            && !timeSlider.isValueChanging()) {
                        timeSlider.adjustValue(currentTime.divide(duration.toMillis()).toMillis() * 100);
                    }
                }
            });
        }
    }

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
