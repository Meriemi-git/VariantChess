package fr.aboucorp.variantchess.entities.events.models;

import fr.aboucorp.variantchess.entities.Location;
import fr.aboucorp.variantchess.entities.Piece;
import fr.aboucorp.variantchess.entities.Square;
import fr.aboucorp.variantchess.entities.enums.BoardEventType;
import fr.aboucorp.variantchess.entities.enums.PieceId;

public class MoveEvent extends PieceEvent {
    public final Location to;
    public final Location from;
    public final PieceId deadPiece;
    public MoveEvent(String eventMessage,Location from,Location to, PieceId played, PieceId deadPiece) {
        super(eventMessage, BoardEventType.MOVE,played);
        this.to = to;
        this.from = from;
        this.deadPiece = deadPiece;
    }
}
