package controllers;

import events.SwitchSceneEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class NameView extends Controller {

    @FXML TextField nameField;
    @FXML TextField imageField;

    @FXML public void pressCreate() {
        // TODO - Initialise CreationBuilder and pass to CreationManager.create()
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
