package controllers;

import com.google.common.eventbus.EventBus;
import events.SwitchSceneEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import main.CommunicationBus;

import java.io.IOException;
import java.util.EventObject;

public abstract class Controller {

    protected FXMLLoader load;
    protected EventBus bus = CommunicationBus.getBus();
    protected Controller listener;

    public void setListener(Controller listener) {
        this.listener = listener;
    }

    protected String handle(SwitchSceneEvent event){
        return event.getNext();
    }
}
