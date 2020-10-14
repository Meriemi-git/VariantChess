package fr.aboucorp.variantchess.entities.utils;

import fr.aboucorp.variantchess.entities.Piece;
import fr.aboucorp.variantchess.entities.enums.PieceId;

public class PieceList extends GameElementList<Piece> {
    public Piece getPieceById(PieceId id) {
        for (Piece piece : this) {
            if (piece.getPieceId().equals(id)) {
                return piece;
            }
        }
        return null;
    }

}
