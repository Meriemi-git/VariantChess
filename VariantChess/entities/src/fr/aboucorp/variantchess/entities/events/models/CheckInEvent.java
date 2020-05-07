package fr.aboucorp.variantchess.entities.events.models;

import java.util.List;

import fr.aboucorp.variantchess.entities.Piece;
import fr.aboucorp.variantchess.entities.enums.BoardEventType;

public class CheckInEvent extends PieceEvent {
    public final List<fr.aboucorp.variantchess.entities.Piece> checkingPieces;
    public CheckInEvent(String eventMessage, BoardEventType type, fr.aboucorp.variantchess.entities.Piece piece, List<Piece> checkingPieces) {
        super(eventMessage, type, piece);
        this.checkingPieces = checkingPieces;
    }
}
