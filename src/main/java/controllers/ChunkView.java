package controllers;

import constants.View;
import events.CreationProcessEvent;
import events.SwitchSceneEvent;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import main.ProcessRunner;
import models.FormManager;
import models.chunk.Chunk;
import models.chunk.ChunkFileBuilder;
import models.chunk.ChunkFileManager;
import models.synthesizer.EspeakSynthesizer;
import models.synthesizer.Synthesizer;
import views.ChunkCell;
import java.io.File;
import java.util.Iterator;

/**
 * ChunkView is the scene responsible for the user being able to create and edit {@link models.chunk.Chunk}s. This allows them to preview
 * their chunks, edit the text, choose voices etc.
 * This scene is also responsible for sending a lot of the creation process data to other places to be serialised
 *
 * @author Tait & Alex
 */
public class ChunkView extends Controller {

    @FXML private ListView<Chunk> chunksListView; // List of chunks the user can interact with
    @FXML private TextArea searchResult; // The text the user is able to edit and generate chunks from
    @FXML private ChoiceBox<Synthesizer> voiceDropdown; // Dropdown of different voices
    @FXML private Text highlightingMessage; // Message to let the user know if they've exceeded the 40 word limit

    // Buttons used in the scene
    @FXML private ToggleButton previewButton;
    @FXML private Button saveButton;
    @FXML private ToggleButton playbackAllButton;
    @FXML private ToggleButton playbackButton;
    @FXML private Button deleteButton;
    @FXML private Button backButton;
    @FXML private Button nextButton;
    @FXML private Button downButton;
    @FXML private Button upButton;


    private Synthesizer synthesizer; // The synthesizer used for the voice
    private MediaPlayer mediaPlayer; // The player that will play the requested voice (eg. for previewing)

    // The iterator is used for the "Playback All" functionality
    private ReadOnlyObjectWrapper<Iterator<Chunk>> chunkIterator = new ReadOnlyObjectWrapper<>();
    // The preview process is used to preview a single highlighted chunk
    private ReadOnlyObjectWrapper<ProcessRunner> previewProcess = new ReadOnlyObjectWrapper<>();

    /**
     * The initialize method is responsible for setting up the scene & the setting up the listeners and handlers for each
     * part of the application
     */
    @FXML public void initialize() {
        // Listener for the highlighted text
        searchResult.selectedTextProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                // Setting a message if they haven't selected anything
                highlightingMessage.setText("Highlight text to create snippets");
                highlightingMessage.setFill(Color.BLACK);
                previewButton.setDisable(true);
                saveButton.setDisable(true);
            } else if (newValue.split("\\s+").length >= 40) {
                // Setting an error message if they've selected more than 40 words, and disabling the buttons
                highlightingMessage.setText("Please select less than 40 words");
                highlightingMessage.setFill(Color.web("e80c0c"));
                previewButton.setDisable(true);
                saveButton.setDisable(true);
            } else {
                // Otherwise enable the buttons
                highlightingMessage.setText("");
                previewButton.setDisable(false);
                saveButton.setDisable(false);
            }
        });

        // Disabling all buttons that require the user to have done something by default
        previewButton.setDisable(true);
        saveButton.setDisable(true);
        playbackButton.setDisable(true);
        playbackAllButton.setDisable(true);
        deleteButton.setDisable(true);

        FormManager formManager = FormManager.getInstance();

        // Removing the back button if editing
        if (formManager.getMode() == FormManager.Mode.EDIT) {
            backButton.setVisible(false);
        }
        // Disabling next if no chunks have been created
        if (ChunkFileManager.getInstance().getItems().isEmpty()) {
            nextButton.setDisable(true);
        }

        // Setting a placeholder text for no chunks
        chunksListView.setItems(ChunkFileManager.getInstance().getItems());
        Label emptyList = new Label("Save a Snippet to continue!");
        emptyList.setFont(new Font(16.0));
        chunksListView.setPlaceholder(emptyList);

        // Setting a cell factory for the chunk list
        chunksListView.setCellFactory(param -> new ChunkCell());

        // Setting a listener which allows toggling of buttons based on selected/not selected chunks
        chunksListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                playbackButton.setDisable(false);
                deleteButton.setDisable(false);
            } else {
                playbackButton.setDisable(true);
                deleteButton.setDisable(true);
            }
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        });
        // Setting a listener which allows the list to update based on whether items are deleted/added to the list
        chunksListView.getItems().addListener((ListChangeListener<Chunk>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    chunksListView.getSelectionModel().select(c.getFrom());
                }
                if (c.getList().isEmpty()) {
                    playbackAllButton.setDisable(true);
                    upButton.setDisable(true);
                    downButton.setDisable(true);
                    nextButton.setDisable(true);
                } else {
                    playbackAllButton.setDisable(false);
                    nextButton.setDisable(false);
                }
            }
        });
        // Setting a listener for the up/down buttons to disable up when at the top item, and disable down when at the bottom item
        chunksListView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() == 0) {
                upButton.setDisable(true);
            } else {
                upButton.setDisable(false);
            }
            if (newValue.intValue() == chunksListView.getItems().size()-1) {
                downButton.setDisable(true);
            } else {
                downButton.setDisable(false);
            }
        });
        // Both up/down are disabled by default
        upButton.setDisable(true);
        downButton.setDisable(true);

        // Binding the search result with a stored field in the FormManager singleton
        searchResult.textProperty().bindBidirectional(formManager.searchTextProperty());

        // Setting up the voice dropdown with espeak voices
        ObservableList<Synthesizer> voices = FXCollections.observableArrayList();
        for (EspeakSynthesizer.Voice voice: EspeakSynthesizer.Voice.values()) {
            voices.add(new EspeakSynthesizer(voice));
        }
        voiceDropdown.setItems(voices);
        // Adding a listener to change the voice used for the chunk previewing/saving
        voiceDropdown.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> synthesizer = newValue);
        // Selecting the first synthesizer by default
        voiceDropdown.getSelectionModel().select(0);
    }

    /**
     * Previews a highlighted chunk of text, while allowing stopping of playing the preview
     */
    @FXML public void pressPreview() {
        if (previewButton.isSelected()) {
            if (mediaPlayer != null) { // Stops other playback
                mediaPlayer.stop();
            }
            chunkIterator.set(chunksListView.getItems().iterator());

            previewProcess.set(synthesizer.preview(searchResult.getSelectedText()));// Starts playing the preview
            chunkIterator.set(null);

            // Deselects the preview button after playback
            previewProcess.get().setOnSucceeded(event -> previewButton.setSelected(false));
            previewProcess.get().setOnCancelled(event -> previewButton.setSelected(false));
        } else {
            if (previewProcess != null) { // Cancels other playback
                previewProcess.get().cancel();
            }
        }
    }

    /**
     * Creates a {@link models.chunk.Chunk} object when the user presses save, and stores it in the {@link ChunkFileManager} singleton, which automatically
     * updates the observable list
     */
    @FXML public void pressSave() {
        ChunkFileBuilder chunkBuilder = ChunkFileManager.getInstance().getBuilder();
        chunkBuilder.setText(searchResult.getSelectedText()).setSynthesizer(synthesizer);
        ChunkFileManager.getInstance().create(chunkBuilder);
    }

    /**
     * Plays back a selected previously saved {@link models.chunk.Chunk}, allowing button toggling to stop playback
     */
    @FXML public void pressPlayback() {
        if (playbackButton.isSelected()) {
            chunkIterator.set(null);
            if(previewProcess.get()!=null){ // Cancels playback
                previewProcess.get().cancel();
            }

            // Gets the chunk from the ChunkFileManager, and then plays it
            File audioFile = ChunkFileManager.getInstance().getFile(chunksListView.getSelectionModel().getSelectedItem());
            playMedia(audioFile);

            // Deselects buttons after playback
            mediaPlayer.setOnEndOfMedia(() -> playbackButton.setSelected(false));
            mediaPlayer.setOnStopped(() -> playbackButton.setSelected(false));
        } else {
            if (mediaPlayer != null) { // Cancels playback
                mediaPlayer.stop();
            }
        }
    }

    /**
     * Iterates through each {@link models.chunk.Chunk} in order in the chunk list and plays them, to give the user a good representation of
     * what their creation might sound like with those selected chunks
     */
    @FXML public void pressPlaybackAll() {
        if (playbackAllButton.isSelected()) {
            if(previewProcess.get()!=null){ // Cancels any previews
                previewProcess.get().cancel();
            }

            // Sets the iterator
            chunkIterator.set(chunksListView.getItems().iterator());
            chunkIterator.addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    playbackAllButton.setSelected(false);
                }
            });
            // Plays back each chunk in order
            recursivePlayback();

        } else {
            if (mediaPlayer != null) { // Cancels playback
                mediaPlayer.stop();
            }
        }
    }

    /**
     * Deletes a selected {@link models.chunk.Chunk} and stops the media player from playing
     */
    @FXML public void pressDelete() {
        ChunkFileManager.getInstance().delete(chunksListView.getSelectionModel().selectedItemProperty().getValue());
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    /**
     * Swaps a {@link models.chunk.Chunk} with the chunk above it
     */
    @FXML public void pressUp() {
        int index = chunksListView.getSelectionModel().getSelectedIndex();
        Chunk source = chunksListView.getSelectionModel().getSelectedItem();
        Chunk target = chunksListView.getItems().get(index-1);
        ChunkFileManager.getInstance().reorder(source, target);
        chunksListView.getSelectionModel().select(index-1);
    }

    /**
     * Swaps a {@link models.chunk.Chunk} with the chunk below it
     */
    @FXML public void pressDown() {
        int index = chunksListView.getSelectionModel().getSelectedIndex();
        Chunk source = chunksListView.getSelectionModel().getSelectedItem();
        Chunk target = chunksListView.getItems().get(index+1);
        ChunkFileManager.getInstance().reorder(source, target);
        chunksListView.getSelectionModel().select(index+1);
    }

    /**
     * Navigates to the previous scene (the search scene) after displaying a popup warning that progress will be lost
     */
    @FXML public void pressBack() {
        if (!ChunkFileManager.getInstance().getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "If you go back your progress will not be saved. Do you wish to continue?",
                    ButtonType.YES, ButtonType.CANCEL);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); // Credit to Di Kun Ong (dngo711) for this line
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                listener.handle(new SwitchSceneEvent(this, View.SEARCH.get()));
            }
        } else {
            listener.handle(new SwitchSceneEvent(this, View.SEARCH.get()));
        }
    }

    /**
     * Navigates to the welcome scene after displaying a popup warning that progress will be lost
     */
    @FXML public void pressCancel() {
        if (FormManager.getInstance().getMode() == FormManager.Mode.EDIT) {
            alertMessage(
                    "If you go back you will lose any unsaved changes. Do you wish to continue?",
                    new CreationProcessEvent(this, CreationProcessEvent.Status.CANCEL_EDIT)
            );
        } else if (!ChunkFileManager.getInstance().getItems().isEmpty()) {
            alertMessage(
                    "If you go back your progress will not be saved. Do you wish to continue?",
                    new CreationProcessEvent(this, CreationProcessEvent.Status.CANCEL_CREATE)
            );
        } else {
            listener.handle(new CreationProcessEvent(this, CreationProcessEvent.Status.CANCEL_CREATE));
        }
    }

    /**
     * Creates an alert and displays it to the user, and sends and event to the AdaptivePanel if the scene change is
     * approved
     * @param message - the message to the user
     * @param event - a {@link CreationProcessEvent} that if the user confirms, will switch the scene
     */
    private void alertMessage(String message, CreationProcessEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.CANCEL);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); // Credit to Di Kun Ong (dngo711) for this line
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            listener.handle(event);
        }
    }

    /**
     * Moves to the next scene (the image preview scene) if the user has made a {@link models.chunk.Chunk}, otherwise
     * prompts them to create a chunk
     */
    @FXML public void pressNext() {
        if (ChunkFileManager.getInstance().getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please make a snippet to continue");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); // Credit to Di Kun Ong (dngo711) for this line
            alert.showAndWait();
            return;
        }
        listener.handle(new SwitchSceneEvent(this, View.IMAGE_PREVIEW.get()));
    }

    /**
     * Plays each {@link models.chunk.Chunk} in the order they are in the chunk list by recursively playing the next chunk
     */
    private void recursivePlayback() {
        if (chunkIterator.get() != null && chunkIterator.get().hasNext()) {
            Chunk chunk = chunkIterator.get().next();

            chunksListView.getSelectionModel().select(chunk);

            File audioFile = ChunkFileManager.getInstance().getFile(chunksListView.getSelectionModel().getSelectedItem());

            playMedia(audioFile);
            mediaPlayer.setOnEndOfMedia(this::recursivePlayback);
        } else {
            playbackAllButton.setSelected(false);
        }
    }

    /**
     * Loads an audio file into the media player
     * @param audioFile - the audio of the chunk that the user wants to play
     */
    private void playMedia(File audioFile) {
        Media media = new Media(audioFile.toURI().toString());
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
    }
}
