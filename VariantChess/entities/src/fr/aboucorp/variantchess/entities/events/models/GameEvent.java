package fr.aboucorp.variantchess.entities.events.models;

import java.io.Serializable;

public abstract class GameEvent implements Serializable {
    public final String message;
    public final int boardEventType;

    public GameEvent(String message, int boardEventType) {
        this.message = message;
        this.boardEventType = boardEventType;
    }
}
