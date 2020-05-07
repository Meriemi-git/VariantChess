package fr.aboucorp.variantchess.entities.events.models;

import fr.aboucorp.variantchess.entities.Piece;
import fr.aboucorp.variantchess.entities.enums.BoardEventType;

public class CheckOutEvent extends PieceEvent {
    public CheckOutEvent(String eventMessage, BoardEventType type, Piece piece) {
        super(eventMessage, type, piece);
    }
}
