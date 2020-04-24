package fr.aboucorp.teamchess.entities.model.events.models;

import fr.aboucorp.teamchess.entities.model.ChessPiece;
import fr.aboucorp.teamchess.entities.model.enums.PieceEventType;

public class CheckEvent extends PieceEvent {
    public final ChessPiece checkingPiece;
    public CheckEvent(String eventMessage, PieceEventType type, ChessPiece piece, ChessPiece checkingPiece) {
        super(eventMessage, type, piece);
        this.checkingPiece = checkingPiece;
    }
}
