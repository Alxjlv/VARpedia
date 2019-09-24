package controllers;

import javafx.fxml.FXML;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import models.Creation;
import models.MediaSingleton;

public class VideoView extends Controller {

    @FXML MediaView mediaView;

    @FXML
    Text creationName;
    @FXML
    Text elapsedTime;
    @FXML
    Text totalTime;

    private Media media;
    private MediaPlayer mediaPlayer;

    @FXML
    public void initialize() {
        Creation creation = MediaSingleton.getInstance().getCreation();

        creationName.setText(creation.getName());

        media = new Media(creation.getVideoFile().toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);

        elapsedTime.setText(mediaPlayer.getCurrentTime().toString());
        totalTime.setText(media.getDuration().toString());
    }

    @FXML
    public void pressPlay() {
        mediaPlayer.play();
    }
}
