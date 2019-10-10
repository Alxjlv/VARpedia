package controllers;

import constants.View;
import events.CreationProcessEvent;
import events.SwitchSceneEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import models.*;
import models.creation.Creation;
import models.creation.CreationBuilder;
import models.creation.CreationManager;

public class NameView extends Controller {

    @FXML TextField nameField;
    @FXML TextField imageField;
    @FXML Text errorText;

    @FXML
    public void initialize() {
//        nameField.setOnKeyPressed(new EventHandler<KeyEvent>() {
//            @Override
//            public void handle(KeyEvent event) {
//                if (event.getCode().equals(KeyCode.ENTER)) {
//                    imageField.requestFocus();
//                }
//            }
//        });
        imageField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    pressCreate();
                }
            }
        });
    }

    @FXML public void pressCreate() {
        errorText.setText("");

        CreationBuilder builder = CreationManager.getInstance().getBuilder();

        for (Creation creation: CreationManager.getInstance().getItems()) {
            if (creation.getName().equals(nameField.getText())) {
                errorText.setText("A creation already exists with that Name. Please select another");
                return;
            }
        }
        builder.setName(nameField.getText()); // TODO - Check name is unique

        builder.setSearchTerm(SearchManager.getInstance().getSearchTerm());
        try {
            int imageNumber = Integer.parseInt(imageField.getText());
            if (imageNumber >= 1 && imageNumber <= 10) {
                builder.setNumberOfImages(imageNumber);
                // TODO - builder.setImages()

                CreationManager.getInstance().create(builder);

                listener.handle(new CreationProcessEvent(this, CreationProcessEvent.Status.CREATE));
            } else {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            errorText.setText("Error, please enter a number between 1 and 10");
        }
    }

    @FXML public void pressBack() {
        listener.handle(new SwitchSceneEvent(this, View.CHUNK.get()));
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
