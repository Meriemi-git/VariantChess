package fr.aboucorp.variantchess.entities.events.models;

import fr.aboucorp.variantchess.entities.Turn;

public class TurnStartEvent extends TurnEvent {
    public TurnStartEvent(String eventMessage, Turn turn) {
        super(eventMessage, turn);
    }
}
