package fr.aboucorp.variantchess.entities.events.models;

import fr.aboucorp.variantchess.entities.Piece;
import fr.aboucorp.variantchess.entities.enums.BoardEventType;

public class PieceEvent extends BoardEvent {
    public Piece piece;
    public PieceEvent(String eventMessage, BoardEventType type, Piece piece) {
        super(eventMessage, type);
        this.piece = piece;
    }
}
