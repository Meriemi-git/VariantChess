package fr.aboucorp.teamchess.entities.model.events.models;

import fr.aboucorp.teamchess.entities.model.Square;
import fr.aboucorp.teamchess.entities.model.enums.BoardEventType;

public class EnPassantEvent extends BoardEvent {

    public final Square destination;

    public EnPassantEvent(String eventMessage, BoardEventType type, Square destination) {
        super(eventMessage, type);
        this.destination = destination;
    }
}
