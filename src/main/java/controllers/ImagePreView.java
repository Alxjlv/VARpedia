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

    public static Map<URL,File> dummyData = new LinkedHashMap<>();
    private ObservableList<URL> observableList;
    private double width;
    private double height;
    private File loadedImage;

    {
        try{
            dummyData.put(new URL("https://live.staticflickr.com/2693/4174974076_d86e881405.jpg"),new File(".bin/thumbnail/1.jpg"));
            dummyData.put(new URL("https://live.staticflickr.com/5241/5293987598_b5295a528f.jpg"),new File(".bin/thumbnail/2.jpg"));
            dummyData.put(new URL("https://live.staticflickr.com/6039/6334478661_6e1cab8050.jpg"),new File(".bin/thumbnail/3.jpg"));
            dummyData.put(new URL("https://live.staticflickr.com/8113/8629172660_eeb91256f8.jpg"),new File(".bin/thumbnail/4.jpg"));
            dummyData.put(new URL("https://live.staticflickr.com/8681/15856160050_2125e3965b.jpg"),new File(".bin/thumbnail/5.jpg"));
            dummyData.put(new URL("https://live.staticflickr.com/5116/7051608623_ba964f707a.jpg"),new File(".bin/thumbnail/6.jpg"));
            dummyData.put(new URL("https://live.staticflickr.com/5543/9170508306_e64fde4496.jpg"),new File(".bin/thumbnail/7.jpg"));
            dummyData.put(new URL("https://live.staticflickr.com/5565/15069555740_91d02c275d.jpg"),new File(".bin/thumbnail/8.jpg"));
            dummyData.put(new URL("https://live.staticflickr.com/7861/32567922547_9b92dab1a4.jpg"),new File(".bin/thumbnail/9.jpg"));
            dummyData.put(new URL("https://live.staticflickr.com/3857/15255851712_21788645d3.jpg"),new File(".bin/thumbnail/10.jpg"));
        }catch (MalformedURLException m){
            System.out.println("Malformed URL");
        }

    }

    /**
     * This method starts up when the FXML is loaded, and loads the list of images into the scene
     */
    @FXML public void initialize() {
        width = parentBox.getWidth();
        height = parentBox.getHeight();
        for(URL u:dummyData.keySet()){
            System.out.println(dummyData.get(u).toString());
        }

        //Map<URL,File> imageMap = FormManager.getInstance().getCurrentDownloader().getImageList();

        observableList = FXCollections.observableArrayList(dummyData.keySet());
        observableList.addListener(new ListChangeListener<URL>() {
            @Override
            public void onChanged(Change<? extends URL> c) {
                System.out.println("changed");
            }
        });
        imageList.setItems(observableList);
        imageList.setCellFactory(new ThumbnailCellFactory());
        imageList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<URL>() {
            @Override
            public void changed(ObservableValue<? extends URL> observable, URL oldValue, URL newValue) {
                loadedImage=dummyData.get(newValue);
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
    @FXML public void loadImage(File imageFile, double width, double height){
        BackgroundImage myBI;
        Image image = new Image("file:"+imageFile.getPath(), width, height, true, true);
        myBI = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        imagePane.setBackground(new Background(myBI));
    }

}
