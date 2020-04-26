package fr.aboucorp.teamchess.entities.model.utils;

import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.enums.PieceId;

public class ChessPieceList extends ChessList<Piece> {
    public Piece getPieceById(PieceId id){
        for(Piece piece : this){
            if(piece.getPieceId() == id){
                return piece;
            }
        }
        return null;
    }

}
