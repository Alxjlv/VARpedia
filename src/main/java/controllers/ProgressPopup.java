package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;

public class ProgressPopup {

    private SimpleStringProperty messageProperty = new SimpleStringProperty();

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label message;

    @FXML
    public void initialize() {
        progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

        messageProperty.addListener((observable, oldValue, newValue) -> message.setText(newValue));
    }

    public void setMessage(String message) {
        messageProperty.set(message);
    }

    public void close() {
        Stage stage = (Stage) progressBar.getScene().getWindow();
        stage.close();
    }
}
