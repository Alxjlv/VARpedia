package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

public abstract class Controller {

    protected FXMLLoader load;
    @FXML VBox adaptiveArea;

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
