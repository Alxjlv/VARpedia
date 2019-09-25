package views;

import javafx.event.EventHandler;
import javafx.scene.control.ListCell;
import javafx.scene.input.*;
import models.Chunk;
import models.ChunkManager;

import java.io.File;
import java.util.ArrayList;

public class ChunkCell extends ListCell<Chunk> {
    public ChunkCell() {
        setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Dragboard db = startDragAndDrop(TransferMode.MOVE);

                ClipboardContent content = new ClipboardContent();
                content.putString(getItem().getText());
                ArrayList<File> files = new ArrayList();
                files.add(getItem().getAudioFile());
                content.putFiles(files);
                db.setContent(content);

                event.consume();
            }
        });

        setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();

                if (event.getGestureSource() != this && db.hasString() && db.hasFiles()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }

                event.consume();
            }
        });

        setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();

                if (event.getGestureSource() != this && db.hasString() && db.hasFiles()) {
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

                if (db.hasString() && db.hasFiles()) {
                    Chunk source = new Chunk(db.getString(), db.getFiles().get(0));
                    Chunk target = getItem();

                    System.out.println("Source: "+source);
                    System.out.println("Target: "+target);
                    if (target != null) {
                        ChunkManager.getInstance().reorder(source, target);

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
