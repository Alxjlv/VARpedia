package views;

import javafx.scene.control.ListCell;
import models.creation.Creation;

public class CreationCell extends ListCell<Creation> {
    @Override
    public void updateItem(Creation item, boolean empty) {
        super.updateItem(item, empty);

        String name = null;
        if (item != null && !empty) {
            name = String.format("%s: Rating: %d, Views: %d", item.getName(), item.getConfidenceRating(), item.getViewCount());
        }

        setText(name);
        setGraphic(null); // TODO - Custom fxml - Creation thumbail, name, duration, etc.
    }
}
