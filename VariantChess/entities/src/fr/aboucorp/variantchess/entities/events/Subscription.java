package fr.aboucorp.variantchess.entities.events;

class Subscription implements Comparable<Subscription> {
    public final fr.aboucorp.variantchess.entities.events.GameEventSubscriber subscriber;
    public final int priority;
    public final String subscriptionName;

    public Subscription(GameEventSubscriber subscriber, int priority, String subscriptionName) {
        this.subscriber = subscriber;
        this.priority = priority;
        this.subscriptionName = subscriptionName;
    }


    @Override
    public int compareTo(Subscription sub) {
        return Integer.compare(this.priority, sub.priority);
    }
}
