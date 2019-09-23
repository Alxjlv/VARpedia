package controllers;

import com.google.common.eventbus.Subscribe;
import events.SwitchSceneEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import main.CreationCellFactory;
import models.Creation;
import models.CreationManager;

import java.io.IOException;

public class AdaptivePanel extends Controller {

    @FXML BorderPane adaptiveArea;
    @FXML ComboBox dropdown;

    @FXML ListView<Creation> creationsListView;

    private SortedList<Creation> sortedCreations;

    @FXML public void initialize() throws IOException {
        loadScene("/WelcomeView.fxml");

        // TODO - Get list of comparators from Creation
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

        CreationManager.getInstance().load();
        sortedCreations = CreationManager.getInstance().getItems().sorted();
        creationsListView.setItems(sortedCreations);
        creationsListView.setCellFactory(new CreationCellFactory());
        creationsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Creation>() {
            @Override
            public void changed(ObservableValue<? extends Creation> observable, Creation oldValue, Creation newValue) {
                try {
                    if (newValue != null) {
                        loadScene("/VideoView.fxml");
                    }
                } catch (IOException e) {
                    // TODO - Do something
                }
            }
        });
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

        creationsListView.getSelectionModel().clearSelection();
    }

    @FXML public void pressDelete(){
        CreationManager.getInstance().delete(creationsListView.getSelectionModel().selectedItemProperty().getValue());
    }

}
