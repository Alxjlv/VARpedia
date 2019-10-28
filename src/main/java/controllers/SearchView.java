package controllers;

import constants.View;
import events.CreationProcessEvent;
import events.SwitchSceneEvent;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import models.WikipediaSearcher;
import models.creation.CreationProcessManager;
import models.images.ImageSearcher;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.util.ArrayList;
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
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                searchButton.setDisable(true);
            } else {
                searchButton.setDisable(false);

                Task<Void> suggestionSearcher = new Task<Void>() {
                    @Override
                    protected Void call() {
                        String searchTerm = searchField.getText();
                        List<String> suggestions;
                        try {
                            suggestions = WikipediaSearcher.GetPages(searchTerm);
                        } catch (IOException e) {
                            return null;
                        }
                        suggestionProvider.clearSuggestions();
                        suggestionProvider.addPossibleSuggestions(suggestions);
                        return null;
                    }
                };
                Executors.newSingleThreadExecutor().submit(suggestionSearcher);
            }
        });
        searchField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                pressSearch();
            }
        });
        searchButton.setDisable(true);
    }

    @FXML public void pressSearch() {
        if (searchField.getText().equals("")) {
            loadingMessage.setText("Please enter an input");
        } else {
            loadingMessage.setText("Searching...");

            CreationProcessManager formManager = CreationProcessManager.getInstance();
            formManager.reset();
            formManager.setSearchTerm(searchField.getText());

            ImageSearcher.Search(formManager.getSearchTerm(), 15);

            Task<Void> pageSearcher = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    String searchResult = WikipediaSearcher.GetPage(searchField.getText());

                    if (searchResult == null) {
                        throw new Exception();
                    } else {
                        CreationProcessManager.getInstance().setSearchText(searchResult);
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
}
