package views;

import javafx.event.Event;
import javafx.scene.control.ListCell;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

/**
 * DraggableCell is an abstract class that implements common functionality for {@link ListCell}'s that support
 * drag-and-drop. Note: Extending classes must set {@code setOnDragDetected()} and {@code setOnDragDropped()}
 * @param <T> The type of item contained within the {@link DraggableCell}
 * @author Tait & Alex
 */
public abstract class DraggableCell<T> extends ListCell<T> {

    /**
     * Default constructor sets default handlers for {@code setOnDragOver()}, {@code setOnDragEntered},
     * {@code setOnDragExited} & {@code setOnDragDone}.
     */
    public DraggableCell() {
        super();

        // Prevent horizontal scrollbar in ListView
        setPrefWidth(0);

        setOnDragOver(event -> {
            Dragboard db = event.getDragboard();

            if (event.getGestureSource() != this && db.hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }

            event.consume();
        });

        setOnDragEntered(event -> {
            Dragboard db = event.getDragboard();

            if (event.getGestureSource() != this && db.hasString()) {
                setOpacity(0.3);
            }

            event.consume();
        });

        setOnDragExited(event -> {
            setOpacity(1);
            event.consume();
        });

        setOnDragDone(Event::consume);
    }

    /*
    Update parent ListCell
     */
    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
    }
}
