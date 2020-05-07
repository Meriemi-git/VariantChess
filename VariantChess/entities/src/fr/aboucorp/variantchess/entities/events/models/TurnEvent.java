package fr.aboucorp.variantchess.entities.events.models;

import fr.aboucorp.variantchess.entities.Turn;

public abstract class TurnEvent extends GameEvent {
    public final Turn turn;

    public TurnEvent(String eventMessage, Turn turn) {
        super(eventMessage);
        this.turn = turn;
    }
}
