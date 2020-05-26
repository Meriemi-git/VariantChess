package fr.aboucorp.variantchess.entities.events;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import fr.aboucorp.variantchess.entities.Match;
import fr.aboucorp.variantchess.entities.PartyLifeCycle;
import fr.aboucorp.variantchess.entities.events.models.GameEvent;

public class GameEventManager {
    private static Hashtable<Class, List<Subscription>> SUBSCRIPTIONS;

    public GameEventManager(){
        SUBSCRIPTIONS = new Hashtable();
    }

    public void subscribe(Class gameEventType, GameEventSubscriber subscriber,int priority){
        List<Subscription> subsribers = SUBSCRIPTIONS.get(gameEventType);
        if(subsribers == null){
            subsribers = new ArrayList<>();
        }
        subsribers.add(new Subscription(subscriber,priority));
        SUBSCRIPTIONS.put(gameEventType,subsribers);
    }

    public void unSubscribe(Class gameEventType, GameEventSubscriber subscriber){
        SUBSCRIPTIONS
                .keySet()
                .stream()
                .filter(aClass -> gameEventType.isAssignableFrom(aClass))
                .map(aClass -> SUBSCRIPTIONS.get(aClass))
                .forEach(subs -> subs.removeIf(sub -> sub.subscriber == subscriber));
    }

    public void sendMessage(GameEvent event){
        List<Subscription> subscriptions = new ArrayList();
        this.getSubscriptionsRec(event.getClass(),subscriptions);
        subscriptions
                .stream()
                .sorted(Comparator.naturalOrder())
                .forEachOrdered( sub -> sub.subscriber.receiveGameEvent(event));
    }

    private List<Subscription> getSubscriptionsRec(Class eventClass, List<Subscription> subscription) {
        List<Subscription> subs = SUBSCRIPTIONS.get(eventClass);
        if(subs != null){
            subscription.addAll(subs);
        }
        if(!eventClass.getSuperclass().equals(Object.class)){
            this.getSubscriptionsRec(eventClass.getSuperclass(),subscription);
        }
        return subscription;
    }

}
