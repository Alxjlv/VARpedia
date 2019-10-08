package controllers;

import constants.FileExtension;
import constants.FolderPath;
import events.SwitchSceneEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import models.*;
import views.ChunkCellFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.StringTokenizer;

public class ChunkView extends Controller {

    @FXML ListView<Chunk> chunksListView;

    @FXML TextArea searchResult;

    @FXML ChoiceBox synthesizerDropdown;
    @FXML ChoiceBox voiceDropdown;

    private Synthesizer synthesizer;

    private Iterator<Chunk> iterator;

    private MediaPlayer mediaPlayer;

    @FXML
    public void initialize() {
        iterator = null;
        mediaPlayer = null;

        ChunkManager.getInstance();
        chunksListView.setItems(ChunkManager.getInstance().getItems());
        chunksListView.setCellFactory(new ChunkCellFactory());
        ChunkManager.getInstance().getItems().addListener(new ListChangeListener<Chunk>() {
            @Override
            public void onChanged(Change<? extends Chunk> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        chunksListView.getSelectionModel().select(c.getFrom());
                    }
                }
            }
        });

        // TODO - Load Wikit Result
        try {
            FileReader result = new FileReader(new File(FolderPath.TEMP_FOLDER.getPath(), FileExtension.SEARCH_TEXT.getExtension()));
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

        synthesizerDropdown.getItems().setAll("Espeak", "Festival");
        synthesizerDropdown.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            private ChangeListener espeakListener = new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    synthesizer = new EspeakSynthesizerBuilder().setVoice((EspeakSynthesizer.Voice) newValue).build();
                }
            };

            private ChangeListener festivalListener = new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    synthesizer = new FestivalSynthesizerBuilder().setVoice((FestivalSynthesizer.Voice) newValue).build();
                }
            };

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue.equals("Espeak")) {
                    voiceDropdown.getItems().setAll(Arrays.asList(EspeakSynthesizer.Voice.values()));
                    voiceDropdown.getSelectionModel().selectedItemProperty().removeListener(festivalListener);
                    voiceDropdown.getSelectionModel().selectedItemProperty().addListener(espeakListener);
                    voiceDropdown.getSelectionModel().select(EspeakSynthesizer.Voice.DEFAULT);
                } else if (newValue.equals("Festival")) {
                    voiceDropdown.getItems().setAll(Arrays.asList(FestivalSynthesizer.Voice.values()));
                    voiceDropdown.getSelectionModel().selectedItemProperty().removeListener(espeakListener);
                    voiceDropdown.getSelectionModel().selectedItemProperty().addListener(festivalListener);
                    voiceDropdown.getSelectionModel().select(FestivalSynthesizer.Voice.KAL);
                }
            }
        });
        synthesizerDropdown.getSelectionModel().select("Espeak");
    }

    @FXML public void pressBack() {
        if (!ChunkManager.getInstance().getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    String.format("If you go back your Snippets will not be saved. Do you wish to continue?"),
                    ButtonType.YES, ButtonType.CANCEL);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                listener.handle(new SwitchSceneEvent(this, "/SearchView.fxml"));
            }
        } else {
            listener.handle(new SwitchSceneEvent(this, "/SearchView.fxml"));
        }
    }
  
    @FXML public void pressPreview() {
        if (checkWords(searchResult.getSelectedText())) {
            // TODO - Word count validation (20-40)
            // TODO - add ability to stop preview (e.g. preview button becomes cancel/stop button)
            synthesizer.preview(searchResult.getSelectedText());
        }
    }

    @FXML public void pressSaveSnippet() {
        if (checkWords(searchResult.getSelectedText())) {
            // TODO - Word count validation (20-40)
            ChunkBuilder chunkBuilder = ChunkManager.getInstance().getBuilder();
            chunkBuilder.setText(searchResult.getSelectedText()).setSynthesizer(synthesizer);
            ChunkManager.getInstance().create(chunkBuilder);
        }
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

    @FXML public void pressPlayback() {
        iterator = chunksListView.getItems().iterator();
        recursivePlayback();
    }

    private void recursivePlayback() {
        if (iterator.hasNext()) {
            Chunk chunk = iterator.next();

            chunksListView.getSelectionModel().select(chunk);

            File audioFile = new File(chunksListView.getSelectionModel().getSelectedItem().getFolder(), FileExtension.CHUNK_AUDIO.getExtension());
            Media media = new Media(audioFile.toURI().toString());

            mediaPlayer.stop();
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setAutoPlay(true);
            mediaPlayer.setOnEndOfMedia(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Ended?");
                    recursivePlayback();
                }
            });
        }
    }

    @FXML public void pressPreviewSnippet() {
        // TODO - add ability to stop preview (e.g. preview button becomes cancel/stop button, click on a different snippet)
        File audioFile = new File(chunksListView.getSelectionModel().getSelectedItem().getFolder(), "audio.wav");
        Media media = new Media(audioFile.toURI().toString());
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
    }

    @FXML public void pressDelete() {
        ChunkManager.getInstance().delete(chunksListView.getSelectionModel().selectedItemProperty().getValue());
    }

    @FXML public void pressNext() {
        if (ChunkManager.getInstance().getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please make a snippet to continue");
            alert.showAndWait();
            return;
        }
        listener.handle(new SwitchSceneEvent(this, "/NameView.fxml"));
    }
}
