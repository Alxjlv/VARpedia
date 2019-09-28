package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

public class Main extends Application {

    private static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        try {
            Main.primaryStage = primaryStage;
            primaryStage.setTitle("VARpedia");
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/AdaptivePanel.fxml"));
            Parent layout = loader.load();
            Scene scene = new Scene(layout);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
