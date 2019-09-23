package controllers;

import com.google.common.eventbus.EventBus;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import main.CommunicationBus;

import java.io.IOException;

public abstract class Controller {

    protected FXMLLoader load;
    protected EventBus bus = CommunicationBus.getBus();


}
