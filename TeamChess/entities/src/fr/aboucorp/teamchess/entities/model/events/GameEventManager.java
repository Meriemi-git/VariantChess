package fr.aboucorp.teamchess.entities.model.events;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class GameEventManager {
    private static GameEventManager INSTANCE;
    private static Hashtable<Class, List<GameEventSubscriber>> SUBSCRIBERS;
    private GameEventManager(){
    }

    public static GameEventManager getINSTANCE() {
        if(INSTANCE == null){
            INSTANCE = new GameEventManager();
            SUBSCRIBERS = new Hashtable();
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
        List<GameEventSubscriber> subscribers = new ArrayList();
        getSubscribersRec(event.getClass(),subscribers);
        for (GameEventSubscriber subscriber : subscribers) {
            subscriber.receiveGameEvent(event);
        }
    }

    public  List<GameEventSubscriber> getSubscribersRec(Class eventClass,List<GameEventSubscriber> subscribers) {
        if(eventClass.getSuperclass().equals(Object.class)){
            subscribers.addAll(SUBSCRIBERS.get(eventClass));
        }else{
            subscribers.addAll(SUBSCRIBERS.get(eventClass));
            getSubscribersRec(eventClass.getSuperclass(),subscribers);
        }
        return subscribers;
    }
}
