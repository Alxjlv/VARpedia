package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.images.ImageFileManager;

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
            primaryStage.setMaximized(true);
            primaryStage.setResizable(true);
            primaryStage.setMinWidth(1280);
            primaryStage.setMinHeight(720);
            primaryStage.setOnCloseRequest(event -> {
                ImageFileManager.getInstance().clearImages();
                Platform.exit();
                System.exit(0);
            });
            primaryStage.show();

            ImageFileManager.getInstance().clearImages();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
