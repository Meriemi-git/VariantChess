package fr.aboucorp.teamchess.entities.model.events;

public abstract class GameEvent {
    public String eventMessage;

    public GameEvent(String eventMessage) {
        this.eventMessage = eventMessage;
    }
}
