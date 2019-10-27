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
import views.CreationCell;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;

/**
 * Adaptive Panel is the main scene that the application loads. It loads smaller sub-scenes into itself while maintaining
 * a consistent side {@link ListView} of creations.
 * @author Tait & Alex
 */
public class AdaptivePanel extends Controller {

    @FXML private BorderPane adaptiveArea; // This is the area which the sub-scenes are loaded into
    @FXML private ChoiceBox<Comparator<Creation>> sortDropdown; // The dropdown of sorting options
    @FXML private ListView<Creation> creationsListView; // Consistent side ListView

    // The three buttons in the side pane, named accordingly
    @FXML private Button createButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private SortedList<Creation> sortedCreations; // A sorted ArrayList of creations

    // Allowing the selected creation to be set from controllers.MediaView
    private static Creation selectedCreation;
    public static Creation getSelectedCreation() {
        return selectedCreation;
    }
    private static void setSelectedCreation(Creation creation) {
        selectedCreation = creation;
    }

    /**
     * The selected creation will be loaded into the selectedCreationMediaPlayer to be played by {@link controllers.VideoView}
     * This allows controllers.VideoView control play/pause etc.
     */
    private static MediaPlayer selectedCreationMediaPlayer = null;
    public static void setSelectedCreationMediaPlayer(MediaPlayer mediaPlayer) {
        selectedCreationMediaPlayer = mediaPlayer;
    }

    // These fields & methods make the selected comparator accessible to other classes, such as views.CreationCell
    private static final ReadOnlyObjectWrapper<Comparator<Creation>> selectedComparator = new ReadOnlyObjectWrapper<>();
    public static Comparator<Creation> getSelectedComparator() {
        return selectedComparator.get();
    }
    public static ReadOnlyObjectProperty<Comparator<Creation>> selectedComparatorProperty() {
        return selectedComparator.getReadOnlyProperty();
    }

    /**
     * The initialize method is responsible for loading the first sub-scene into the adaptive panel, setting up the side
     * list & loading content into it, and setting up listeners for selecting items
     * @throws IOException - if the sub-scene doesn't load properly
     */
    @FXML public void initialize() throws IOException {
        loadScene(View.WELCOME.get()); // Loading the welcome scene

        /*Getting the observable array list of creations which are stored in the CreationFileManager singleton. When the
          CreationFileManager is first loaded, it will read all serialised creations and load them into an internal
          ObservableList
        */
        ObservableList<Creation> creationsList = CreationFileManager.getInstance().getItems();

        /* Listener checking if a creation was removed, and if so, then checking if it was the last creation in the list
           If it was the last item then it loads the welcome screen again
        */
        creationsList.addListener((ListChangeListener<Creation>) c -> {
            while (c.next()) {
                if (c.wasRemoved() && c.getList().size() == 0) {
                    try {
                        loadScene(View.WELCOME.get());
                    } catch (IOException e) {
                        return;
                    }
                }
            }
        });
        // Wrapping the list of creations in CreationFileManager in a SortedList
        sortedCreations = CreationFileManager.getInstance().getItems().sorted();

        // Setting the comparators to display in the dropdown
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
        // Setting a listener so that when a comparator is selected, the way the list is sorted is changed
        sortDropdown.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                sortedCreations.setComparator(newValue);
            }
        });
        // Automatically selecting the first item in the list
        sortDropdown.getSelectionModel().selectFirst();
        // Binding the selected comparator to the internally used comparator
        selectedComparator.bind(sortDropdown.getSelectionModel().selectedItemProperty());

        // Displaying the sorted list of creations
        creationsListView.setItems(sortedCreations);
        // Default message/placeholder for the list if they haven't created anything yet
        Label emptyList = new Label("Click \"Create\" to get started!");
        emptyList.setFont(new Font(16.0));
        creationsListView.setPlaceholder(emptyList);

        // Setting the cell factory for the ListView for when a new creation is created
        creationsListView.setCellFactory(param -> new CreationCell());

        /* Adding a listener to check if the selected creation actually exists, and if it does, switching to change to
           playing that video. Otherwise if the selected item doesn't exits, an error popup is thrown
        */
        creationsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Checking if a new creation has been selected
            if (newValue != null && newValue != oldValue && newValue != selectedCreation) {
                // Checking if the creation exists
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
                        return;
                    }
                } else {
                    // Switching to the new creation and stopping the old one
                    stopPlayer();
                    setSelectedCreation(newValue);
                    try {
                        loadScene(View.VIDEO.get());
                    } catch (IOException e) {
                        return;
                    }
                }
            }
            // Disabling the delete and edit buttons if there are no creations
            if (newValue == null) {
                deleteButton.setDisable(true);
                editButton.setDisable(true);
            } else {
                deleteButton.setDisable(false);
                editButton.setDisable(false);
            }
        });

        // When the user creates a new creation, this will select it automatically
        sortedCreations.addListener((ListChangeListener<Creation>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    creationsListView.getSelectionModel().select(c.getList().get(c.getFrom()));
                }
            }
        });
        // Disabling the delete and edit buttons if nothing is selected
        deleteButton.setDisable(true);
        editButton.setDisable(true);
    }

    /**
     * This handles events which want to switch back to the welcome scene. It clears selection of items and emtpies the
     * media player
     * @param event - a {@link SwitchSceneEvent} sent by a button in a sub-scene to change to a different sub-scene
     */
    @Override
    protected void handle(SwitchSceneEvent event) {
        try {
            if (event.getNext() == View.WELCOME.get()) {
                creationsListView.getSelectionModel().clearSelection();
                setSelectedCreation(null);
            }
            loadScene(event.getNext());
        } catch (IOException ignored) {
        }
    }

    /**
     * This handles events which want to sequentially move back and forward within the creation process, and loads the
     * subsequent sub-scenes
     * @param event - a {@link CreationProcessEvent} to indicate which step of the creation process the user is at
     */
    @Override
    public void handle(CreationProcessEvent event) {
        if (event.getStatus() == CreationProcessEvent.Status.BEGIN_CREATE) {
            // Resetting the FormManager on beginning the creation process
            FormManager.getInstance().reset();

            disableControls();

            // Switching to the search scene
            handle(new SwitchSceneEvent(this, View.SEARCH.get()));
        } else if (event.getStatus() == CreationProcessEvent.Status.BEGIN_EDIT) {
            // Resetting the FormManager on beginning editing
            FormManager formManager = FormManager.getInstance();
            formManager.reset();
            formManager.setEdit(creationsListView.getSelectionModel().getSelectedItem());

            disableControls();

            // Switching to the chunk scene
            handle(new SwitchSceneEvent(this, View.CHUNK.get()));
        } else if (event.getStatus() == CreationProcessEvent.Status.SAVE) {
            enableControls();
        } else if (event.getStatus() == CreationProcessEvent.Status.CANCEL_EDIT) {
            enableControls();

            handle(new SwitchSceneEvent(this, View.VIDEO.get()));
        } else {
            enableControls();

            handle(new SwitchSceneEvent(this, View.WELCOME.get()));
        }
    }

    /**
     * Disabling the controls for the side creation list
     */
    private void disableControls() {
        creationsListView.setDisable(true);
        sortDropdown.setDisable(true);
        createButton.setDisable(true);
    }

    /**
     * Enabling the controls for the side creation list
     */
    private void enableControls() {
        sortDropdown.setDisable(false);
        createButton.setDisable(false);
        creationsListView.setDisable(false);
    }

    /**
     * Loads the requested fxml sub-scene into the center of the adaptive panel adaptive {@link BorderPane}
     * @param scene - a {@link URL} of the fxml file location to be loaded within the adaptive panel
     * @throws IOException - if the scene fails to load
     */
    @FXML public void loadScene(URL scene) throws IOException {
        load = new FXMLLoader(scene);
        GridPane subScene = load.load();
        Controller controller = load.getController();
        controller.setListener(this);
        adaptiveArea.setCenter(subScene);
    }

    /**
     * When the "Create" button is pressed, it removes selection and moves to the search scene
     */
    @FXML public void pressCreate() {
        setSelectedCreation(null);
        handle(new CreationProcessEvent(this, CreationProcessEvent.Status.BEGIN_CREATE));
    }

    /**
     * When the "Edit" button is pressed, it moves to the editing view, to the snippet view
     */
    @FXML public void pressEdit() {
        handle(new CreationProcessEvent(this, CreationProcessEvent.Status.BEGIN_EDIT));
    }

    /**
     * Stops the player & sends a confirmation message to the user asking if they really want to delete it
     */
    @FXML public void pressDelete() {
        stopPlayer();
        setSelectedCreation(null); // Deselecting the player
        Creation creation = creationsListView.getSelectionModel().getSelectedItem();

        // Sending an alert to the user
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                String.format("Are you sure you want to delete \"%s\"?", creation.getName()),
                ButtonType.YES, ButtonType.CANCEL);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); // Credit to Di Kun Ong (dngo711) for this line
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            CreationFileManager.getInstance().delete(creation);
        }
    }

    /**
     * Stops the player
     */
    private void stopPlayer() {
        if (selectedCreationMediaPlayer != null) {
            selectedCreationMediaPlayer.stop();
        }
    }
}
