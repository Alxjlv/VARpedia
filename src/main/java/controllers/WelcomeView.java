package controllers;

import events.SwitchSceneEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

public class WelcomeView extends Controller {

    // TODO - Make welcome message contextual. E.g. if Creations exist suggest clicking on to play

    @FXML
    GridPane WelcomeView;

    @FXML
    public void pressCreate() {
        listener.handle(new SwitchSceneEvent(this, "/CreateView.fxml"));
    }
}
