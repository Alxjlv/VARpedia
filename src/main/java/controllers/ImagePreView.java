package controllers;

import constants.View;
import events.CreationProcessEvent;
import events.SwitchSceneEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import models.FormManager;
import models.images.ImageFileManager;
import views.ThumbnailCellFactory;

import java.io.File;
import java.net.URL;
import java.util.Collections;

public class ImagePreView extends Controller{

    @FXML Pane imagePane;
    @FXML VBox parentBox;
    @FXML GridPane imagePreView;
    @FXML ListView<URL> imageListView;
    @FXML Button upButton;
    @FXML Button downButton;


    private ObservableList<URL> images;
    private double width;
    private double height;
    private File loadedImage;

    /**
     * This method starts up when the FXML is loaded, and loads the list of images into the scene
     */
    @FXML public void initialize() {
        FormManager formManager = FormManager.getInstance();
        formManager.setImages(FXCollections.observableArrayList(formManager.getImages().subList(0,10)));
        images = formManager.getImages();
        width = parentBox.getWidth();
        height = parentBox.getHeight();

        imageListView.setItems(images);
        imageListView.setCellFactory(new ThumbnailCellFactory());
        imageListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<URL>() {
            @Override
            public void changed(ObservableValue<? extends URL> observable, URL oldValue, URL newValue) {
                loadedImage=ImageFileManager.getInstance().getFile(newValue);
                loadImage(loadedImage,width,height);
            }
        });
        imageListView.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
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
            }
        });
        //Selecting the first item in the list
        imageListView.getSelectionModel().select(0);

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
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); // Credit to Di Kun Ong (dngo711) for this line
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
        int index = imageListView.getSelectionModel().getSelectedIndex();
        Collections.swap(images,index,index-1);
        imageListView.getSelectionModel().select(index-1);
    }

    @FXML public void pressDown(){
        //TODO
        int index = imageListView.getSelectionModel().getSelectedIndex();
        Collections.swap(images,index,index+1);
        imageListView.getSelectionModel().select(index+1);
    }

    @FXML public void pressDelete(){
        FormManager.getInstance().getImages().remove(imageListView.getSelectionModel().getSelectedItem());
    }
}
