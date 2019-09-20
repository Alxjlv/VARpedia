package main;

import com.google.common.eventbus.EventBus;

public class CommunicationBus {

    private static EventBus bus;

    private CommunicationBus(){}

    public static EventBus getBus(){
        if(bus == null){
            bus = new EventBus();
        }
        return bus;
    }
}
