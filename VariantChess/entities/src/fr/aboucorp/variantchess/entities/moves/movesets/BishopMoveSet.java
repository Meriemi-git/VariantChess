package fr.aboucorp.variantchess.entities.moves.movesets;

import fr.aboucorp.variantchess.entities.boards.ClassicBoard;
import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.Location;
import fr.aboucorp.variantchess.entities.Piece;
import fr.aboucorp.variantchess.entities.Square;
import fr.aboucorp.variantchess.entities.moves.AbstractMoveSet;
import fr.aboucorp.variantchess.entities.utils.SquareList;

public class BishopMoveSet extends AbstractMoveSet {

    public BishopMoveSet(Piece thisPiece, ClassicBoard classicBoard) {
        super(thisPiece, classicBoard);
    }

    @Override
    protected fr.aboucorp.variantchess.entities.utils.SquareList getPossibleMoves(Piece piece, ChessColor turnColor) {
        fr.aboucorp.variantchess.entities.utils.SquareList allSquares = classicBoard.getSquares();
        fr.aboucorp.variantchess.entities.utils.SquareList validSquares = new fr.aboucorp.variantchess.entities.utils.SquareList();
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
