package views;

import javafx.collections.ObservableList;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import models.FormManager;
import models.images.ImageFileManager;

import java.net.URL;

/**
 * ThumbnailCell extends {@link DraggableCell} to display images of type {@link URL} in a
 * {@link javafx.scene.control.ListView}. These cells support drag-and-drop using {@link FormManager} to reorder images.
 * @author Tait & Alex
 */
public class ThumbnailCell extends DraggableCell<URL> {
    /*
    Setup required drag event handlers
     */
    public ThumbnailCell() {
        super();

        setOnDragDetected(event -> {
            Dragboard db = startDragAndDrop(TransferMode.MOVE);

            ClipboardContent content = new ClipboardContent();

            content.putString(getItem().toString());
            db.setContent(content);

            ThumbnailDragboard.getInstance().set(getItem());

            event.consume();
        });

        setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                URL source = ThumbnailDragboard.getInstance().get();
                URL target = getItem();

                if (target != null) {
                    ObservableList<URL> items = FormManager.getInstance().getImages();
                    items.add(items.indexOf(target), items.remove(items.indexOf(source)));
                    success = true;
                }
            }
            event.setDropCompleted(success);

            event.consume();
        });
    }

    /*
    Update ThumbnailCell to display the provided URL item
     */
    @Override
    public void updateItem(URL item, boolean empty) {
        super.updateItem(item, empty);

        // Load and display the image
        ImageView thumbnailImage = new ImageView();
        if (item != null && !empty) {
            Image image = new Image("file:"+ ImageFileManager.getInstance().getFile(item).getPath());
            thumbnailImage.setImage(image);
            thumbnailImage.setPreserveRatio(true);
            thumbnailImage.setFitHeight(135);
            thumbnailImage.setFitWidth(240);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }

        setText(null);
        setGraphic(thumbnailImage);
    }



}
