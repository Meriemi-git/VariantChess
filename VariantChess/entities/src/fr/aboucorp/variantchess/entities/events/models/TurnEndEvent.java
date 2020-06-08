package fr.aboucorp.variantchess.entities.events.models;

import java.io.Serializable;

import fr.aboucorp.variantchess.entities.Turn;

public class TurnEndEvent extends TurnEvent implements Serializable {
    public TurnEndEvent(String eventMessage, Turn turn) {
        super(eventMessage, turn);
    }
}
