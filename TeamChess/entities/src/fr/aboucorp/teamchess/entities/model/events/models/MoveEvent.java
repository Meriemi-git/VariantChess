package fr.aboucorp.teamchess.entities.model.events.models;

import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.Square;
import fr.aboucorp.teamchess.entities.model.enums.BoardEventType;

public class MoveEvent extends PieceEvent {
    public final Square to;
    public final Square from;
    public final Piece played;
    public final Piece deadPiece;
    public MoveEvent(String eventMessage,Square from,Square to, Piece played, Piece deadPiece) {
        super(eventMessage, BoardEventType.MOVE,played);
        this.to = to;
        this.from = from;
        this.played = played;
        this.deadPiece = deadPiece;
    }
}
