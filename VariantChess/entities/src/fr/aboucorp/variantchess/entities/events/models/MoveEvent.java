package fr.aboucorp.variantchess.entities.events.models;

import java.io.Serializable;

import fr.aboucorp.variantchess.entities.Location;
import fr.aboucorp.variantchess.entities.enums.EventType;
import fr.aboucorp.variantchess.entities.enums.PieceId;

public class MoveEvent extends PieceEvent implements Serializable {
    public final Location to;
    public final Location from;
    public final PieceId deadPiece;

    public MoveEvent(String eventMessage, Location from, Location to, PieceId played, PieceId deadPiece) {
        super(eventMessage, EventType.MOVE, played);
        this.to = to;
        this.from = from;
        this.deadPiece = deadPiece;
    }
}
