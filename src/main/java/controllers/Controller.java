package controllers;

import com.google.common.eventbus.EventBus;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import main.CommunicationBus;

import java.io.IOException;

public abstract class Controller {

    protected FXMLLoader load;
    @FXML VBox adaptiveArea;
    protected EventBus bus = CommunicationBus.getBus();

    @FXML public void loadScene(String fxml, VBox adaptiveArea) throws IOException{
        load = new FXMLLoader(this.getClass().getResource(fxml));
        //Controller controller = load.getController();
        //controller.setAdaptiveArea(adaptiveArea);
        adaptiveArea.getChildren().add(load.load());
    }

    public void setAdaptiveArea(VBox adaptiveArea) {
        this.adaptiveArea = adaptiveArea;
    }
}
