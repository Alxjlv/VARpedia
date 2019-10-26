package views;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.net.URL;

public class ThumbnailCellFactory implements Callback<ListView<URL>, ListCell<URL>> {

    @Override
    public ThumbnailCell call(ListView<URL> listView) {
        return new ThumbnailCell();
    }
}
