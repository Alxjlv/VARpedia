package events;

import java.net.URL;
import java.util.EventObject;

/**
 * This event is simply responsible for sending the scene that needs to be changed to back to the Adaptive Panel
 * @author Tait & Alex
 */
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
