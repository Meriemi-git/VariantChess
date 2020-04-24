package fr.aboucorp.teamchess.entities.model.events;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import fr.aboucorp.teamchess.entities.model.events.models.GameEvent;

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

    public void unSubscribe(Class gameEventType, GameEventSubscriber subscriber){
        for (Class aClass: SUBSCRIBERS.keySet()) {
            if(gameEventType.isAssignableFrom(aClass)){
                List<GameEventSubscriber> subsribers = SUBSCRIBERS.get(aClass);
                for(Iterator<GameEventSubscriber> iter = subsribers.iterator();iter.hasNext();){
                    GameEventSubscriber sub = iter.next();
                    if(sub == subscriber){
                        iter.remove();
                    }
                }
            }
        }
    }

    public void sendMessage(GameEvent event){
        List<GameEventSubscriber> subscribers = new ArrayList();
        getSubscribersRec(event.getClass(),subscribers);
        for (GameEventSubscriber subscriber : subscribers) {
            subscriber.receiveGameEvent(event);
        }
    }

    public  List<GameEventSubscriber> getSubscribersRec(Class eventClass,List<GameEventSubscriber> subscribers) {
        List<GameEventSubscriber> subs = SUBSCRIBERS.get(eventClass);
        if(subs != null){
            subscribers.addAll(subs);
        }
        if(!eventClass.getSuperclass().equals(Object.class)){
            getSubscribersRec(eventClass.getSuperclass(),subscribers);
        }
        return subscribers;
    }
}
