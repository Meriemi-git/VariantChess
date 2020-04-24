package fr.aboucorp.teamchess.entities.model.events.models;

import fr.aboucorp.teamchess.entities.model.ChessPiece;
import fr.aboucorp.teamchess.entities.model.enums.PieceEventType;

public class PieceEvent extends BoardEvent {
    public ChessPiece piece;
    public PieceEventType type;
    public PieceEvent(String eventMessage, PieceEventType type, ChessPiece piece) {
        super(eventMessage);
        this.type = type;
        this.piece = piece;
    }
}
