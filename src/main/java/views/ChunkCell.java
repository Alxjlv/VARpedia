package views;

import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.text.Font;
import models.chunk.Chunk;
import models.chunk.ChunkFileManager;

/**
 * ChunkCell extends {@link DraggableCell} to display {@link Chunk} objects in a
 * {@link javafx.scene.control.ListView}. These cells support drag-and-drop using {@link ChunkFileManager} to
 * reorder Chunks.
 * @author Tait & Alex
 */
public class ChunkCell extends DraggableCell<Chunk> {
    /*
    Setup required drag event handlers
     */
    public ChunkCell() {
        super();

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

    /*
    Update ChunkCell to display the provided Chunk item
     */
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
