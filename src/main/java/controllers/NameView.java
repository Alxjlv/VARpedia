package controllers;

import constants.Music;
import constants.View;
import events.CreationProcessEvent;
import events.SwitchSceneEvent;
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
    @FXML TextField imageField;
    @FXML Text errorText;
    @FXML ChoiceBox<Music> musicDropdown;
    @FXML Button submitButton;

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

//        nameField.setOnKeyPressed(new EventHandler<KeyEvent>() {
//            @Override
//            public void handle(KeyEvent event) {
//                if (event.getCode().equals(KeyCode.ENTER)) {
//                    imageField.requestFocus();
//                }
//            }
//        });
//        imageField.setOnKeyPressed(new EventHandler<KeyEvent>() {
//            @Override
//            public void handle(KeyEvent event) {
//                if (event.getCode().equals(KeyCode.ENTER)) {
//                    pressSubmit();
//                }
//            }
//        });

        if (formManager.getState() == FormManager.State.EDIT) {
            submitButton.setText("Save");
        }
    }

    @FXML public void pressSubmit() {
        errorText.setText("");

        FormManager formManager = FormManager.getInstance();
        CreationFileBuilder builder = CreationFileManager.getInstance().getBuilder();

        // Validate name is unique
        if (formManager.getState() != FormManager.State.EDIT) {
            for (Creation creation : CreationFileManager.getInstance().getItems()) {
                if (creation.getName().equals(nameField.getText())) {
                    errorText.setText("A creation already exists with that Name. Please select another");
                    return;
                }
            }
        }
        // Validate number of images is a integer TODO - Remove
        int imageNumber = 0;
        try {
            imageNumber = Integer.parseInt(imageField.getText());
            if (!(imageNumber >= 1 && imageNumber <= 10)) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            errorText.setText("Error, please enter a number between 1 and 10");
            return;
        }

        builder.setName(formManager.getName());
        builder.setSearchTerm(formManager.getSearchTerm());
        builder.setSearchText(formManager.getSearchText());
        builder.setImages(formManager.getImages());
        builder.setThumbnail(formManager.getThumbnail());
        builder.setBackgroundMusic(formManager.getBackgroundMusic());
        builder.setEdit(formManager.getState() == FormManager.State.EDIT);
        builder.setProgressPopupOwner(nameField.getScene().getWindow());

        builder.setNumberOfImages(imageNumber); // TODO - Remove

        CreationFileManager.getInstance().create(builder);
        listener.handle(new CreationProcessEvent(this, CreationProcessEvent.Status.CREATE));
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
