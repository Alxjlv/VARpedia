package main;

import javafx.scene.control.ListCell;
import models.Creation;

public class CreationCell extends ListCell<Creation> {
    @Override
    public void updateItem(Creation item, boolean empty) {
        super.updateItem(item, empty);

        String name = null;
        if (item != null && !empty) {
            name = item.getName();
        }

        setText(name);
        setGraphic(null); // TODO - Thumbnails?
    }
}
