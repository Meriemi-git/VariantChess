package fr.aboucorp.teamchess.entities.model.events;

public class Subscription implements Comparable<Subscription>{
    public final GameEventSubscriber subscriber;
    public final int priority;

    public Subscription(GameEventSubscriber subscriber, int priority) {
        this.subscriber = subscriber;
        this.priority = priority;
    }


    @Override
    public int compareTo(Subscription sub) {
        return Integer.compare(this.priority,sub.priority);
    }
}
