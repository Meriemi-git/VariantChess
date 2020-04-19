package fr.aboucorp.teamchess.entities.model.events;

public interface GameEventSubscriber {
    void receiveGameEvent(GameEvent event);
}
