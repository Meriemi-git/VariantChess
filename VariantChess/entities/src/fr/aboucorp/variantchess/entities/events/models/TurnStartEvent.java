package fr.aboucorp.variantchess.entities.events.models;

import java.io.Serializable;

import fr.aboucorp.variantchess.entities.Turn;

public class TurnStartEvent extends TurnEvent implements Serializable {
    public TurnStartEvent(String eventMessage, Turn turn) {
        super(eventMessage, turn);
    }
}
