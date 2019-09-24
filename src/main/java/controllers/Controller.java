package controllers;

import events.SwitchSceneEvent;
import javafx.fxml.FXMLLoader;

public abstract class Controller {

    protected FXMLLoader load;
    protected Controller listener;

    public void setListener(Controller listener) {
        this.listener = listener;
    }

    protected String handle(SwitchSceneEvent event) {
        return event.getNext();
    }
}
