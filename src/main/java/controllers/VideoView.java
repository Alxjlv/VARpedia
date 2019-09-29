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
import models.Creation;
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
//    private boolean atEndOfMedia = false;
//    private boolean stopRequested = false;

    @FXML
    public void initialize() {
        Creation creation = MediaSingleton.getInstance().getCreation();

        media = new Media(creation.getVideoFile().toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);

        playButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Press play");
                MediaPlayer.Status status = mediaPlayer.getStatus();
                System.out.println(status);

                if (status == MediaPlayer.Status.UNKNOWN
                        || status == MediaPlayer.Status.HALTED) {
                    return;
                }

                if (status == MediaPlayer.Status.PAUSED
                        || status == MediaPlayer.Status.READY
                        || status == MediaPlayer.Status.STOPPED) {
//                    if (atEndOfMedia) {
//                        mediaPlayer.stop();
////                        mediaPlayer.seek(mediaPlayer.getStartTime());
//                        atEndOfMedia = false;
//                    }
                    mediaPlayer.play();
                } else {
                    mediaPlayer.pause();
                }
            }
        });

        muteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Press mute");
                mediaPlayer.setMute(!mediaPlayer.isMute());
            }
        });

        mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                System.out.println("Update time");
                updateValues();
            }
        });

        mediaPlayer.setOnPlaying(new Runnable() {
            @Override
            public void run() {
                System.out.println("Player playing");

                playButton.setText("||");

//                if (stopRequested) {
//                    mediaPlayer.pause();
//                    stopRequested = false;
//                } else {
//                    playButton.setText("||");
//                }
            }
        });

        mediaPlayer.setOnPaused(new Runnable() {
            @Override
            public void run() {
                System.out.println("Player paused");
                playButton.setText("|>");
            }
        });

        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                System.out.println("Player ready");

                duration = mediaPlayer.getMedia().getDuration();

                if (totalTime != null) {
                    totalTime.setText(formatTime(duration));
                    System.out.println(duration.toString()); // TODO - Remove
                }

                updateValues();
            }
        });

        mediaPlayer.setOnStopped(new Runnable() {
            @Override
            public void run() {
                System.out.println("Player stopped");
                playButton.setText("|>");
            }
        });

        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                System.out.println("End of media");
                System.out.println(mediaPlayer.getCurrentTime().toString());

                mediaPlayer.stop();

//                playButton.setText("|>");
//                stopRequested = true;
//                atEndOfMedia = true;
//                updateValues();
            }
        });

        timeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                System.out.println("Changing time slider");
                if (timeSlider.isValueChanging()) {
                    System.out.println("Seek to: "+duration.multiply(timeSlider.getValue() / 100.0).toString());
                    mediaPlayer.seek(duration.multiply(timeSlider.getValue() / 100.0));
                }
            }
        });
//        timeSlider.valueProperty().addListener(new ChangeListener<Number>() {
//            @Override
//            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                System.out.println("Time slider changed: "+observable+" Old value: "+oldValue+" New value: "+newValue);
//                System.out.println("Is changing:"+timeSlider.isValueChanging());
//            }
//        });

        creationName.setText(creation.getName());

        elapsedTime.setText(mediaPlayer.getCurrentTime().toString());
        totalTime.setText(media.getDuration().toString());
    }

    protected void updateValues() {
        if (elapsedTime != null && timeSlider != null && muteButton != null) {
            Platform.runLater(new Runnable() {
                public void run() {
                    Duration currentTime = mediaPlayer.getCurrentTime();

                    elapsedTime.setText(formatTime(currentTime));
                    System.out.println(currentTime.toString()); // TODO - Remove

                    timeSlider.setDisable(duration.isUnknown());
                    if (!timeSlider.isDisabled()
                            && duration.greaterThan(Duration.ZERO)
                            && !timeSlider.isValueChanging()) {
                        timeSlider.adjustValue(currentTime.divide(duration.toMillis()).toMillis() * 100);
                    }

                    // TODO - Make muteButton ToggleButton
                    // muteButton;4
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
