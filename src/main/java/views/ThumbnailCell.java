package views;

import constants.View;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.io.IOException;

public class ThumbnailCell {


    @FXML ImageView thumbnailImage;
    @FXML Text imageNumber; //intended to show the order of the images


    ThumbnailCell() {
        try {
            FXMLLoader loader = new FXMLLoader(View.THUMBNAIL_CELL.get());
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void updateItem(Thumbnail item, boolean empty) {
//        super.updateItem(item, empty);
//
////        String name = null;
//        if (item != null && !empty) {
//            try {
//                Image image = SwingFXUtils.toFXImage(ImageIO.read(item.getThumbnialFile()), null);
//                thumbnailImage.setImage(image);
//                thumbnailImage.setPreserveRatio(true);
//                thumbnailImage.setFitHeight(100);
//                thumbnailImage.setFitWidth(80);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//
//            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
//        } else {
//            setText(null);
//            setContentDisplay(ContentDisplay.TEXT_ONLY);
//        }

//        setText(null);
//        setGraphic(value); // TODO - Custom fxml - Creation thumbail, name, duration, etc.
//    }



}
