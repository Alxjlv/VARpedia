package controllers;

import events.CreationProcessEvent;
import events.StatusEvent;
import events.SwitchSceneEvent;
import javafx.fxml.FXMLLoader;

import java.net.URL;
import java.util.concurrent.ExecutorService;

public abstract class Controller {

    protected FXMLLoader load;
    protected Controller listener;
    protected ExecutorService threadRunner;

    public void setListener(Controller listener) {
        this.listener = listener;
    }

    protected URL handle(SwitchSceneEvent event) {
        return event.getNext();
    }
    public void handle(StatusEvent statusEvent){};

    public void handle(CreationProcessEvent event) {
        return;
    }
}
