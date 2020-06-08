package fr.aboucorp.variantchess.entities.events.models;

import java.io.Serializable;

import fr.aboucorp.variantchess.entities.Piece;
import fr.aboucorp.variantchess.entities.enums.PieceId;
import fr.aboucorp.variantchess.entities.utils.GameElementList;

public class CheckInEvent extends PieceEvent implements Serializable {
    public final GameElementList<Piece> checkingPieces;

    public CheckInEvent(String eventMessage, int boardEventType, PieceId played, GameElementList<Piece> checkingPieces) {
        super(eventMessage, boardEventType, played);
        this.checkingPieces = checkingPieces;
    }
}
