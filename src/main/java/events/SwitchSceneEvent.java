package events;

import java.net.URL;
import java.util.EventObject;

public class SwitchSceneEvent extends EventObject{
    private URL next;

    public SwitchSceneEvent(Object source, URL scene){
        super(source);
        next = scene;
    }

    public URL getNext(){
        return next;
    }

}
