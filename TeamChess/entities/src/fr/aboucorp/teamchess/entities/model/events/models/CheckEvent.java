package fr.aboucorp.teamchess.entities.model.events.models;

import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.enums.PieceEventType;

public class CheckEvent extends PieceEvent {
    public final Piece checkingPiece;
    public CheckEvent(String eventMessage, PieceEventType type, Piece piece, Piece checkingPiece) {
        super(eventMessage, type, piece);
        this.checkingPiece = checkingPiece;
    }
}
