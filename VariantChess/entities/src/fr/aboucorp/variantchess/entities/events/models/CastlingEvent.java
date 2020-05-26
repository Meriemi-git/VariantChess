package fr.aboucorp.variantchess.entities.events.models;

import fr.aboucorp.variantchess.entities.Square;
import fr.aboucorp.variantchess.entities.enums.BoardEventType;
import fr.aboucorp.variantchess.entities.enums.PieceId;

public class CastlingEvent extends PieceEvent {
    public final Square detination;

    public CastlingEvent(String eventMessage, BoardEventType type, PieceId played, Square detination) {
        super(eventMessage, type, played);
        this.detination = detination;
    }
}
