package controllers;

import events.CreationProcessEvent;
import events.SwitchSceneEvent;
import javafx.fxml.FXMLLoader;

import java.util.concurrent.ExecutorService;

/**
 * The abstract controller class is responsible for some small duplicate functionality across all controller classes
 * @author Tait & Alex
 */
public abstract class Controller {

    FXMLLoader load; // Loader for scene switching
    Controller listener; // Used to store the parent Controller (ie. AdaptivePanel)
    ExecutorService threadRunner; // Used for executing tasks & concurrent processes

    /**
     * Sets the parent controller
     * @param listener - the parent {@link controllers.Controller}
     */
    void setListener(Controller listener) {
        this.listener = listener;
    }

    /**
     * Handles a {@link events.SwitchSceneEvent}
     * @param event - the event to be handled
     */
    protected void handle(SwitchSceneEvent event){

    }

    /**
     * Handles a {@link events.CreationProcessEvent}
     * @param event - the event to be handled
     */
    public void handle(CreationProcessEvent event){

    }
}
