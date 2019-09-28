package controllers;

import events.SwitchSceneEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import models.ChunkManager;
import models.CreationBuilder;
import models.CreationManager;

public class NameView extends Controller {

    @FXML TextField nameField;
    @FXML TextField imageField;

    @FXML public void pressCreate() {
        CreationBuilder builder = CreationManager.getInstance().getBuilder();

        builder.setName(nameField.getText()); // TODO - Check name is unique
        builder.setSearchTerm("Test Term"); // TODO - Get search term
        builder.setNumberOfImages(Integer.parseInt(imageField.getText())); // TODO - validate images number
        builder.setChunks(ChunkManager.getInstance().getItems());
        // TODO builder.setImages()

        CreationManager.getInstance().create(builder);

        listener.handle(new SwitchSceneEvent(this,"/WelcomeView.fxml"));
    }

    @FXML public void pressBack() {
        // TODO - Save state
        listener.handle(new SwitchSceneEvent(this,"/SnippetView.fxml"));
    }

    @FXML public void pressCancel() {
        //TODO - Warning popup
        listener.handle(new SwitchSceneEvent(this,"/WelcomeView.fxml"));
    }

}
