package fr.aboucorp.variantchess.entities.events.models;

import java.io.Serializable;

import fr.aboucorp.variantchess.entities.Square;

public class EnPassantEvent extends BoardEvent implements Serializable {

    public final Square destination;

    public EnPassantEvent(String eventMessage, int boardEventType, Square destination) {
        super(eventMessage, boardEventType);
        this.destination = destination;
    }
}
