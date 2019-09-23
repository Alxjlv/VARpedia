package controllers;

import events.SwitchSceneEvent;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import main.ChunkCellFactory;
import models.Chunk;
import models.ChunkManager;
import models.Synthesizer;

public class SnippetView extends Controller{

    @FXML ListView<Chunk> chunksListView;

    @FXML
    TextArea searchResult;

    @FXML
    public void initialize(){
        ChunkManager.getInstance().load();
        chunksListView.setItems(ChunkManager.getInstance().getItems());
        chunksListView.setCellFactory(new ChunkCellFactory());
        ChunkManager.getInstance().getItems().addListener(new ListChangeListener<Chunk>() {
            @Override
            public void onChanged(Change<? extends Chunk> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        chunksListView.getSelectionModel().select(c.getFrom());
                    }
                    System.out.println(c);
                }
            }
        });

        searchResult.setText("An apple is a sweet, edible fruit produced by an apple tree (Malus domestica). Apple " +
                "trees are cultivated worldwide and are the most widely grown species in the genus Malus. The tree " +
                "originated in Central Asia, where its wild ancestor, Malus sieversii, is still found today. Apples " +
                "have been grown for thousands of years in Asia and Europe and were brought to North America by " +
                "European colonists. Apples have religious and mythological significance in many cultures, including" +
                " Norse, Greek and European Christian traditions.");
    }

    @FXML public void pressBack(){
        listener.handle(new SwitchSceneEvent(this, "/CreateView.fxml"));
    }

    @FXML public void pressSpeech(){
        //TODO - alert box for speech settings
    }

    @FXML public void pressPreview(){
        //TODO - add preview logic
        Synthesizer synthesizer = new Synthesizer();
        synthesizer.preview(searchResult.getSelectedText());
    }

    @FXML public void pressSaveSnippet(){
        //TODO - logic for adding to list
    }

    @FXML public void pressPlayback(){
        //TODO - logic for combining and playing snips
    }

    @FXML public void pressPreviewSnippet(){
        //TODO - logic for previewing that snippet from there
    }

    @FXML public void pressDelete(){
        //TODO - delete logic
    }

    @FXML public void pressNext(){
        listener.handle(new SwitchSceneEvent(this,"/NameView.fxml"));
    }
}
