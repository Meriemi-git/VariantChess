package fr.aboucorp.teamchess.entities.model.moves.movesets;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.Square;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.Location;
import fr.aboucorp.teamchess.entities.model.moves.AbstractMoveSet;
import fr.aboucorp.teamchess.entities.model.utils.ChessCellList;

public class RookMoveSet extends AbstractMoveSet {

    public RookMoveSet(Piece thisPiece, Board board) {
        super(thisPiece, board);
    }

    @Override
    protected ChessCellList getPossibleMoves(Piece piece, Board board, ChessColor turnColor) {
        ChessCellList validCells = new ChessCellList();
        Location start = piece.getLocation();
        for(int x = start.getX()+1; x < 8 ; x++){
            Square validCell = (Square) board.getChessCells().getItemByLocation(new Location(x,0,start.getZ()));
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
            Square validCell = (Square) board.getChessCells().getItemByLocation(new Location(x,0,start.getZ()));
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
            Square validCell = (Square) board.getChessCells().getItemByLocation(new Location(start.getX(),0,z));
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
            Square validCell = (Square) board.getChessCells().getItemByLocation(new Location(start.getX(),0,z));
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
    public ChessCellList getThreats(Piece piece, Board board, ChessColor turnColor) {
        return getPossibleMoves(piece,board,turnColor);
    }
}
