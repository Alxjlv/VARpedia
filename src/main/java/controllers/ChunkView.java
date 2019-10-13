package controllers;

import constants.View;
import constants.Filename;
import constants.Folder;
import events.SwitchSceneEvent;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import main.ProcessRunner;
import models.chunk.Chunk;
import models.chunk.ChunkBuilder;
import models.chunk.ChunkManager;
import models.synthesizer.*;
import views.ChunkCellFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
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

    private Synthesizer synthesizer;
    private MediaPlayer mediaPlayer;
    private ReadOnlyObjectWrapper<Iterator<Chunk>> iterator = new ReadOnlyObjectWrapper<>();
    private ReadOnlyObjectWrapper<ProcessRunner> previewProcess = new ReadOnlyObjectWrapper<>();

    @FXML
    public void initialize() {
        playbackButton.setDisable(true);
        playbackAllButton.setDisable(true);
        deleteButton.setDisable(true);

        ChunkManager.getInstance();
        chunksListView.setItems(ChunkManager.getInstance().getItems());
        chunksListView.setCellFactory(new ChunkCellFactory());
        ChunkManager.getInstance().getItems().addListener((ListChangeListener<Chunk>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    chunksListView.getSelectionModel().select(c.getFrom());
                }
            }
        });
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
                if (c.getList().isEmpty()) {
                    playbackAllButton.setDisable(true);
                } else {
                    playbackAllButton.setDisable(false);
                }
            }
        });

        // TODO - Load Wikit Result
        try {
            FileReader result = new FileReader(new File(Folder.TEMP.get(), Filename.SEARCH_TEXT.get()));
            String string = "";
            int i;
            while ((i = result.read()) != -1) {
                string = string.concat(Character.toString((char) i));
            }
            string = string.trim();
            searchResult.setText(string);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO - Handle exception
        }

        ObservableList<Synthesizer> voices = FXCollections.observableArrayList();
        for (EspeakSynthesizer.Voice voice: EspeakSynthesizer.Voice.values()) {
            voices.add(new EspeakSynthesizer(voice));
        }
        for (FestivalSynthesizer.Voice voice: FestivalSynthesizer.Voice.values()) {
            voices.add(new FestivalSynthesizer(voice));
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
            ChunkBuilder chunkBuilder = ChunkManager.getInstance().getBuilder();
            chunkBuilder.setText(searchResult.getSelectedText()).setSynthesizer(synthesizer);
            ChunkManager.getInstance().create(chunkBuilder);
        }
    }

    @FXML public void pressPlayback() {
        if (playbackButton.isSelected()) {
            iterator.set(null);
            if(previewProcess.get()!=null){
                previewProcess.get().cancel();
            }

            File audioFile = new File(ChunkManager.getInstance().getChunkFile(chunksListView.getSelectionModel().getSelectedItem()), Filename.CHUNK_AUDIO.get());

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
        ChunkManager.getInstance().delete(chunksListView.getSelectionModel().selectedItemProperty().getValue());
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    @FXML public void pressBack() {
        if (!ChunkManager.getInstance().getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "If you go back your Snippets will not be saved. Do you wish to continue?",
                    ButtonType.YES, ButtonType.CANCEL);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                listener.handle(new SwitchSceneEvent(this, View.SEARCH.get()));
            }
        } else {
            listener.handle(new SwitchSceneEvent(this, View.SEARCH.get()));
        }
    }

    @FXML public void pressNext() {
        if (ChunkManager.getInstance().getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please make a snippet to continue");
            alert.showAndWait();
            return;
        }
        listener.handle(new SwitchSceneEvent(this, View.NAME.get()));
    }

    private boolean checkWords(String string) {
        StringTokenizer tokenizer = new StringTokenizer(string);
        if (tokenizer.countTokens() > 40) {
            System.out.println("Popup: more than 40 words");

            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select less than 40 words to synthesize text");
            alert.showAndWait();

            return false;
        }
        return true;
    }

    private void recursivePlayback() {
        if (iterator.get() != null && iterator.get().hasNext()) {
            Chunk chunk = iterator.get().next();

            chunksListView.getSelectionModel().select(chunk);

            File audioFile = new File(ChunkManager.getInstance().getChunkFile(chunksListView.getSelectionModel().getSelectedItem()), Filename.CHUNK_AUDIO.get());

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
