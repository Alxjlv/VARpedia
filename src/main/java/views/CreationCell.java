package views;

import constants.View;
import javafx.embed.swing.SwingFXUtils;
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
//        prefWidth(0);

        try {
            FXMLLoader loader = new FXMLLoader(View.CREATION_CELL.get());
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
//            value.setPrefWidth(getListView().getWidth());
//            System.out.println(value.getWidth());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateItem(Creation item, boolean empty) {
        super.updateItem(item, empty);

//        String name = null;
        if (item != null && !empty) {
            try {
                Image image = SwingFXUtils.toFXImage(ImageIO.read(item.getThumbnailFile()), null);
                thumbnail.setImage(image);
                thumbnail.setPreserveRatio(true);
                thumbnail.setFitHeight(100);
                thumbnail.setFitWidth(80);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            InputStream imageStream = CreationCell.class.getResourceAsStream("/image.jpg");
//            System.out.println(imageStream);
//            Image image = new Image(imageStream);

            name.setText(item.getName());
            viewCount.setText(Integer.toString(item.getViewCount()));
            confidenceRating.setText(Integer.toString(item.getConfidenceRating()));

            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        } else {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }

//        setText(null);
//        setGraphic(value); // TODO - Custom fxml - Creation thumbail, name, duration, etc.
    }
}
