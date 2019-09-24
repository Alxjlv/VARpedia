package controllers;

import events.SwitchSceneEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

public class CreateView extends AdaptivePanel {

    @FXML GridPane CreateView;

    @FXML public void initialize() {
    }

    @FXML public void pressSearch() {
        // TODO - Run wikit search
        // TODO - "Searching." -> "Searching.." -> "Searching..." message
        listener.handle(new SwitchSceneEvent(this, "/SnippetView.fxml"));
    }

    @FXML public void pressCancel() {
        // TODO - No alert required?
        listener.handle(new SwitchSceneEvent(this, "/WelcomeView.fxml"));
    }

}
