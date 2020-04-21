package fr.aboucorp.teamchess.entities.model.utils;

import fr.aboucorp.teamchess.entities.model.ChessPiece;
import fr.aboucorp.teamchess.entities.model.enums.PieceId;

public class ChessPieceList extends ChessList<ChessPiece> {
    public ChessPiece getPieceById(PieceId id){
        for(ChessPiece piece : this){
            if(piece.getPieceId() == id){
                return piece;
            }
        }
        return null;
    }
}
