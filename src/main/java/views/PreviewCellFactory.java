package views;

import javafx.scene.control.ListView;
import javafx.util.Callback;
import models.images.Thumbnail;

public class PreviewCellFactory implements Callback<ListView<Thumbnail>, ThumbnailCell> {

    @Override
    public ThumbnailCell call(ListView<Thumbnail> listView) {
        return new ThumbnailCell();
    }
}
