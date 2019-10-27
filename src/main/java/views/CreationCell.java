package views;

import constants.View;
import controllers.AdaptivePanel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import models.creation.Creation;
import models.creation.CreationComparators;
import models.creation.CreationFileManager;

import java.io.IOException;
import java.util.Comparator;

/**
 * ChunkCell implements a {@link ListCell<Creation>} for {@link Creation} objects to display in a
 * {@link javafx.scene.control.ListView<Creation>} using a custom FXML layout.
 * @author Tait & Alex
 */
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
        super();

        // Load custom FXML layout
        try {
            FXMLLoader loader = new FXMLLoader(View.CREATION_CELL.get());
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    Update CreationCell to display the provided Creation item
     */
    @Override
    public void updateItem(Creation item, boolean empty) {
        super.updateItem(item, empty);

        if (item != null && !empty) {
            // Disable this ListCell if AdaptivePanel is sorting by CreationComparators.TO_REVIEW
            setDisable(false);
            AdaptivePanel.selectedComparatorProperty().addListener(new ChangeListener<Comparator<Creation>>() {
                @Override
                public void changed(ObservableValue<? extends Comparator<Creation>> observable, Comparator<Creation> oldValue, Comparator<Creation> newValue) {
                    if (newValue == CreationComparators.TO_REVIEW && item.getConfidenceRating() > 3) {
                        setDisable(true);
                    } else {
                        setDisable(false);
                    }
                }
            });
            if (AdaptivePanel.getSelectedComparator() == CreationComparators.TO_REVIEW && item.getConfidenceRating() > 3) {
                setDisable(true);
            }

            // Load thumbnail
            Image image = new Image("file:"+ CreationFileManager.getInstance().getThumbnailFile(item).getPath());
            thumbnail.setImage(image);
            thumbnail.setPreserveRatio(true);
            thumbnail.setFitHeight(100);
            thumbnail.setFitWidth(80);

            // Set text fields
            name.setText(item.getName());
            viewCount.setText(Integer.toString(item.getViewCount()));
            if (item.getConfidenceRating() == 0) {
                confidenceRating.setText("-");
            } else {
                confidenceRating.setText(Integer.toString(item.getConfidenceRating()));
            }

            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        } else {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
    }
}
