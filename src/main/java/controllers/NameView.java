package controllers;

import constants.Music;
import constants.View;
import events.CreationProcessEvent;
import events.SwitchSceneEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import models.creation.Creation;
import models.creation.CreationFileBuilder;
import models.creation.CreationFileManager;
import models.creation.CreationProcessManager;

import java.io.File;

public class NameView extends Controller {

    @FXML private TextField nameField;
    @FXML private Text errorText;
    @FXML private ChoiceBox<Music> musicDropdown;
    @FXML private Button submitButton;
    @FXML private ProgressBar progressBar;
    @FXML private Text progressMessage;
    @FXML private ToggleButton previewButton;

    MediaPlayer mediaPlayer;

    @FXML
    public void initialize() {
        CreationProcessManager creationProcessManager = CreationProcessManager.getInstance();

        nameField.textProperty().bindBidirectional(creationProcessManager.nameProperty());
        nameField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                submitButton.setDisable(false);
                errorText.setText("");
                if (newValue.isEmpty()) {
                    submitButton.setDisable(true);
                } else {
                    if (creationProcessManager.getMode() != CreationProcessManager.Mode.EDIT) {
                        for (Creation creation : CreationFileManager.getInstance().getItems()) {
                            if (creation.getName().equals(newValue)) {
                                errorText.setText("A creation already exists with that Name. Please select another");
                                submitButton.setDisable(true);
                                return;
                            }
                        }
                    }
                }
            }
        });
        if (nameField.getText() == null || nameField.getText().isEmpty()) {
            submitButton.setDisable(true);
        }

        ObservableList<Music> musicList = FXCollections.observableArrayList();
        for(Music m:Music.values()){
           musicList.add(m);
        }
        musicDropdown.setItems(musicList);
        musicDropdown.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            creationProcessManager.setBackgroundMusic(newValue);
            if (newValue == Music.TRACK_NONE) {
                previewButton.setDisable(true);
            } else {
                previewButton.setDisable(false);
            }
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        });
        if (creationProcessManager.getBackgroundMusic() == null) {
            musicDropdown.getSelectionModel().select(Music.TRACK_NONE);
        } else {
            musicDropdown.getSelectionModel().select(creationProcessManager.getBackgroundMusic());
        }

        progressMessage.textProperty().bind(CreationProcessManager.getInstance().progressMessageProperty());

        if (creationProcessManager.getMode() == CreationProcessManager.Mode.EDIT) {
            submitButton.setText("Save");
        }
    }

    @FXML public void pressSubmit() {
        errorText.setText("");

        CreationProcessManager creationProcessManager = CreationProcessManager.getInstance();

        progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        progressMessage.setVisible(true);
        progressBar.setVisible(true);

        creationProcessManager.progressStateProperty().addListener(new ChangeListener<CreationFileBuilder.ProgressState>() {
            @Override
            public void changed(ObservableValue<? extends CreationFileBuilder.ProgressState> observable, CreationFileBuilder.ProgressState oldValue, CreationFileBuilder.ProgressState newValue) {
                if (newValue.equals(CreationFileBuilder.ProgressState.SUCCEEDED)) {
                    listener.handle(new CreationProcessEvent(this, CreationProcessEvent.Status.SAVE));
                } else if (newValue.equals(CreationFileBuilder.ProgressState.FAILED)) {
                    progressBar.setVisible(false);
                    progressMessage.setVisible(false);
                    Alert alert = new Alert(Alert.AlertType.ERROR,"Something went wrong, your creation did not create.",ButtonType.OK);
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); // Credit to Di Kun Ong (dngo711) for this line
                    alert.showAndWait();
                    creationProcessManager.progressStateProperty().removeListener(this);
                }
            }
        });
        creationProcessManager.build();

    }

    @FXML public void pressBack() {
        listener.handle(new SwitchSceneEvent(this, View.IMAGE_PREVIEW.get()));
    }

    @FXML public void pressCancel() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                String.format("If you cancel your Snippets will not be saved. Do you wish to continue?"),
                ButtonType.YES, ButtonType.CANCEL);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); // Credit to Di Kun Ong (dngo711) for this line
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            listener.handle(new CreationProcessEvent(this, CreationProcessEvent.Status.CANCEL_CREATE));
        }
    }

    @FXML public void pressPreview() {
        System.out.println("Preview audio");
        if (previewButton.isSelected()) {
            File audioFile = musicDropdown.getSelectionModel().getSelectedItem().getMusicFile();

            Media media = new Media(audioFile.toURI().toString());
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setAutoPlay(true);
            mediaPlayer.setOnEndOfMedia(() -> previewButton.setSelected(false));
            mediaPlayer.setOnStopped(() -> previewButton.setSelected(false));
        } else {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        }
    }
}
