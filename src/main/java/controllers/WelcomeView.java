package controllers;

import events.CreationProcessEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.io.IOException;

public class WelcomeView extends Controller {

    // TODO - Make welcome message contextual. E.g. if Creations exist suggest clicking on to play

    @FXML
    GridPane WelcomeView;

    @FXML
    public void pressCreate() {
        listener.handle(new CreationProcessEvent(this, CreationProcessEvent.Status.BEGIN_CREATE));
    }

    @FXML public void pressHelp() {
        ProcessBuilder processBuilder = new ProcessBuilder("xdg-open", new File(".bin/User-Manual.pdf").getPath());
        try {
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
