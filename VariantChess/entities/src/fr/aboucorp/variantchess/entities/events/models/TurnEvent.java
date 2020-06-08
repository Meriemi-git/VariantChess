package fr.aboucorp.variantchess.entities.events.models;

import java.io.Serializable;

import fr.aboucorp.variantchess.entities.Turn;
import fr.aboucorp.variantchess.entities.enums.EventType;

public abstract class TurnEvent extends GameEvent implements Serializable {
    public final Turn turn;

    public TurnEvent(String eventMessage, Turn turn) {
        super(eventMessage, EventType.TURN);
        this.turn = turn;
    }
}
