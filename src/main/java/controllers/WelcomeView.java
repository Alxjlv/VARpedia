package controllers;

import events.CreationProcessEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class WelcomeView extends Controller {

    // TODO - Make welcome message contextual. E.g. if Creations exist suggest clicking on to play

    @FXML
    GridPane WelcomeView;

    @FXML
    public void pressCreate() {
        listener.handle(new CreationProcessEvent(this, CreationProcessEvent.Status.BEGIN));
    }

    @FXML public void pressHelp() {
        ProcessBuilder processBuilder = new ProcessBuilder("xdg-open", new File(".bin/User-Manual.pdf").getPath());
        try {
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println("Open file....");
//        try {
//            Desktop.getDesktop().open(new File(".bin/User-Manual.pdf"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println("Doesn't block?");
    }
}
