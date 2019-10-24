package views;

import controllers.ImagePreView;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;

public class ThumbnailCell extends ListCell<URL> {

    ThumbnailCell() {
        setPrefWidth(0);
    }

    @Override
    public void updateItem(URL item, boolean empty) {
        super.updateItem(item, empty);

        ImageView thumbnailImage = new ImageView();

//        String name = null;
        if (item != null && !empty) {
            try {
                Image image = SwingFXUtils.toFXImage(ImageIO.read(ImagePreView.dummyData.get(item)), null);
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
        setGraphic(thumbnailImage); // TODO - Custom fxml - Creation thumbail, name, duration, etc.
    }



}
