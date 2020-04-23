package fr.aboucorp.teamchess.entities.model.moves.movesets;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessCell;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.ChessPiece;
import fr.aboucorp.teamchess.entities.model.Location;
import fr.aboucorp.teamchess.entities.model.utils.ChessCellList;

public class RookMoveSet extends AbstractMoveSet{

    @Override
    public ChessCellList getPossibleMoves(ChessPiece piece, Board board, ChessColor turnColor) {
        ChessCellList validCells = new ChessCellList();
        Location start = piece.getLocation();
        for(int x = start.getX()+1; x < 8 ; x++){
            ChessCell validCell = (ChessCell) board.getChessCells().getItemByLocation(new Location(x,0,start.getZ()));
            if(validCell != null && validCell.getPiece() == null){
                validCells.add(validCell);
            }else if(validCell.getPiece().getChessColor() != turnColor){
                validCells.add(validCell);
                break;
            }else{
                break;
            }
        }

        for(int x = start.getX()-1 ; x >= 0 ; x--){
            ChessCell validCell = (ChessCell) board.getChessCells().getItemByLocation(new Location(x,0,start.getZ()));
            if(validCell != null && validCell.getPiece() == null){
                validCells.add(validCell);
            }else if(validCell.getPiece().getChessColor() != turnColor){
                validCells.add(validCell);
                break;
            }else{
                break;
            }
        }

        for(int z = start.getZ()-1 ;  z >= 0; z-- ){
            ChessCell validCell = (ChessCell) board.getChessCells().getItemByLocation(new Location(start.getX(),0,z));
            if(validCell != null && validCell.getPiece() == null){
                validCells.add(validCell);
            }else if(validCell.getPiece().getChessColor() != turnColor){
                validCells.add(validCell);
                break;
            }else{
                break;
            }
        }

        for(int z = start.getZ()+1 ; z < 8; z++ ){
            ChessCell validCell = (ChessCell) board.getChessCells().getItemByLocation(new Location(start.getX(),0,z));
            if(validCell != null && validCell.getPiece() == null){
                validCells.add(validCell);
            }else if(validCell.getPiece().getChessColor() != turnColor){
                validCells.add(validCell);
                break;
            }else{
                break;
            }
        }
        return validCells;
    }

    @Override
    public ChessCellList getThreats(ChessPiece piece, Board board, ChessColor turnColor) {
        return getPossibleMoves(piece,board,turnColor);
    }
}
