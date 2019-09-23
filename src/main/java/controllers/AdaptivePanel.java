package controllers;

import com.google.common.eventbus.Subscribe;
import events.SwitchSceneEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class AdaptivePanel extends Controller {

    @FXML
    public BorderPane adaptiveArea;

    @FXML
    private GridPane CreateView;

    @FXML
    private GridPane NameView;

    @FXML
    private GridPane SnippetView;

    @FXML
    private GridPane VideoView;


    @FXML public void initialize() throws IOException {
        loadScene("/CreateView.fxml");
        bus.register(this);
    }

    @FXML public void setCreateView(){
        adaptiveArea.getChildren().clear();
        adaptiveArea.getChildren().add(CreateView);
    }

    @Subscribe
    public void SwitchEventHandler(SwitchSceneEvent event)throws IOException{
        loadScene(event.getNext());
    }

    @FXML
    public void loadScene(String fxml) throws IOException {

        load = new FXMLLoader(this.getClass().getResource(fxml));
        GridPane test = load.load();
        adaptiveArea.setCenter(test);
        //Controller controller = load.getController();
        //controller.setAdaptiveArea(adaptiveArea);

    }

}
