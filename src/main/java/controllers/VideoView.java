package controllers;

import javafx.fxml.FXML;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import models.MediaSingleton;

public class VideoView extends Controller {

    @FXML
    MediaView mediaView;

    private MediaPlayer mediaPlayer;

    @FXML
    public void initialize() {
        mediaPlayer = new MediaPlayer(MediaSingleton.getInstance().getMedia());
        mediaView.setMediaPlayer(mediaPlayer);
    }

    @FXML
    public void pressPlay() {
        mediaPlayer.play();
    }
}
