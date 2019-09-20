package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class Controller {

    @FXML
    public void switchScene(String fxml, VBox page) throws IOException {
        Parent pane = (Parent) FXMLLoader.load(getClass().getResource(fxml));
        Stage stage = (Stage) page.getScene().getWindow();
        Scene scene = stage.getScene();

        scene = new Scene(pane);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.setResizable(false);
    }

    @FXML public void loadScene(String fxml, Pane page) throws IOException{
        page.getChildren().clear();
        page.getChildren().add(FXMLLoader.load(getClass().getResource(fxml)));
    }

}
