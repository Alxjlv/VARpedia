package controllers;

import events.SwitchSceneEvent;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import main.ChunkCellFactory;
import models.Chunk;
import models.ChunkManager;

public class SnippetView extends Controller{

    @FXML ListView<Chunk> chunksListView;

    private SortedList<Chunk> sortedChunks;

    @FXML
    public void initialize(){
        ChunkManager.getInstance().load();
        sortedChunks = ChunkManager.getInstance().getItems().sorted();
        chunksListView.setItems(sortedChunks);
        chunksListView.setCellFactory(new ChunkCellFactory());
    }

    @FXML public void pressBack(){
        listener.handle(new SwitchSceneEvent(this, "/CreateView.fxml"));
    }

    @FXML public void pressSpeech(){
        //TODO - alert box for speech settings
    }

    @FXML public void pressPreview(){
        //TODO - add preview logic
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
