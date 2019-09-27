package controllers;

import events.SwitchSceneEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import main.ProcessRunner;
import main.Search;

import java.util.concurrent.Executors;

public class CreateView extends AdaptivePanel {

    @FXML GridPane CreateView;
    @FXML Text loadingMessage;
    @FXML TextField searchBox;


    @FXML public void initialize() {
    }

    @FXML public void pressSearch() {
        if(searchBox.getText().equals("")){
            loadingMessage.setText("Please enter an input");
        }else {
            String searchTerm = searchBox.getText();
            String command = "wikit " + searchTerm + " > ./.temp/search.txt; " +
                    "if [ $(cat ./index/temp.txt | grep \"" + searchTerm +
                    " not found :^(\">/dev/null; echo $?) -eq \"0\" ]; then exit 1;" +
                    "fi; exit 0;";
            ProcessRunner process = new ProcessRunner(command);
            threadRunner = Executors.newSingleThreadExecutor();
            threadRunner.submit(process);
            process.setOnSucceeded(event -> {
                if(process.getExitVal()==0){
                    searchBox.setText("Success");
                    Search search = new Search();
                    search.Search(searchTerm,15);
                }else{
                    searchBox.setText("Nothing returned, please try again");
                }
            });
        }


        // TODO - "Searching." -> "Searching.." -> "Searching..." message
        listener.handle(new SwitchSceneEvent(this, "/SnippetView.fxml"));
    }

    @FXML public void pressCancel() {
        // TODO - No alert required?
        listener.handle(new SwitchSceneEvent(this, "/WelcomeView.fxml"));
    }

}
