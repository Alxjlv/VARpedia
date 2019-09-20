package controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class AdaptivePanel extends Controller {

    @FXML
    public VBox adaptiveArea;



    @FXML public void initialize() throws IOException {
        loadScene("/CreateView.fxml",adaptiveArea);
    }

    @FXML public VBox getAdaptiveArea(){
        return adaptiveArea;
    }

}
