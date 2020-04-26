package fr.aboucorp.teamchess.entities.model.moves.movesets;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.Square;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.Location;
import fr.aboucorp.teamchess.entities.model.moves.AbstractMoveSet;
import fr.aboucorp.teamchess.entities.model.utils.ChessCellList;

public class BishopMoveSet extends AbstractMoveSet {

    public BishopMoveSet(Piece thisPiece, Board board) {
        super(thisPiece, board);
    }

    @Override
    protected ChessCellList getPossibleMoves(Piece piece, Board board, ChessColor turnColor) {
        ChessCellList allCells = board.getChessCells();
        ChessCellList validCells = new  ChessCellList();
        Location start = piece.getLocation();
        // top left direction
        for(int x = start.getX()+1, z = start.getZ()+1 ; x < 8 && z < 8; x++,z++ ){
            Square validCell = (Square) allCells.getItemByLocation(new Location(x,0,z));
            if(validCell != null && validCell.getPiece() == null){
                validCells.add(validCell);
            }else if(validCell.getPiece().getChessColor() != turnColor){
                validCells.add(validCell);
                break;
            }else{
                break;
            }
        }
        // Top Right direction
        for(int x = start.getX()-1, z = start.getZ()+1 ; x >= 0 && z < 8; x--,z++ ){
            Square validCell = (Square) allCells.getItemByLocation(new Location(x,0,z));
            if(validCell != null && validCell.getPiece() == null){
                validCells.add(validCell);
            }else if(validCell.getPiece().getChessColor() != turnColor){
                validCells.add(validCell);
                break;
            }else{
                break;
            }
        }
        // Down Left direction
        for(int x = start.getX()-1, z = start.getZ()-1 ; x >= 0 && z >= 0; x--,z-- ){
            Square validCell = (Square) allCells.getItemByLocation(new Location(x,0,z));
            if(validCell != null && validCell.getPiece() == null){
                validCells.add(validCell);
            }else if(validCell.getPiece().getChessColor() != turnColor){
                validCells.add(validCell);
                break;
            }else{
                break;
            }
        }
        // Down Right direction
        for(int x = start.getX()+1, z = start.getZ()-1 ; x < 8 && z >= 0; x++,z-- ){
            Square validCell = (Square) allCells.getItemByLocation(new Location(x,0,z));
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
        return getPossibleMoves(piece, board, turnColor);
    }
}
