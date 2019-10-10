package views;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import models.chunk.Chunk;

public class ChunkCellFactory implements Callback<ListView<Chunk>, ListCell<Chunk>> {
    @Override
    public ListCell<Chunk> call(ListView<Chunk> listView) {
        return new ChunkCell();
    }
}
