package controllers;

import constants.View;
import events.CreationProcessEvent;
import events.SwitchSceneEvent;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import models.FormManager;
import models.creation.Creation;
import models.creation.CreationComparators;
import models.creation.CreationFileManager;
import views.CreationCellFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;

public class AdaptivePanel extends Controller {

    @FXML
    BorderPane adaptiveArea;
    @FXML
    ChoiceBox<Comparator<Creation>> sortDropdown;

    @FXML
    ListView<Creation> creationsListView;

    @FXML
    Button createButton;
    @FXML
    Button editButton;
    @FXML
    Button deleteButton;

    private SortedList<Creation> sortedCreations; // Could be local variable in initialise()?

    // Fields and Methods to interact with controllers.VideoView
    private static Creation selectedCreation;
    public static Creation getSelectedCreation() {
        return selectedCreation;
    }
    private static void setSelectedCreation(Creation creation) {
        selectedCreation = creation;
    }
    private static MediaPlayer selectedCreationMediaPlayer = null;
    public static void setSelectedCreationMediaPlayer(MediaPlayer mediaPlayer) {
        selectedCreationMediaPlayer = mediaPlayer;
    }

    private static final ReadOnlyObjectWrapper<Comparator<Creation>> selectedComparator = new ReadOnlyObjectWrapper<>();
    public static Comparator<Creation> getSelectedComparator() {
        return selectedComparator.get();
    }
    public static ReadOnlyObjectProperty<Comparator<Creation>> selectedComparatorProperty() {
        return selectedComparator.getReadOnlyProperty();
    }


    @FXML public void initialize() throws IOException {
        loadScene(View.WELCOME.get());

        ObservableList<Creation> creationsList = CreationFileManager.getInstance().getItems();

        creationsList.addListener((ListChangeListener<Creation>) c -> {
            while (c.next()) {
                if (c.wasRemoved() && c.getList().size() == 0) {
                    try {
                        loadScene(View.WELCOME.get());
                    } catch (IOException e) {
                        // TODO - Handle exception
                    }
                }
            }
        });
        sortedCreations = CreationFileManager.getInstance().getItems().sorted();

        sortDropdown.setItems(FXCollections.observableArrayList(
                CreationComparators.TO_REVIEW,
                CreationComparators.NAME_A_TO_Z,
                CreationComparators.NAME_Z_TO_A,
                CreationComparators.LEAST_VIEWED,
                CreationComparators.MOST_VIEWED,
                CreationComparators.LEAST_CONFIDENT,
                CreationComparators.MOST_CONFIDENT,
                CreationComparators.NEWEST,
                CreationComparators.OLDEST
                ));
        sortDropdown.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Comparator<Creation>>() {
            @Override
            public void changed(ObservableValue<? extends Comparator<Creation>> observable, Comparator<Creation> oldValue, Comparator<Creation> newValue) {
                if (newValue != null) {
                    sortedCreations.setComparator(newValue);
                }
            }
        });
        sortDropdown.getSelectionModel().selectFirst();
        selectedComparator.bind(sortDropdown.getSelectionModel().selectedItemProperty());

        creationsListView.setItems(sortedCreations);
        Label emptyList = new Label("Click \"Create\" to get started!");
        emptyList.setFont(new Font(16.0));
        creationsListView.setPlaceholder(emptyList);
        creationsListView.setCellFactory(new CreationCellFactory());
        creationsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue != oldValue && newValue != selectedCreation) {
                if (!CreationFileManager.getInstance().getVideoFile(newValue).exists()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("File not found");
                    alert.setContentText(String.format("The video file for creation %s could not be found and will be" +
                            " removed from this list", newValue.getName()));
                    alert.showAndWait();
                    CreationFileManager.getInstance().delete(newValue);

                    creationsListView.getSelectionModel().clearSelection();
                    try {
                        loadScene(View.WELCOME.get());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    stopPlayer();
                    setSelectedCreation(newValue);
                    try {
                        loadScene(View.VIDEO.get());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (newValue == null) {
                deleteButton.setDisable(true);
                editButton.setDisable(true);
            } else {
                deleteButton.setDisable(false);
                editButton.setDisable(false);
            }
        });
        sortedCreations.addListener(new ListChangeListener<Creation>() {
            @Override
            public void onChanged(Change<? extends Creation> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        System.out.println("Select: "+c.getList().get(c.getFrom()));
                        creationsListView.getSelectionModel().select(c.getList().get(c.getFrom()));
                    }
                }
            }
        });

        deleteButton.setDisable(true);
        editButton.setDisable(true);
    }

    @Override
    protected URL handle(SwitchSceneEvent event) {
        try {
            if (event.getNext() == View.WELCOME.get()) {
                creationsListView.getSelectionModel().clearSelection();
                setSelectedCreation(null);
            }
            loadScene(event.getNext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void handle(CreationProcessEvent event) {
        if (event.getStatus() == CreationProcessEvent.Status.BEGIN) {
            FormManager.getInstance().reset();

            creationsListView.getSelectionModel().clearSelection();
            creationsListView.setDisable(true);
            sortDropdown.setDisable(true);
            createButton.setDisable(true);

            try {
                loadScene(View.SEARCH.get());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (event.getStatus() == CreationProcessEvent.Status.SAVE_EDIT) {
            FormManager formManager = FormManager.getInstance();
            formManager.reset();
            formManager.setEdit(creationsListView.getSelectionModel().getSelectedItem());

            creationsListView.getSelectionModel().clearSelection();
            creationsListView.setDisable(true);
            sortDropdown.setDisable(true);
            createButton.setDisable(true);

            try {
                loadScene(View.CHUNK.get());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            sortDropdown.setDisable(false);
            createButton.setDisable(false);
            creationsListView.setDisable(false);

            try {
                loadScene(View.WELCOME.get());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void loadScene(URL scene) throws IOException {
        load = new FXMLLoader(scene);
        GridPane test = load.load();
        Controller controller = load.getController();
        controller.setListener(this);
        adaptiveArea.setCenter(test);
    }

    @FXML public void pressCreate() {
        setSelectedCreation(null);
        handle(new CreationProcessEvent(this, CreationProcessEvent.Status.BEGIN));
    }

    @FXML public void pressEdit() {
        handle(new CreationProcessEvent(this, CreationProcessEvent.Status.SAVE_EDIT));
    }

    @FXML public void pressDelete() {
        stopPlayer();
        setSelectedCreation(null);
        Creation creation = creationsListView.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                String.format("Are you sure you want to delete \"%s\"?", creation.getName()),
                ButtonType.YES, ButtonType.CANCEL);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); // Credit to Di Kun Ong (dngo711) for this line
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            CreationFileManager.getInstance().delete(creation);
        }
    }

    private void stopPlayer() {
        if (selectedCreationMediaPlayer != null) {
            selectedCreationMediaPlayer.stop();
        }
    }
}
