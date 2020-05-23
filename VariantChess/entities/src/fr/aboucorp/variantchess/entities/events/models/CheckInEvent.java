package fr.aboucorp.variantchess.entities.events.models;

import java.util.List;

import fr.aboucorp.variantchess.entities.Piece;
import fr.aboucorp.variantchess.entities.enums.BoardEventType;
import fr.aboucorp.variantchess.entities.enums.PieceId;
import fr.aboucorp.variantchess.entities.utils.ChessList;

public class CheckInEvent extends PieceEvent {
    public final ChessList<Piece> checkingPieces;
    public CheckInEvent(String eventMessage, BoardEventType type, PieceId played, ChessList<Piece> checkingPieces) {
        super(eventMessage, type, played);
        this.checkingPieces = checkingPieces;
    }
}
