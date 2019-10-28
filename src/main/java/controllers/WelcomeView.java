package controllers;

import events.CreationProcessEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.io.IOException;

/**
 * WelcomeView is responsible for providing an alternative, easy to reach create button, as well providing a location
 * for a help button, which opens the user manual pdf
 * @author Tait & Alex
 */
public class WelcomeView extends Controller {

    @FXML GridPane WelcomeView;

    /**
     * Begins the creation process
     */
    @FXML public void pressCreate() {
        listener.handle(new CreationProcessEvent(this, CreationProcessEvent.Status.BEGIN_CREATE));
    }

    /**
     * Opens the user manual pdf in another window
     */
    @FXML public void pressHelp() {
        ProcessBuilder processBuilder = new ProcessBuilder("xdg-open", new File(".bin/User-Manual.pdf").getPath());
        try {
            processBuilder.start();
        } catch (IOException ignored) {
        }
    }
}
