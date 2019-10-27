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
import javafx.scene.text.Font;
import main.ProcessRunner;
import models.FormManager;
import models.chunk.Chunk;
import models.chunk.ChunkFileBuilder;
import models.chunk.ChunkFileManager;
import models.synthesizer.EspeakSynthesizer;
import models.synthesizer.Synthesizer;
import views.ChunkCellFactory;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.StringTokenizer;

public class ChunkView extends Controller {

    @FXML
    private ListView<Chunk> chunksListView;
    @FXML
    private TextArea searchResult;
    @FXML
    private ChoiceBox<Synthesizer> voiceDropdown;
    @FXML
    private ToggleButton previewButton;
    @FXML
    private Button saveButton;
    @FXML
    private ToggleButton playbackAllButton;
    @FXML
    private ToggleButton playbackButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button backButton;
    @FXML
    private Button downButton;
    @FXML
    private Button upButton;

    private Synthesizer synthesizer;
    private MediaPlayer mediaPlayer;
    private ReadOnlyObjectWrapper<Iterator<Chunk>> iterator = new ReadOnlyObjectWrapper<>();
    private ReadOnlyObjectWrapper<ProcessRunner> previewProcess = new ReadOnlyObjectWrapper<>();

    @FXML
    public void initialize() {
        searchResult.selectedTextProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                previewButton.setDisable(true);
                saveButton.setDisable(true);
            } else {
                previewButton.setDisable(false);
                saveButton.setDisable(false);
            }
        });
        previewButton.setDisable(true);
        saveButton.setDisable(true);

        FormManager formManager = FormManager.getInstance();

        playbackButton.setDisable(true);
        playbackAllButton.setDisable(true);
        deleteButton.setDisable(true);
        if (formManager.getMode() == FormManager.Mode.EDIT) {
            backButton.setVisible(false);
        }

        chunksListView.setItems(ChunkFileManager.getInstance().getItems());
        Label emptyList = new Label("Save a Snippet to continue!");
        emptyList.setFont(new Font(16.0));
        chunksListView.setPlaceholder(emptyList);
        chunksListView.setCellFactory(new ChunkCellFactory());
//        ChunkFileManager.getInstance().getItems().addListener((ListChangeListener<Chunk>) c -> {
//            while (c.next()) {
//
//            }
//        });
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
        chunksListView.getItems().addListener((ListChangeListener<Chunk>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    chunksListView.getSelectionModel().select(c.getFrom());
                }
                if (c.getList().isEmpty()) {
                    playbackAllButton.setDisable(true);
                    upButton.setDisable(true);
                    downButton.setDisable(true);
                } else {
                    playbackAllButton.setDisable(false);
                }
            }
        });
        chunksListView.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
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
            }
        });
        upButton.setDisable(true);
        downButton.setDisable(true);

        searchResult.textProperty().bindBidirectional(formManager.searchTextProperty());

        ObservableList<Synthesizer> voices = FXCollections.observableArrayList();
        for (EspeakSynthesizer.Voice voice: EspeakSynthesizer.Voice.values()) {
            voices.add(new EspeakSynthesizer(voice));
        }
        voiceDropdown.setItems(voices);
        voiceDropdown.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> synthesizer = newValue);
        voiceDropdown.getSelectionModel().select(0);
    }
  
    @FXML public void pressPreview() {
        if (checkWords(searchResult.getSelectedText())) {
            if (previewButton.isSelected()) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                }
                iterator.set(chunksListView.getItems().iterator());

                previewProcess.set(synthesizer.preview(searchResult.getSelectedText()));
                iterator.set(null);
                previewProcess.get().setOnSucceeded(event -> previewButton.setSelected(false));
                previewProcess.get().setOnCancelled(event -> previewButton.setSelected(false));
            } else {
                if (previewProcess != null) {
                    previewProcess.get().cancel();
                }
            }
        }
    }

    @FXML public void pressSave() {
        if (checkWords(searchResult.getSelectedText())) {
            ChunkFileBuilder chunkBuilder = ChunkFileManager.getInstance().getBuilder();
            chunkBuilder.setText(searchResult.getSelectedText()).setSynthesizer(synthesizer);
            ChunkFileManager.getInstance().create(chunkBuilder);
        }
    }

    @FXML public void pressPlayback() {
        if (playbackButton.isSelected()) {
            iterator.set(null);
            if(previewProcess.get()!=null){
                previewProcess.get().cancel();
            }

            File audioFile = ChunkFileManager.getInstance().getFile(chunksListView.getSelectionModel().getSelectedItem());

            playMedia(audioFile);
            mediaPlayer.setOnEndOfMedia(() -> playbackButton.setSelected(false));
            mediaPlayer.setOnStopped(() -> playbackButton.setSelected(false));
        } else {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        }
    }

    @FXML public void pressPlaybackAll() {
        if (playbackAllButton.isSelected()) {
            if(previewProcess.get()!=null){
                previewProcess.get().cancel();
            }

            iterator.set(chunksListView.getItems().iterator());
            iterator.addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    playbackAllButton.setSelected(false);
                }
            });
            recursivePlayback();

        } else {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        }
    }

    @FXML public void pressDelete() {
        ChunkFileManager.getInstance().delete(chunksListView.getSelectionModel().selectedItemProperty().getValue());
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    @FXML public void pressUp() {
        int index = chunksListView.getSelectionModel().getSelectedIndex();
        Chunk source = chunksListView.getSelectionModel().getSelectedItem();
        Chunk target = chunksListView.getItems().get(index-1);
        ChunkFileManager.getInstance().reorder(source, target);
        chunksListView.getSelectionModel().select(index-1);
    }

    @FXML public void pressDown() {
        int index = chunksListView.getSelectionModel().getSelectedIndex();
        Chunk source = chunksListView.getSelectionModel().getSelectedItem();
        Chunk target = chunksListView.getItems().get(index+1);
        ChunkFileManager.getInstance().reorder(source, target);
        chunksListView.getSelectionModel().select(index+1);
    }

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

    private void alertMessage(String message, CreationProcessEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.CANCEL);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); // Credit to Di Kun Ong (dngo711) for this line
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            listener.handle(event);
        }
    }

    @FXML public void pressNext() {
        if (ChunkFileManager.getInstance().getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please make a snippet to continue");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); // Credit to Di Kun Ong (dngo711) for this line
            alert.showAndWait();
            return;
        }
        listener.handle(new SwitchSceneEvent(this, View.IMAGE_PREVIEW.get()));
    }

    private boolean checkWords(String string) {
        StringTokenizer tokenizer = new StringTokenizer(string);
        if (tokenizer.countTokens() > 40) {
            System.out.println("Popup: more than 40 words");

            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select less than 40 words to synthesize text");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); // Credit to Di Kun Ong (dngo711) for this line
            alert.showAndWait();

            return false;
        }
        return true;
    }

    private void recursivePlayback() {
        if (iterator.get() != null && iterator.get().hasNext()) {
            Chunk chunk = iterator.get().next();

            chunksListView.getSelectionModel().select(chunk);

            File audioFile = ChunkFileManager.getInstance().getFile(chunksListView.getSelectionModel().getSelectedItem());

            playMedia(audioFile);
            mediaPlayer.setOnEndOfMedia(this::recursivePlayback);
        } else {
            playbackAllButton.setSelected(false);
        }
    }

    private void playMedia(File audioFile) {
        Media media = new Media(audioFile.toURI().toString());
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
    }
}
