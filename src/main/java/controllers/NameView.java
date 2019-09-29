package controllers;

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
import models.ChunkManager;
import models.CreationBuilder;
import models.CreationManager;
import models.SearchManager;

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
        CreationBuilder builder = CreationManager.getInstance().getBuilder();

        builder.setName(nameField.getText()); // TODO - Check name is unique

        builder.setSearchTerm(SearchManager.getInstance().getSearchTerm());
        int imageNumber = Integer.parseInt(imageField.getText());
        if(imageNumber > 1 || imageNumber <= 10) {
            builder.setNumberOfImages(imageNumber);
            builder.setChunks(ChunkManager.getInstance().getItems());
            // TODO - builder.setImages()

            CreationManager.getInstance().create(builder);

            listener.handle(new CreationProcessEvent(this, CreationProcessEvent.Status.CREATE));
        } else {
            errorText.setText("Error, please enter a number between 1 and 10");
        }
    }

    @FXML public void pressBack() {
        listener.handle(new SwitchSceneEvent(this, "/ChunkView.fxml"));
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
