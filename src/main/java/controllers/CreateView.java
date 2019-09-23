package controllers;

import events.SwitchSceneEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;


import java.io.IOException;

public class CreateView extends AdaptivePanel {

    @FXML GridPane CreateView;


    @FXML public void initialize(){
        bus.register(this);
    }

    @FXML public void pressSearch(){

        listener.handle(new SwitchSceneEvent(this, "/SnippetView.fxml"));
    }

}
