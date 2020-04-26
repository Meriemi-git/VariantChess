package fr.aboucorp.teamchess.entities.model.events.models;

import java.util.List;

import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.enums.BoardEventType;

public class CheckInEvent extends PieceEvent {
    public final List<Piece> checkingPieces;
    public CheckInEvent(String eventMessage, BoardEventType type, Piece piece, List<Piece> checkingPieces) {
        super(eventMessage, type, piece);
        this.checkingPieces = checkingPieces;
    }
}
