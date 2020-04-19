package fr.aboucorp.teamchess.entities.model.events;

import fr.aboucorp.teamchess.entities.model.ChessPiece;
import fr.aboucorp.teamchess.entities.model.enums.PieceEventType;

public class PieceEvent extends BoardEvent {
    public ChessPiece pieceConcerned;
    public PieceEventType type;
    public PieceEvent(String eventMessage, PieceEventType type, ChessPiece pieceConcerned) {
        super(eventMessage);
        this.type = type;
        this.pieceConcerned = pieceConcerned;
    }
}
