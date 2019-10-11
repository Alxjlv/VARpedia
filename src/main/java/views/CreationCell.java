package views;

import javafx.scene.control.ListCell;
import javafx.util.Duration;
import models.creation.Creation;

public class CreationCell extends ListCell<Creation> {
    @Override
    public void updateItem(Creation item, boolean empty) {
        super.updateItem(item, empty);

        String name = null;
        if (item != null && !empty) {
            Duration duration = item.getDuration();
            name = String.format("%s: Rating: %d, Views: %d, Time: %s",
                    item.getName(),
                    item.getConfidenceRating(),
                    item.getViewCount(),
                    duration == null ? "0:00" : duration.toString());
        }

        setText(name);
        setGraphic(null); // TODO - Custom fxml - Creation thumbail, name, duration, etc.
    }
}
