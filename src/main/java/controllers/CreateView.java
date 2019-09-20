package controllers;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class CreateView extends AdaptivePanel {

    @FXML GridPane CreateView;

    @FXML public void pressSearch() throws IOException {


        loadScene("/SnippetView.fxml",adaptiveArea);
    }

}
