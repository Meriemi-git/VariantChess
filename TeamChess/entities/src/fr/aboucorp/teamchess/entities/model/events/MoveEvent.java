package fr.aboucorp.teamchess.entities.model.events;

import fr.aboucorp.teamchess.entities.model.ChessCell;
import fr.aboucorp.teamchess.entities.model.ChessPiece;

public class MoveEvent extends GameEvent {
    public final ChessCell from;
    public final ChessCell to;
    public final ChessPiece played;
    public final ChessPiece deadPiece;
    public MoveEvent(String eventMessage, ChessCell from, ChessCell to, ChessPiece played, ChessPiece deadPiece) {
        super(eventMessage);
        this.from = from;
        this.to = to;
        this.played = played;
        this.deadPiece = deadPiece;
    }
}
