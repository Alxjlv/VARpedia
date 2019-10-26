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
import javafx.scene.text.Text;
import models.*;
import models.creation.Creation;
import models.creation.CreationFileBuilder;
import models.creation.CreationFileManager;

public class NameView extends Controller {

    @FXML TextField nameField;
    @FXML Text errorText;
    @FXML ChoiceBox<Music> musicDropdown;
    @FXML Button submitButton;
    @FXML ProgressBar progressBar;
    @FXML Text progressMessage;

    @FXML
    public void initialize() {
        FormManager formManager = FormManager.getInstance();

        nameField.textProperty().bindBidirectional(formManager.nameProperty());

        ObservableList<Music> musicList = FXCollections.observableArrayList();
        for(Music m:Music.values()){
           musicList.add(m);
        }
        musicDropdown.setItems(musicList);
        if (formManager.getBackgroundMusic() == null) {
            musicDropdown.getSelectionModel().select(Music.TRACK_NONE);
        } else {
            musicDropdown.getSelectionModel().select(formManager.getBackgroundMusic());
        }
        musicDropdown.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            formManager.setBackgroundMusic(newValue);
        });

        if (formManager.getMode() == FormManager.Mode.EDIT) {
            submitButton.setText("Save");
        }
    }

    @FXML public void pressSubmit() {
        errorText.setText("");

        FormManager formManager = FormManager.getInstance();


        // Validate name is unique
        if (formManager.getMode() != FormManager.Mode.EDIT) {
            for (Creation creation : CreationFileManager.getInstance().getItems()) {
                if (creation.getName().equals(nameField.getText())) {
                    errorText.setText("A creation already exists with that Name. Please select another");
                    return;
                }
            }
        }

        progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        progressMessage.setVisible(true);
        progressBar.setVisible(true);
        progressMessage.textProperty().bind(FormManager.getInstance().progressMessageProperty());

        formManager.progressStateProperty().addListener(new ChangeListener<CreationFileBuilder.State>() {
            @Override
            public void changed(ObservableValue<? extends CreationFileBuilder.State> observable, CreationFileBuilder.State oldValue, CreationFileBuilder.State newValue) {
                if(newValue.equals(CreationFileBuilder.State.SUCCEEDED)) {
                    listener.handle(new CreationProcessEvent(this, CreationProcessEvent.Status.CREATE));
                } else if (newValue.equals(CreationFileBuilder.State.FAILED)) {
                    progressBar.setVisible(false);
                    progressMessage.setVisible(false);
                }
            }
        });
        formManager.build();

    }

    @FXML public void pressBack() {
        listener.handle(new SwitchSceneEvent(this, View.IMAGE_PREVIEW.get()));
    }

    @FXML public void pressCancel() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                String.format("If you cancel your Snippets will not be saved. Do you wish to continue?"),
                ButtonType.YES, ButtonType.CANCEL);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            listener.handle(new CreationProcessEvent(this, CreationProcessEvent.Status.CANCEL));
        }
    }

}
