package views;

import controllers.ImagePreView;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import models.FormManager;
import models.images.ImageFileManager;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;

public class ThumbnailCell extends ListCell<URL> {

    ThumbnailCell() {
        setPrefWidth(0);

        setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Dragboard db = startDragAndDrop(TransferMode.MOVE);

                ClipboardContent content = new ClipboardContent();

                content.putString(getItem().toString());
                db.setContent(content);

                ThumbnailDragboard.getInstance().set(getItem());

                event.consume();
            }
        });

        setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();

                if (event.getGestureSource() != this && db.hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }

                event.consume();
            }
        });

        setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();

                if (event.getGestureSource() != this && db.hasString()) {
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

                if (db.hasString()) {
                    URL source = ThumbnailDragboard.getInstance().get();
                    URL target = getItem();

                    if (target != null) {
                        ObservableList<URL> items = FormManager.getInstance().getImages();
                        items.add(items.indexOf(target), items.remove(items.indexOf(source)));
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
    public void updateItem(URL item, boolean empty) {
        super.updateItem(item, empty);

        ImageView thumbnailImage = new ImageView();

        if (item != null && !empty) {
            try {
                Image image = SwingFXUtils.toFXImage(ImageIO.read(ImageFileManager.getInstance().getFile(item)), null);
                thumbnailImage.setImage(image);
                thumbnailImage.setPreserveRatio(true);
                thumbnailImage.setFitHeight(100);
                thumbnailImage.setFitWidth(195);
            } catch (IOException e) {
                e.printStackTrace();
            }
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }

        setText(null);
        setGraphic(thumbnailImage);
    }



}
