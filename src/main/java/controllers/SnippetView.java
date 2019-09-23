package controllers;

import events.SwitchSceneEvent;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import main.ChunkCellFactory;
import models.Chunk;
import models.ChunkManager;

public class SnippetView extends Controller{

    @FXML ListView<Chunk> chunksListView;

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
