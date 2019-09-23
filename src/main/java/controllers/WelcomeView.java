package controllers;

import events.SwitchSceneEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

public class WelcomeView extends Controller{

    @FXML
    GridPane WelcomeView;

    @FXML
    public void pressCreate(){
        listener.handle(new SwitchSceneEvent(this, "/CreateView.fxml"));
    }
}
