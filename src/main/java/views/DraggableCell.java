package views;

import javafx.event.Event;
import javafx.scene.control.ListCell;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public abstract class DraggableCell<T> extends ListCell<T> {

    public DraggableCell() {
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

    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
    }
}
