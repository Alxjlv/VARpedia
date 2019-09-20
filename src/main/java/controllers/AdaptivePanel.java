package controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class AdaptivePanel extends Controller {

    @FXML
    VBox adaptiveArea;

    @FXML public void initialize() throws IOException {
        loadScene("/CreateView",adaptiveArea);
    }
}
