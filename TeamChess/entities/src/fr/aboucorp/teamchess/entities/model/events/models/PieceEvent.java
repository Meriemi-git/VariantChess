package fr.aboucorp.teamchess.entities.model.events.models;

import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.enums.PieceEventType;

public class PieceEvent extends BoardEvent {
    public Piece piece;
    public PieceEventType type;
    public PieceEvent(String eventMessage, PieceEventType type, Piece piece) {
        super(eventMessage);
        this.type = type;
        this.piece = piece;
    }
}
