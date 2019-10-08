package controllers;

import constants.FileExtension;
import constants.FolderPath;
import events.CreationProcessEvent;
import events.StatusEvent;
import events.SwitchSceneEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import main.ImageSearcher;
import main.ProcessRunner;
import models.ChunkManager;
import models.SearchManager;

import java.io.File;
import java.util.concurrent.Executors;

public class SearchView extends AdaptivePanel {

    @FXML GridPane CreateView;
    @FXML Text loadingMessage;
    @FXML TextField searchBox;


    @FXML public void initialize() {
        ChunkManager.newInstance();

        searchBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    pressSearch();
                }
            }
        });
    }

    @FXML public void pressSearch() {
        File tempFolder = FolderPath.TEMP_FOLDER.getPath();
        File imagesFolder = new File(tempFolder, FileExtension.IMAGES.getExtension());
        imagesFolder.mkdirs();
        if (searchBox.getText().equals("")) { 
            loadingMessage.setText("Please enter an input");
        } else {
            String searchTerm = searchBox.getText();
            SearchManager searchManager = SearchManager.getInstance();
            searchManager.setSearchTerm(searchTerm);
            File searchText = new File(tempFolder, FileExtension.SEARCH_TEXT.getExtension());
            String command = "wikit " + searchTerm + " > "+searchText.getPath()+"; " +
                    "if [ $(cat "+searchText.getPath()+" | grep \"" + searchTerm +
                    " not found :^(\">/dev/null; echo $?) -eq \"0\" ]; then exit 1;" +
                    "fi; exit 0;";
            ProcessRunner process = new ProcessRunner(command);
            threadRunner = Executors.newSingleThreadExecutor();
            threadRunner.submit(process);
            loadingMessage.setText("Searching..."); // TODO - Animate: "Searching." -> "Searching.." -> "Searching..."
            process.setOnSucceeded(event -> {
                if (process.getExitVal()==0) {
                    ImageSearcher imageSearcher = new ImageSearcher(this);
                    imageSearcher.Search(searchTerm,15);
                    listener.handle(new SwitchSceneEvent(this, "/ChunkView.fxml"));
                } else {
                    loadingMessage.setText("Nothing returned, please try again");
                }
            });
        }
    }

    @FXML public void pressCancel() {
        listener.handle(new CreationProcessEvent(this, CreationProcessEvent.Status.CANCEL));
    }

    @Override
    public void handle(StatusEvent statusEvent) {
        if (statusEvent.getStatus()) {
            loadingMessage.setText("Images downloaded successfully");
        } else {
            loadingMessage.setText("Images not downloaded");
        }
    }

}
