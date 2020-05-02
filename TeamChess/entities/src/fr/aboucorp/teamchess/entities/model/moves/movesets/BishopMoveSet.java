package fr.aboucorp.teamchess.entities.model.moves.movesets;

import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.Location;
import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.Square;
import fr.aboucorp.teamchess.entities.model.boards.ClassicBoard;
import fr.aboucorp.teamchess.entities.model.moves.AbstractMoveSet;
import fr.aboucorp.teamchess.entities.model.utils.SquareList;

public class BishopMoveSet extends AbstractMoveSet {

    public BishopMoveSet(Piece thisPiece, ClassicBoard classicBoard) {
        super(thisPiece, classicBoard);
    }

    @Override
    protected SquareList getPossibleMoves(Piece piece, ChessColor turnColor) {
        SquareList allSquares = classicBoard.getSquares();
        SquareList validSquares = new SquareList();
        Location start = piece.getLocation();
        // top left direction
        for(float x = start.getX()+1, z = start.getZ()+1 ; x < 8 && z < 8; x++,z++ ){
            Square validSquare = (Square) allSquares.getItemByLocation(new Location(x,0,z));
            if(validSquare != null && validSquare.getPiece() == null){
                validSquares.add(validSquare);
            }else if(validSquare.getPiece().getChessColor() != turnColor){
                validSquares.add(validSquare);
                break;
            }else{
                break;
            }
        }
        // Top Right direction
        for(float x = start.getX()-1, z = start.getZ()+1 ; x >= 0 && z < 8; x--,z++ ){
            Square validSquare = (Square) allSquares.getItemByLocation(new Location(x,0,z));
            if(validSquare != null && validSquare.getPiece() == null){
                validSquares.add(validSquare);
            }else if(validSquare.getPiece().getChessColor() != turnColor){
                validSquares.add(validSquare);
                break;
            }else{
                break;
            }
        }
        // Down Left direction
        for(float x = start.getX()-1, z = start.getZ()-1 ; x >= 0 && z >= 0; x--,z-- ){
            Square validSquare = (Square) allSquares.getItemByLocation(new Location(x,0,z));
            if(validSquare != null && validSquare.getPiece() == null){
                validSquares.add(validSquare);
            }else if(validSquare.getPiece().getChessColor() != turnColor){
                validSquares.add(validSquare);
                break;
            }else{
                break;
            }
        }
        // Down Right direction
        for(float x = start.getX()+1, z = start.getZ()-1 ; x < 8 && z >= 0; x++,z-- ){
            Square validSquare = (Square) allSquares.getItemByLocation(new Location(x,0,z));
            if(validSquare != null && validSquare.getPiece() == null){
                validSquares.add(validSquare);
            }else if(validSquare.getPiece().getChessColor() != turnColor){
                validSquares.add(validSquare);
                break;
            }else{
                break;
            }
        }
        return validSquares;
    }

    @Override
    public SquareList getThreats(Piece piece, ChessColor turnColor) {
        return getPossibleMoves(piece, turnColor);
    }
}
