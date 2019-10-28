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
import models.FormManager;
import models.creation.Creation;
import models.creation.CreationFileBuilder;
import models.creation.CreationFileManager;

import java.io.File;
import java.util.Arrays;

/**
 * NameView is responsible for naming the creation, setting the background music, and starting off the creation process
 * @author Tait & Alex
 */
public class NameView extends Controller {

    @FXML private TextField nameField; // This is where the user will enter what they want to name the creation
    @FXML private Text errorText; // If they write a duplicate name the error text will let them know

    // These are responsible for displaying the progress of the creation process to the user
    @FXML private Text progressMessage;
    @FXML private ProgressBar progressBar;

    @FXML private ChoiceBox<Music> musicDropdown; // The dropdown for the different background music tracks the user can select

    @FXML private ToggleButton previewButton; // Allows previewing of the selected music track
    @FXML private Button submitButton; // Begins the creation process

    private MediaPlayer mediaPlayer; // Media player to play the music tracks

    /**
     * The initialize method is responsible for adding listeners to the properties of NameView, then populating the
     * dropdown with music
     */
    @FXML public void initialize() {
        FormManager formManager = FormManager.getInstance();

        // Binding the nameField to change the nameProperty in the CreationProcessManager singleton
        nameField.textProperty().bindBidirectional(formManager.nameProperty());

        // Adding a listener to check for duplicate names of creations and sending an error if it's the same & disabling the submit button
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            submitButton.setDisable(false);
            errorText.setText("");
            if (newValue == null || newValue.isEmpty()) {
                submitButton.setDisable(true);
            } else {
                if (formManager.getMode() != FormManager.Mode.EDIT) {
                    for (Creation creation : CreationFileManager.getInstance().getItems()) {
                        if (creation.getName().equals(newValue)) {
                            errorText.setText("A creation already exists with that Name. Please select another");
                            submitButton.setDisable(true);
                            return;
                        }
                    }
                }
            }
        });
        // Disallowing the user to save a creation if the name is empty
        if (nameField.getText() == null || nameField.getText().isEmpty()) {
            submitButton.setDisable(true);
        }

        // Populating the music dropdown with the selected music tracks
        ObservableList<Music> musicList = FXCollections.observableArrayList();
        musicList.addAll(Arrays.asList(Music.values()));
        musicDropdown.setItems(musicList);

        // Listening to see which music track the user has selected
        musicDropdown.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            formManager.setBackgroundMusic(newValue);
            if (newValue == Music.TRACK_NONE) {
                previewButton.setDisable(true);
            } else {
                previewButton.setDisable(false);
            }
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        });
        if (formManager.getBackgroundMusic() == null) {
            musicDropdown.getSelectionModel().select(Music.TRACK_NONE);
        } else {
            musicDropdown.getSelectionModel().select(formManager.getBackgroundMusic());
        }

        // Binding the progress message to the status of the creation process (managed in FormManager)
        progressMessage.textProperty().bind(FormManager.getInstance().progressMessageProperty());

        // Changing the button text if in edit mode
        if (formManager.getMode() == FormManager.Mode.EDIT) {
            submitButton.setText("Save");
        }
    }

    /**
     * This method is responsible for beginning the creation process and reporting the results of each step back to the user
     */
    @FXML public void pressSubmit() {
        errorText.setText("");

        FormManager formManager = FormManager.getInstance();

        // Setting up the progress bar to an indeterminate animation & to show
        progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        progressMessage.setVisible(true);
        progressBar.setVisible(true);

        // Listening to the state of the creation process, and throwing an alert if it fails
        formManager.progressStateProperty().addListener(new ChangeListener<CreationFileBuilder.State>() {
            @Override
            public void changed(ObservableValue<? extends CreationFileBuilder.State> observable, CreationFileBuilder.State oldValue, CreationFileBuilder.State newValue) {
                if (newValue.equals(CreationFileBuilder.State.SUCCEEDED)) {
                    listener.handle(new CreationProcessEvent(this, CreationProcessEvent.Status.SAVE));
                } else if (newValue.equals(CreationFileBuilder.State.FAILED)) {
                    progressBar.setVisible(false);
                    progressMessage.setVisible(false);
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Something went wrong, your creation did not create.", ButtonType.OK);
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); // Credit to Di Kun Ong (dngo711) for this line
                    alert.showAndWait();
                    formManager.progressStateProperty().removeListener(this); // Removing bound listener so multiple presses of submit don't trigger multiple popups
                }
            }
        });
        // Beginning the creation process
        formManager.build();

    }

    /**
     * Returning to the image preview screen
     */
    @FXML public void pressBack() {
        listener.handle(new SwitchSceneEvent(this, View.IMAGE_PREVIEW.get()));
    }

    /**
     * Asks the user if they're sure that they want to cancel by throwing a popup, and switching to the welcome view if
     * they confirm
     */
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

    /**
     * Responsible for previewing the music that the user has selected, and allows toggling of playback
     */
    @FXML public void pressPreview() {
        if (previewButton.isSelected()) {
            File audioFile = musicDropdown.getSelectionModel().getSelectedItem().getMusicFile();

            Media media = new Media(audioFile.toURI().toString()); // Loads the file into media
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setAutoPlay(true);

            // Re-selects the button so you can preview again
            mediaPlayer.setOnEndOfMedia(() -> previewButton.setSelected(false));
            mediaPlayer.setOnStopped(() -> previewButton.setSelected(false));
        } else {
            if (mediaPlayer != null) {
                mediaPlayer.stop(); // Cancels current playback
            }
        }
    }
}
