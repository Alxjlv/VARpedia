package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.images.ImageFileManager;

/**
 * This is the main class, which is responsible for actually launching the application
 * @author Tait & Alex
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setTitle("VARpedia");
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/AdaptivePanel.fxml"));
            Parent layout = loader.load();
            Scene scene = new Scene(layout);
            primaryStage.setScene(scene);

            // Maximising the window to begin with, then setting the minimum resizability
            primaryStage.setMaximized(true);
            primaryStage.setResizable(true);
            primaryStage.setMinWidth(1280);
            primaryStage.setMinHeight(720);

            // When the stage is closed, the cached images are cleared (as per flickr terms and conditions) and threads closed
            primaryStage.setOnCloseRequest(event -> {
                ImageFileManager.getInstance().clearImages();
                Platform.exit();
                System.exit(0);
            });
            primaryStage.show();

            ImageFileManager.getInstance().clearImages(); //Making sure the images are cleared on startup
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
