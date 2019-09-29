package events;

import java.util.EventObject;

public class SwitchSceneEvent extends EventObject{
    private String next = "/ChunkView.fxml";

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
