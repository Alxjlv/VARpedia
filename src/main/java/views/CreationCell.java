package views;

import constants.View;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import models.creation.Creation;

import javax.imageio.ImageIO;
import java.io.IOException;

public class CreationCell extends ListCell<Creation> {
    @FXML
    private Label name;

    @FXML
    private Label viewCount;

    @FXML
    private Label confidenceRating;

    @FXML
    private ImageView thumbnail;

    public CreationCell() {
        try {
            FXMLLoader loader = new FXMLLoader(View.CREATION_CELL.get());
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateItem(Creation item, boolean empty) {
        super.updateItem(item, empty);

        if (item != null && !empty) {

            Image image = new Image("file:"+item.getThumbnailFile().getPath());
            thumbnail.setImage(image);
            thumbnail.setPreserveRatio(true);
            thumbnail.setFitHeight(100);
            thumbnail.setFitWidth(80);

            name.setText(item.getName());
            viewCount.setText(Integer.toString(item.getViewCount()));
            confidenceRating.setText(Integer.toString(item.getConfidenceRating()));

            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        } else {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
    }
}
