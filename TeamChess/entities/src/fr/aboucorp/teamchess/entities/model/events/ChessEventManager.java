package fr.aboucorp.teamchess.entities.model.events;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class ChessEventManager {
    private static ChessEventManager INSTANCE;
    private static Hashtable<Class, List<GameEventSubscriber>> SUBSCRIBERS;
    private ChessEventManager(){

    }

    public static ChessEventManager getINSTANCE() {
        if(INSTANCE == null){
            INSTANCE = new ChessEventManager();
            SUBSCRIBERS = new Hashtable<Class, List<GameEventSubscriber>>();
        }
        return INSTANCE;
    }

    public void subscribe(Class gameEventType, GameEventSubscriber subscriber){
        List<GameEventSubscriber> subsribers = SUBSCRIBERS.get(gameEventType);
        if(subsribers == null){
            subsribers = new ArrayList<>();
        }
        subsribers.add(subscriber);
        SUBSCRIBERS.put(gameEventType,subsribers);
    }

    public void sendMessage(GameEvent event){
        List<GameEventSubscriber> subscribers = SUBSCRIBERS.get(event.getClass());
        if(subscribers != null) {
            for (GameEventSubscriber subscriber : subscribers) {
                subscriber.receiveGameEvent(event);
            }
        }
    }
}
