package fr.aboucorp.variantchess.entities.events.models;

import fr.aboucorp.variantchess.entities.Turn;

public class TurnEndEvent extends TurnEvent {
    public TurnEndEvent(String eventMessage, Turn turn) {
        super(eventMessage, turn);
    }
}
