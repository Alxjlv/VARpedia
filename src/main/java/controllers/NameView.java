package controllers;

import events.SwitchSceneEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import models.ChunkManager;
import models.CreationBuilder;
import models.CreationManager;
import models.SearchManager;

public class NameView extends Controller {

    @FXML TextField nameField;
    @FXML TextField imageField;
    @FXML Text errorText;

    @FXML public void pressCreate() {
        CreationBuilder builder = CreationManager.getInstance().getBuilder();

        builder.setName(nameField.getText()); // TODO - Check name is unique

        builder.setSearchTerm(SearchManager.getInstance().getSearchTerm());
        int imageNumber = Integer.parseInt(imageField.getText());
        if(imageNumber<1||imageNumber>10){
            errorText.setText("Error, please enter a number between 1 and 10");
        } else {
            builder.setNumberOfImages(imageNumber);
            builder.setChunks(ChunkManager.getInstance().getItems());
            // TODO builder.setImages()

            CreationManager.getInstance().create(builder);

            listener.handle(new SwitchSceneEvent(this,"/WelcomeView.fxml"));
        }
    }

    @FXML public void pressBack() {
        // TODO - Save state
        listener.handle(new SwitchSceneEvent(this, "/ChunkView.fxml"));
    }

    @FXML public void pressCancel() {
        //TODO - Warning popup
        listener.handle(new SwitchSceneEvent(this,"/WelcomeView.fxml"));
    }

}
