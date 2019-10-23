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
import java.net.MalformedURLException;
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
    private Map<URL,File> dummyData = new LinkedHashMap<>();
    private ObservableList<String> observableList;
    private double width;
    private double height;
    private String loadedImage = "testImage.jpg";

    {
        try{
            dummyData.put(new URL("https://live.staticflickr.com/2693/4174974076_d86e881405.jpg"),new File("1.jpg"));
            dummyData.put(new URL("https://live.staticflickr.com/5241/5293987598_b5295a528f.jpg"),new File("2.jpg"));
            dummyData.put(new URL("https://live.staticflickr.com/6039/6334478661_6e1cab8050.jpg"),new File("3.jpg"));
            dummyData.put(new URL("https://live.staticflickr.com/8113/8629172660_eeb91256f8.jpg"),new File("4.jpg"));
            dummyData.put(new URL("https://live.staticflickr.com/8681/15856160050_2125e3965b.jpg"),new File("5.jpg"));
            dummyData.put(new URL("https://live.staticflickr.com/5116/7051608623_ba964f707a.jpg"),new File("6.jpg"));
            dummyData.put(new URL("https://live.staticflickr.com/5543/9170508306_e64fde4496.jpg"),new File("7.jpg"));
            dummyData.put(new URL("https://live.staticflickr.com/5565/15069555740_91d02c275d.jpg"),new File("8.jpg"));
            dummyData.put(new URL("https://live.staticflickr.com/7861/32567922547_9b92dab1a4.jpg"),new File("9.jpg"));
            dummyData.put(new URL("https://live.staticflickr.com/3857/15255851712_21788645d3.jpg"),new File("10.jpg"));
        }catch (MalformedURLException m){
            System.out.println("Malformed URL");
        }

    }

    @FXML public void initialize() {
        width = parentBox.getWidth();
        height = parentBox.getHeight();
        for(URL u:dummyData.keySet()){
            System.out.println(dummyData.get(u).toString());
        }


        //Map<URL,File> imageMap = FormManager.getInstance().getCurrentDownloader().getImageList();

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
