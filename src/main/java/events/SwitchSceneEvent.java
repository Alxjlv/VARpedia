package events;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

import java.util.EventObject;

public class SwitchSceneEvent extends EventObject{
    private String next = "/SnippetView.fxml";

    public SwitchSceneEvent(Object source, String fxml){
        super(source);
        if(fxml != null){
            next = fxml;
        }
    }

    public String getNext(){
        return next;
    }

}
