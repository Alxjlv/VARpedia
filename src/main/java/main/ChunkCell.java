package main;

import javafx.scene.control.ListCell;
import models.Chunk;

public class ChunkCell extends ListCell<Chunk> {
    @Override
    public void updateItem(Chunk item, boolean empty) {
        super.updateItem(item, empty);

        String text = null;
        if (item != null && !empty) {
            text = item.getText();
        }

        setText(text);
        setGraphic(null);
    }
}
