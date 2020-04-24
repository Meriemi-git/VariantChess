package fr.aboucorp.teamchess.entities.model.events.models;

import fr.aboucorp.teamchess.entities.model.ChessCell;
import fr.aboucorp.teamchess.entities.model.ChessPiece;
import fr.aboucorp.teamchess.entities.model.enums.PieceEventType;

public class MoveEvent extends PieceEvent {
    public final ChessCell from;
    public final ChessCell to;
    public final ChessPiece played;
    public final ChessPiece deadPiece;
    public MoveEvent(String eventMessage, ChessCell from, ChessCell to, ChessPiece played, ChessPiece deadPiece) {
        super(eventMessage, PieceEventType.MOVE,played);
        this.from = from;
        this.to = to;
        this.played = played;
        this.deadPiece = deadPiece;
    }
}
