package controllers;

import constants.Folder;
import constants.View;
import constants.Filename;
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
import models.FormManager;
import models.images.ImageFileManager;
import main.ProcessRunner;
import models.images.ImageSearcher;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Executors;

public class SearchView extends AdaptivePanel {

    @FXML GridPane CreateView;
    @FXML Text loadingMessage;
    @FXML TextField searchBox;


    @FXML public void initialize() {
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
        if (searchBox.getText().equals("")) { 
            loadingMessage.setText("Please enter an input");
        } else {
            loadingMessage.setText("Searching..."); // TODO - Animate: "Searching." -> "Searching.." -> "Searching..."

            File tempFolder = Folder.TEMP.get();
            String searchTerm = searchBox.getText();
            FormManager formManager = FormManager.getInstance();
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
//                ImageFileManager.getInstance().search(15);
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
                    e.printStackTrace();
                    // TODO - Handle exception
                }

                listener.handle(new SwitchSceneEvent(this, View.CHUNK.get()));
//                } else {
//                    loadingMessage.setText("Nothing returned, please try again");
//                }
            });
            process.setOnFailed(event -> {
                loadingMessage.setText("Nothing returned, please try again");
                process.getException().printStackTrace();
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

    // TODO - This code is reused in Manager. Reduce duplication
    private boolean recursiveDelete(File directory) {
        if (directory.isDirectory()) {
            File[] children = directory.listFiles();
            for (File child: children) {
                boolean status = recursiveDelete(child);
                if (!status) {
                    return false;
                }
            }
        }
        return directory.delete();
    }

}
