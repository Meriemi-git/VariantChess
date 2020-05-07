package fr.aboucorp.variantchess.entities.events.models;

public abstract class GameEvent {
    public String message;

    public GameEvent(String message) {
        this.message = message;
    }
}
