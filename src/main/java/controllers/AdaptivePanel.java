package controllers;

import com.google.common.eventbus.Subscribe;
import events.SwitchSceneEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class AdaptivePanel extends Controller {

    @FXML BorderPane adaptiveArea;
    @FXML ComboBox dropdown;


    @FXML public void initialize() throws IOException {
        loadScene("/WelcomeView.fxml");
        dropdown.getItems().add("Name");
        dropdown.getItems().add("Date created");
        dropdown.getItems().add("Duration");
        dropdown.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                System.out.println(newValue);
            }
        });
        dropdown.getSelectionModel().selectFirst();
    }

    @Override
    protected String handle(SwitchSceneEvent event){
        try {
            loadScene(event.getNext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @FXML
    public void loadScene(String fxml) throws IOException {
        load = new FXMLLoader(this.getClass().getResource(fxml));
        GridPane test = load.load();
        Controller controller = load.getController();
        controller.setListener(this);
        adaptiveArea.setCenter(test);
    }

    @FXML public void pressCreate() throws IOException{
        loadScene("/CreateView.fxml");
    }

    @FXML public void pressDelete(){

    }

}
