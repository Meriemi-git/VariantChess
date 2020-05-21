package fr.aboucorp.variantchess.entities.events.models;

import fr.aboucorp.variantchess.entities.Square;
import fr.aboucorp.variantchess.entities.enums.BoardEventType;

public class EnPassantEvent extends BoardEvent {

    public final Square destination;

    public EnPassantEvent(String eventMessage, BoardEventType type, Square destination) {
        super(eventMessage, type);
        this.destination = destination;
    }
}
