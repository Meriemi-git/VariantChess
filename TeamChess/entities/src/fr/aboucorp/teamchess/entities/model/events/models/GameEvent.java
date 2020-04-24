package fr.aboucorp.teamchess.entities.model.events.models;

public abstract class GameEvent {
    public String eventMessage;

    public GameEvent(String eventMessage) {
        this.eventMessage = eventMessage;
    }
}
