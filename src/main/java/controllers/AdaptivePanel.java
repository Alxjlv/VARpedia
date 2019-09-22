package controllers;

import com.google.common.eventbus.Subscribe;
import events.SwitchSceneEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class AdaptivePanel extends Controller {

    @FXML
    public VBox adaptiveArea;

    @FXML
    private GridPane CreateView;

    @FXML
    private GridPane NameView;

    @FXML
    private GridPane SnippetView;

    @FXML
    private GridPane VideoView;


    @FXML public void initialize() throws IOException {
        loadScene("/CreateView.fxml",adaptiveArea);
        bus.register(this);
    }

    @FXML public VBox getAdaptiveArea(){
        return adaptiveArea;
    }

    @FXML public void setCreateView(){
        adaptiveArea.getChildren().clear();
        adaptiveArea.getChildren().add(CreateView);
    }

    @Subscribe
    public void SwitchEventHandler(SwitchSceneEvent event)throws IOException{
        loadScene(event.getNext(),adaptiveArea);
    }

}
