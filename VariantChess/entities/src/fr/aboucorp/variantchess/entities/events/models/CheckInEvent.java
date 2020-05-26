package fr.aboucorp.variantchess.entities.events.models;

import fr.aboucorp.variantchess.entities.Piece;
import fr.aboucorp.variantchess.entities.enums.BoardEventType;
import fr.aboucorp.variantchess.entities.enums.PieceId;
import fr.aboucorp.variantchess.entities.utils.GameElementList;

public class CheckInEvent extends PieceEvent {
    public final GameElementList<Piece> checkingPieces;
    public CheckInEvent(String eventMessage, BoardEventType type, PieceId played, GameElementList<Piece> checkingPieces) {
        super(eventMessage, type, played);
        this.checkingPieces = checkingPieces;
    }
}
