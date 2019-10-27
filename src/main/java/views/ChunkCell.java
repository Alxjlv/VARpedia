package views;

import javafx.event.EventHandler;
import javafx.scene.control.ListCell;
import javafx.scene.input.*;
import javafx.scene.text.Font;
import models.chunk.Chunk;
import models.chunk.ChunkFileManager;

public class ChunkCell extends DraggableCell<Chunk> {
    public ChunkCell() {
        setFont(new Font(18));

        setOnDragDetected(event -> {
            Dragboard db = startDragAndDrop(TransferMode.MOVE);

            ClipboardContent content = new ClipboardContent();
            content.putString(getItem().getText());
            db.setContent(content);

            ChunkDragboard.getInstance().set(getItem());

            event.consume();
        });

        setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                Chunk source = ChunkDragboard.getInstance().get();
                Chunk target = getItem();

                if (target != null) {
                    ChunkFileManager.getInstance().reorder(source, target);
                    success = true;
                }
            }
            event.setDropCompleted(success);

            event.consume();
        });
    }

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
