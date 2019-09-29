package controllers;

import events.CreationProcessEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

public class WelcomeView extends Controller {

    // TODO - Make welcome message contextual. E.g. if Creations exist suggest clicking on to play

    @FXML
    GridPane WelcomeView;

    @FXML
    public void pressCreate() {
        listener.handle(new CreationProcessEvent(this, CreationProcessEvent.Status.BEGIN));
    }
}
