package fr.aboucorp.variantchess.entities.events.models;

import java.io.Serializable;

import fr.aboucorp.variantchess.entities.Square;
import fr.aboucorp.variantchess.entities.enums.PieceId;

public class CastlingEvent extends PieceEvent implements Serializable {
    public final Square detination;

    public CastlingEvent(String eventMessage, int boardEventType, PieceId played, Square detination) {
        super(eventMessage, boardEventType, played);
        this.detination = detination;
    }
}
