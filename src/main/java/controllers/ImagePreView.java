package controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import models.FormManager;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ImagePreView extends Controller{

    @FXML Pane imagePane;
    @FXML VBox parentBox;
    @FXML GridPane imagePreView;
    @FXML
    ListView<String> imageList;

    private Map<String, File> testmap;
    private ObservableList<String> observableList;
    private double width;
    private double height;
    private String loadedImage = "testImage.jpg";

    @FXML public void initialize() {
        width = parentBox.getWidth();
        height = parentBox.getHeight();

        Map<URL,File> imageMap = FormManager.getInstance().getCurrentDownloader().getImageList();

        testmap = new LinkedHashMap<>();
        for(int i=1;i<=10;i++){
            testmap.put(i+".jpg",new File(i+".jpg"));
        }

        observableList = FXCollections.observableArrayList(testmap.keySet());
        observableList.addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                System.out.println("changed");
            }
        });
        imageList.setItems(observableList);
        imageList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                loadedImage=newValue;
                loadImage(loadedImage,width,height);
            }
        });

        loadImage(loadedImage,width,height);
        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
            width = parentBox.getWidth();
            height = parentBox.getHeight();
            imagePane.setBackground(null);
            loadImage(loadedImage,width,height);
        };
        parentBox.widthProperty().addListener(stageSizeListener);
        parentBox.heightProperty().addListener(stageSizeListener);

    }

    @FXML public void loadImage(String image, double width, double height){
        BackgroundImage myBI= new BackgroundImage(new Image(image,width,height,true,true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        imagePane.setBackground(new Background(myBI));
    }

}
