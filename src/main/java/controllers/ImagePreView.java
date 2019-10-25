package controllers;

import constants.View;
import events.CreationProcessEvent;
import events.SwitchSceneEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import models.images.ImageFileManager;
import views.ThumbnailCellFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class ImagePreView extends Controller{

    @FXML Pane imagePane;
    @FXML VBox parentBox;
    @FXML GridPane imagePreView;
    @FXML
    ListView<URL> imageList;

    private double width;
    private double height;
    private File loadedImage;

    /**
     * This method starts up when the FXML is loaded, and loads the list of images into the scene
     */
    @FXML public void initialize() {
        ImageFileManager imageFileManager = ImageFileManager.getInstance();
        width = parentBox.getWidth();
        height = parentBox.getHeight();

        imageList.setItems(imageFileManager.getItems());
        imageList.setCellFactory(new ThumbnailCellFactory());
        imageList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<URL>() {
            @Override
            public void changed(ObservableValue<? extends URL> observable, URL oldValue, URL newValue) {
                loadedImage=imageFileManager.getFile(newValue);
                loadImage(loadedImage,width,height);
            }
        });
        //Selecting the first item in the list
        imageList.getSelectionModel().select(0);

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
    @FXML private void loadImage(File imageFile, double width, double height){
        BackgroundImage myBI;
        Image image = new Image("file:"+imageFile.getPath(), width, height, true, true);
        myBI = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        imagePane.setBackground(new Background(myBI));
    }

    @FXML public void pressBack(){
        listener.handle(new SwitchSceneEvent(this,View.CHUNK.get()));
    }

    @FXML public void pressCancel(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                String.format("If you cancel your Snippets & selected images will not be saved. Do you wish to continue?"),
                ButtonType.YES, ButtonType.CANCEL);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            listener.handle(new CreationProcessEvent(this, CreationProcessEvent.Status.CANCEL));
        }
    }

    @FXML public void pressNext(){
        listener.handle(new SwitchSceneEvent(this, View.NAME.get()));
    }

    @FXML public void pressUp(){
        //TODO
    }

    @FXML public void pressDown(){
        //TODO
    }

    @FXML public void pressDelete(){
        //TODO
    }
}
