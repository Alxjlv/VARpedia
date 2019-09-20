package controllers;

import events.CreateViewEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;


import java.io.IOException;

public class CreateView extends AdaptivePanel {

    @FXML GridPane CreateView;


    @FXML public void initialize(){
        bus.register(this);
    }

    @FXML public void pressSearch() throws IOException {

        bus.post(new CreateViewEvent());
        loadScene("/SnippetView.fxml",adaptiveArea);
    }

}
