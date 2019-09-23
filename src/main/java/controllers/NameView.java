package controllers;

import events.SwitchSceneEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class NameView extends Controller {

    @FXML TextField nameField;
    @FXML TextField imageField;

    @FXML public void pressCreate(){
        listener.handle(new SwitchSceneEvent(this,"/WelcomeView.fxml"));
    }

    @FXML public void pressBack(){
        listener.handle(new SwitchSceneEvent(this,"/SnippetView.fxml"));
    }

    @FXML public void pressCancel(){
        //TODO - warning popup
        listener.handle(new SwitchSceneEvent(this,"/WelcomeView.fxml"));
    }

}
