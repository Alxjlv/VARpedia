package views;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import models.creation.Creation;

public class CreationCellFactory implements Callback<ListView<Creation>, ListCell<Creation>> {
    @Override
    public ListCell<Creation> call(ListView<Creation> listView) {
        return new CreationCell();
    }
}
