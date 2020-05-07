package fr.aboucorp.variantchess.entities.events.models;

import fr.aboucorp.variantchess.entities.Piece;
import fr.aboucorp.variantchess.entities.Square;
import fr.aboucorp.variantchess.entities.enums.BoardEventType;

public class CastlingEvent extends PieceEvent {
    public final Square detination;
    public CastlingEvent(String eventMessage, BoardEventType type, Piece piece, Square detination) {
        super(eventMessage, type, piece);
        this.detination = detination;
    }
}
