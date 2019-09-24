package views;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import models.Chunk;

public class ChunkCellFactory implements Callback<ListView<Chunk>, ListCell<Chunk>> {
    @Override
    public ListCell<Chunk> call(ListView<Chunk> listView) {
        return new ChunkCell();
    }
}
