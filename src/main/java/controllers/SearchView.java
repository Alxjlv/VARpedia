package controllers;

import constants.Filename;
import constants.Folder;
import constants.View;
import events.CreationProcessEvent;
import events.SwitchSceneEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import main.ProcessRunner;
import models.FormManager;
import models.images.ImageSearcher;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Executors;

public class SearchView extends AdaptivePanel {

    @FXML private Text loadingMessage;
    @FXML private TextField searchBox;
    @FXML private Button searchButton;

    @FXML public void initialize() {
        searchBox.requestFocus();
        searchBox.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                searchButton.setDisable(true);
            } else {
                searchButton.setDisable(false);
            }
        });
        searchBox.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                pressSearch();
            }
        });
        searchButton.setDisable(true);
    }

    @FXML public void pressSearch() {
        if (searchBox.getText().equals("")) { 
            loadingMessage.setText("Please enter an input");
        } else {
            loadingMessage.setText("Searching...");

            File tempFolder = Folder.TEMP.get();
            String searchTerm = searchBox.getText();
            FormManager formManager = FormManager.getInstance();
            formManager.reset();
            formManager.setSearchTerm(searchTerm);
            File searchTextFile = new File(tempFolder, Filename.SEARCH_TEXT.get());
            String command = "wikit " + searchTerm + " > "+searchTextFile.getPath()+"; " +
                    "if [ $(cat "+searchTextFile.getPath()+" | grep \"" + searchTerm +
                    " not found :^(\">/dev/null; echo $?) -eq \"0\" ]; then exit 1;" +
                    "fi; exit 0;";
            ProcessRunner process = new ProcessRunner(command);
            threadRunner = Executors.newSingleThreadExecutor();
            threadRunner.submit(process);
            process.setOnSucceeded(event -> {
                ImageSearcher imageSearcher = new ImageSearcher();
                imageSearcher.Search(FormManager.getInstance().getSearchTerm(), 15);

                try {
                    FileReader result = new FileReader(searchTextFile);
                    String searchText = "";
                    int i;
                    while ((i = result.read()) != -1) {
                        searchText = searchText.concat(Character.toString((char) i));
                    }
                    searchText = searchText.trim();
                    formManager.setSearchText(searchText);

                } catch (IOException e) {
                    return;
                }

                listener.handle(new SwitchSceneEvent(this, View.CHUNK.get()));

            });
            process.setOnFailed(event -> {
                loadingMessage.setText("Nothing returned, please try again");
                process.getException().printStackTrace();
            });
        }
    }

    @FXML public void pressCancel() {
        listener.handle(new CreationProcessEvent(this, CreationProcessEvent.Status.CANCEL_CREATE));
    }
}
