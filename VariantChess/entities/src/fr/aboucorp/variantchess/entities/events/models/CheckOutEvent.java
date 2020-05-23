package fr.aboucorp.variantchess.entities.events.models;

import fr.aboucorp.variantchess.entities.Piece;
import fr.aboucorp.variantchess.entities.enums.BoardEventType;
import fr.aboucorp.variantchess.entities.enums.PieceId;

public class CheckOutEvent extends PieceEvent {
    public CheckOutEvent(String eventMessage, BoardEventType type, PieceId played) {
        super(eventMessage, type, played);
    }
}
