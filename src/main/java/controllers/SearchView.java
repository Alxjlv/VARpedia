package controllers;

import constants.Folder;
import constants.View;
import constants.Filename;
import events.CreationProcessEvent;
import events.StatusEvent;
import events.SwitchSceneEvent;
import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.util.Callback;
import models.FormManager;
import main.ProcessRunner;
import models.WikipediaSearcher;
import models.images.ImageSearcher;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;

public class SearchView extends AdaptivePanel {

    @FXML private Text loadingMessage;
    @FXML private TextField searchField;
    @FXML private Button searchButton;

    private SuggestionProvider<String> suggestionProvider;

    @FXML public void initialize() {
        searchField.requestFocus();
        suggestionProvider = SuggestionProvider.create(new ArrayList<>());
        TextFields.bindAutoCompletion(searchField, suggestionProvider);
        searchField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue == null || newValue.isEmpty()) {
                    searchButton.setDisable(true);
                } else {
                    searchButton.setDisable(false);

                    Task<Void> searchSuggestions = new Task<Void>() {
                        @Override
                        protected Void call() {
                            System.out.println("Search term: "+searchField.getText());
                            List<String> suggestions = WikipediaSearcher.GetPages(searchField.getText());
                            suggestionProvider.clearSuggestions();
                            suggestionProvider.addPossibleSuggestions(suggestions);
                            System.out.println("Search term: "+searchField.getText()+" Suggestions: "+suggestions);
                            return null;
                        }
                    };
                    Executors.newSingleThreadExecutor().submit(searchSuggestions);
                }
            }
        });
        searchField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    pressSearch();
                }
            }
        });
        searchButton.setDisable(true);
    }

    @FXML public void pressSearch() {
        if (searchField.getText().equals("")) {
            loadingMessage.setText("Please enter an input");
        } else {
            loadingMessage.setText("Searching..."); // TODO - Animate: "Searching." -> "Searching.." -> "Searching..."

            FormManager formManager = FormManager.getInstance();
            formManager.reset();
            formManager.setSearchResult(searchField.getText());

            Task<Void> pageSearcher = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    System.out.println("Searching...");
                    String searchResult = WikipediaSearcher.GetPage(searchField.getText());
                    System.out.println(searchResult);

                    if (searchResult == null) {
                        throw new Exception();
                    } else {
                        FormManager.getInstance().setSearchResult(searchResult);
                    }
                    return null;
                }
            };
            pageSearcher.setOnSucceeded(event -> {
                listener.handle(new SwitchSceneEvent(this, View.CHUNK.get()));
            });
            pageSearcher.setOnFailed(event -> {
                loadingMessage.setText(String.format("Sorry, there are no results for \"%s\"", searchField.getText()));
            });
            Executors.newSingleThreadExecutor().submit(pageSearcher);
        }
    }

    @FXML public void pressCancel() {
        listener.handle(new CreationProcessEvent(this, CreationProcessEvent.Status.CANCEL_CREATE));
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
