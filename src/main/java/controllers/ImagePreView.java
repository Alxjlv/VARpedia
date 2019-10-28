package controllers;

import constants.View;
import events.CreationProcessEvent;
import events.SwitchSceneEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import models.FormManager;
import models.images.ImageFileManager;
import views.ThumbnailCell;

import java.io.File;
import java.net.URL;
import java.util.Collections;

/**
 * The ImagePreView class is responsible for displaying a list of 10 images and allowing the user to delete & rearrange
 * the images
 * @author Tait & Alex
 */
public class ImagePreView extends Controller {

    @FXML private Pane imagePane; // A pane responsible for displaying the image
    @FXML private VBox parentBox; // A VBox that the imagePane is within
    @FXML private ListView<URL> imageListView; // ListView of images/URLs

    // Buttons in the scene
    @FXML private Button upButton;
    @FXML private Button downButton;
    @FXML private Button deleteButton;

    private ObservableList<URL> images; // The list of image URLs which is able to be stored in the ImageFileManager

    // Both of these are used for scaling the imagePane
    private double width; // Width of the parent resizable VBox
    private double height; // Height of the parent resizable VBox

    private File loadedImage; // The image to be loaded into the imagePane

    /**
     * This method starts up when the FXML is loaded, and loads the list of images into the scene
     */
    @FXML public void initialize() {
        images = FormManager.getInstance().getImages(); // Retrieving the downloaded images from the FormManager

        // Getting the initial height of the parentBox
        width = parentBox.getWidth();
        height = parentBox.getHeight();

        // Displaying the images in the list
        imageListView.setItems(images);
        imageListView.setCellFactory(param -> new ThumbnailCell()); // Setting the thumbnail cell factory

        // Loading a new image if the selected image changes
        imageListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            loadedImage=ImageFileManager.getInstance().getFile(newValue);
            loadImage(loadedImage,width,height);
        });

        // Disables the Up/Down button depending on the location of the selected image (eg. at the top Up is diasabled)
        imageListView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.intValue() == 0){
                upButton.setDisable(true);
            } else {
                upButton.setDisable(false);
            }
            if (newValue.intValue() == images.size()-1){
                downButton.setDisable(true);
            } else {
                downButton.setDisable(false);
            }
        });

        // Disabling the buttons when the list is empty
        imageListView.getItems().addListener((ListChangeListener<URL>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    imageListView.getSelectionModel().select(c.getFrom());
                }
                if (c.getList().size() <= 1) {
                    deleteButton.setDisable(true);
                    upButton.setDisable(true);
                    downButton.setDisable(true);
                } else {
                    deleteButton.setDisable(false);
                }
            }
        });
        //Selecting the first item in the list by default
        imageListView.getSelectionModel().select(0);

        // Listening for changes in the size of the stage & resizing the image to match
        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
            width = parentBox.getWidth();
            height = parentBox.getHeight();
            imagePane.setBackground(null);
            loadImage(loadedImage,width,height);
        };
        parentBox.widthProperty().addListener(stageSizeListener);
        parentBox.heightProperty().addListener(stageSizeListener);

    }

    /**
     * This method loads a background image into a pane, showing the user what the selected image will look like
     * @param imageFile - the file of the image to load
     * @param width - the width we want the image to be
     * @param height - the height we want the image to be
     */
    @FXML private void loadImage(File imageFile, double width, double height) {
        BackgroundImage myBI;
        Image image = new Image("file:"+imageFile.getPath(), width, height, true, true);
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        myBI = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        imagePane.setBackground(new Background(myBI));
    }

    /**
     * Switches back to the chunk scene
     */
    @FXML public void pressBack(){
        listener.handle(new SwitchSceneEvent(this,View.CHUNK.get()));
    }

    /**
     * Asks the user if they're sure that they want to cancel by throwing a popup, and switching to the welcome view if
     * they confirm
     */
    @FXML public void pressCancel() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                String.format("If you cancel your Snippets & selected images will not be saved. Do you wish to continue?"),
                ButtonType.YES, ButtonType.CANCEL);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); // Credit to Di Kun Ong (dngo711) for this line
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            listener.handle(new CreationProcessEvent(this, CreationProcessEvent.Status.CANCEL_CREATE));
        }
    }

    /**
     * Moves to the name view scene
     */
    @FXML public void pressNext(){
        listener.handle(new SwitchSceneEvent(this, View.NAME.get()));
    }

    /**
     * Swaps the selected image with the image above it
     */
    @FXML public void pressUp() {
        int index = imageListView.getSelectionModel().getSelectedIndex();
        Collections.swap(images,index,index-1);
        imageListView.getSelectionModel().select(index-1);
    }

    /**
     * Swaps the selected image with the image below it
     */
    @FXML public void pressDown() {
        int index = imageListView.getSelectionModel().getSelectedIndex();
        Collections.swap(images,index,index+1);
        imageListView.getSelectionModel().select(index+1);
    }

    /**
     * Deleted the selected image
     */
    @FXML public void pressDelete() {
        FormManager.getInstance().getImages().remove(imageListView.getSelectionModel().getSelectedItem());
    }
}
