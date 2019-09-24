package controllers;

import com.google.common.eventbus.Subscribe;
import events.SwitchSceneEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import main.CreationCellFactory;
import models.Creation;
import models.CreationManager;
import models.MediaSingleton;

import java.io.IOException;
import java.util.Comparator;

public class AdaptivePanel extends Controller {

    @FXML BorderPane adaptiveArea;
    @FXML ComboBox<Comparator<Creation>> dropdown;

    @FXML ListView<Creation> creationsListView;

    private SortedList<Creation> sortedCreations;

    @FXML public void initialize() throws IOException {
        loadScene("/WelcomeView.fxml");

        CreationManager.getInstance().load();
        sortedCreations = CreationManager.getInstance().getItems().sorted();

        // TODO - Get list of comparators from Creation/CreationManager
        ObservableList<Comparator<Creation>> comparators = FXCollections.observableArrayList();
        comparators.add(new Comparator<Creation>() {
            @Override
            public int compare(Creation o1, Creation o2) {
                return o1.getName().compareTo(o2.getName());
            }

            @Override
            public String toString() {
                return "Name";
            }
        });
        comparators.add(new Comparator<Creation>() {
            @Override
            public int compare(Creation o1, Creation o2) {
                return o2.getName().compareTo(o1.getName());
            }

            @Override
            public String toString() {
                return "Date";
            }
        });
        dropdown.setItems(comparators);
        dropdown.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Comparator<Creation>>() {
            @Override
            public void changed(ObservableValue<? extends Comparator<Creation>> observable, Comparator<Creation> oldValue, Comparator<Creation> newValue) {
                if (newValue != null) {
                    sortedCreations.setComparator(newValue);
                }
            }
        });
        dropdown.getSelectionModel().selectFirst();

        creationsListView.setItems(sortedCreations);
        creationsListView.setCellFactory(new CreationCellFactory());
        creationsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Creation>() {
            @Override
            public void changed(ObservableValue<? extends Creation> observable, Creation oldValue, Creation newValue) {
                if (newValue != null && newValue != oldValue && newValue != MediaSingleton.getInstance().getCreation()) {
                    MediaSingleton.getInstance().setCreation(newValue);
                    try {
                        loadScene("/VideoView.fxml");
                    } catch (IOException e) {
                        // TODO - Do something
                    }
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
