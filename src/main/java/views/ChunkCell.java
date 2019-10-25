package views;

import javafx.event.EventHandler;
import javafx.scene.control.ListCell;
import javafx.scene.input.*;
import javafx.scene.text.Font;
import models.chunk.Chunk;
import models.chunk.ChunkFileManager;

public class ChunkCell extends ListCell<Chunk> {
    public ChunkCell() {
        setPrefWidth(0);
        setFont(new Font(18));

        setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Dragboard db = startDragAndDrop(TransferMode.MOVE);

                ClipboardContent content = new ClipboardContent();
                content.putString(getItem().getText());
                db.setContent(content);

                ChunkDragboard.getInstance().set(getItem());

                event.consume();
            }
        });

        setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();

                if (event.getGestureSource() != this && db.hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }

                event.consume();
            }
        });

        setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();

                if (event.getGestureSource() != this && db.hasString()) {
                    setOpacity(0.3);
                }

                event.consume();
            }
        });

        setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                setOpacity(1);

                event.consume();
            }
        });

        setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
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
            }
        });

        setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.consume();
            }
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
