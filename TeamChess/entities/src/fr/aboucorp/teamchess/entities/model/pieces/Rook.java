package fr.aboucorp.teamchess.entities.model.pieces;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessCell;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.ChessPiece;
import fr.aboucorp.teamchess.entities.model.Location;
import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.entities.model.utils.ChessList;

public class Rook extends ChessPiece {

    public Rook(ChessCell cell, ChessColor chessColor, PieceId pieceId) {
        super(cell, chessColor,pieceId);
    }

    @Override
    public  ChessList<ChessCell> getNextMoves(ChessPiece piece, Board board) {
        ChessList allCells = board.getChessCells();
        ChessList<ChessCell> validCells = new ChessList<ChessCell>();
        Location start = piece.getLocation();

        for(int x = start.getX()+1; x < 8 ; x++){
            ChessCell validCell = (ChessCell) allCells.getItemByLocation(new Location(x,0,start.getZ()));
            if(validCell != null && validCell.getPiece() == null){
                validCells.add(validCell);
            }else{
                break;
            }
        }

        for(int x = start.getX()-1 ; x >= 0 ; x--){
            ChessCell validCell = (ChessCell) allCells.getItemByLocation(new Location(x,0,start.getZ()));
            if(validCell != null && validCell.getPiece() == null){
                validCells.add(validCell);
            }else{
                break;
            }
        }

        for(int z = start.getZ()-1 ;  z >= 0; z-- ){
            ChessCell validCell = (ChessCell) allCells.getItemByLocation(new Location(start.getX(),0,z));
            if(validCell != null && validCell.getPiece() == null){
                validCells.add(validCell);
            }else{
                break;
            }
        }

        for(int z = start.getZ()+1 ; z < 8; z++ ){
            ChessCell validCell = (ChessCell) allCells.getItemByLocation(new Location(start.getX(),0,z));
            if(validCell != null && validCell.getPiece() == null){
                validCells.add(validCell);
            }else{
                break;
            }
        }
        return validCells;
    }
}
