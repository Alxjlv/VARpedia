package controllers;

import events.SwitchSceneEvent;
import javafx.fxml.FXML;

public class SnippetView extends Controller{
    @FXML
    public void initialize(){
    }

    @FXML public void pressBack(){
        listener.handle(new SwitchSceneEvent(this, "/CreateView.fxml"));
    }

    @FXML public void pressSpeech(){
        //TODO - alert box for speech settings
    }

    @FXML public void pressPreview(){
        //TODO - add preview logic
    }

    @FXML public void pressSaveSnippet(){
        //TODO - logic for adding to list
    }

    @FXML public void pressPlayback(){
        //TODO - logic for combining and playing snips
    }

    @FXML public void pressPreviewSnippet(){
        //TODO - logic for previewing that snippet from there
    }

    @FXML public void pressDelete(){
        //TODO - delete logic
    }

    @FXML public void pressNext(){
        listener.handle(new SwitchSceneEvent(this,"/NameView.fxml"));
    }
}
