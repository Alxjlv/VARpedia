package controllers;

import events.SwitchSceneEvent;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import models.*;
import views.ChunkCellFactory;

public class SnippetView extends Controller {

    @FXML ListView<Chunk> chunksListView;

    @FXML TextArea searchResult;

    private Synthesizer synthesizer;

    @FXML
    public void initialize() {
        ChunkManager.getInstance(); // TODO - This will clear chunks if user goes back and returns
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

        synthesizer = new EspeakSynthesizerBuilder().setVoice(EspeakSynthesizer.Voice.DEFAULT).build();
        // TODO - Load Wikit Result
        searchResult.setText("An apple is a sweet, edible fruit produced by an apple tree (Malus domestica). Apple " +
                "trees are cultivated worldwide and are the most widely grown species in the genus Malus. The tree " +
                "originated in Central Asia, where its wild ancestor, Malus sieversii, is still found today. Apples " +
                "have been grown for thousands of years in Asia and Europe and were brought to North America by " +
                "European colonists. Apples have religious and mythological significance in many cultures, including" +
                " Norse, Greek and European Christian traditions.");
    }

    @FXML public void pressBack() {
        listener.handle(new SwitchSceneEvent(this, "/CreateView.fxml"));
        // TODO - Save TextArea searchResult?
    }

    @FXML public void pressSpeech() {
        // TODO - popup box for speech settings
    }

    @FXML public void pressPreview() {
        // TODO - Word count validation (20-40)
        // TODO - add ability to stop preview (e.g. preview button becomes cancel/stop button)
        synthesizer.preview(searchResult.getSelectedText());
    }

    @FXML public void pressSaveSnippet() {
        // TODO - Word count validation (20-40)
        ChunkBuilder chunkBuilder = ChunkManager.getInstance().getBuilder();
        chunkBuilder.setText(searchResult.getSelectedText()).setSynthesizer(synthesizer);
        ChunkManager.getInstance().create(chunkBuilder);
    }

    @FXML public void pressPlayback() {
        // TODO - Turn this into Task and make synthesizer.preview() block while running allowing sequential (not simultaneous) playback
        for (Chunk chunk: chunksListView.getItems()) {
            chunksListView.getSelectionModel().select(chunk);
            synthesizer.preview(chunk.getText());
        }
    }

    @FXML public void pressPreviewSnippet() {
        // TODO - add ability to stop preview (e.g. preview button becomes cancel/stop button, click on a different snippet)
        synthesizer.preview(chunksListView.getSelectionModel().selectedItemProperty().getValue().getText());
    }

    @FXML public void pressDelete() {
        // TODO - Display alert?
        ChunkManager.getInstance().delete(chunksListView.getSelectionModel().selectedItemProperty().getValue());
    }

    @FXML public void pressNext() {
        listener.handle(new SwitchSceneEvent(this,"/NameView.fxml"));
    }
}
