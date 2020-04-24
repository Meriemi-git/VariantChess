package fr.aboucorp.teamchess.entities.model.moves.movesets;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessCell;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.ChessPiece;
import fr.aboucorp.teamchess.entities.model.Location;
import fr.aboucorp.teamchess.entities.model.moves.AbstractMoveSet;
import fr.aboucorp.teamchess.entities.model.utils.ChessCellList;

public class PawnMoveSet extends AbstractMoveSet {

    public PawnMoveSet(ChessPiece thisPiece, Board board) {
        super(thisPiece, board);
    }

    @Override
    protected ChessCellList getPossibleMoves(ChessPiece piece, Board board, ChessColor turnColor) {
        ChessCellList validCells = new  ChessCellList();
        Location start = piece.getLocation();
        ChessCell simpleMove;
        if(piece.getChessColor() == ChessColor.WHITE) {
            simpleMove = (ChessCell) board.getChessCells().getItemByLocation(new Location(start.getX(), 0, start.getZ() + 1));
        }else{
            simpleMove = (ChessCell) board.getChessCells().getItemByLocation(new Location(start.getX(), 0, start.getZ() - 1));
        }
        if(simpleMove != null && simpleMove.getPiece() == null ){
            validCells.add(simpleMove);
            ChessCell doubleMove;
            if(piece.isFirstMove()){
                if(piece.getChessColor() == ChessColor.WHITE) {
                    doubleMove = (ChessCell) board.getChessCells().getItemByLocation(new Location(start.getX(), 0, start.getZ() + 2));
                }else{
                    doubleMove = (ChessCell) board.getChessCells().getItemByLocation(new Location(start.getX(), 0, start.getZ() - 2));
                }
                if(doubleMove != null && doubleMove.getPiece() == null){
                    validCells.add(doubleMove);
                }
            }
        }
        validCells.addAll(getEatingMoves(piece,board,turnColor,false));
        checkEnPassant();
        return  validCells;
    }

    private ChessCellList getEatingMoves(ChessPiece piece, Board board, ChessColor turnColor , boolean isThreat){
        Location start = piece.getLocation();
        ChessCellList validCells = new  ChessCellList();
        ChessCell diagRight;
        ChessCell diagLeft;
        if(piece.getChessColor() == ChessColor.WHITE) {
            diagRight = (ChessCell) board.getChessCells().getItemByLocation(new Location(start.getX()-1,0,start.getZ() + 1));
            diagLeft = (ChessCell) board.getChessCells().getItemByLocation(new Location(start.getX()+1,0,start.getZ() + 1));
        }else{
            diagRight = (ChessCell) board.getChessCells().getItemByLocation(new Location(start.getX()+1,0,start.getZ() - 1));
            diagLeft = (ChessCell) board.getChessCells().getItemByLocation(new Location(start.getX()-1,0,start.getZ() - 1));
        }
        if(diagRight != null && (isThreat || (diagRight.getPiece() != null && diagRight.getPiece().getChessColor() != turnColor))){
            validCells.add(diagRight);
        }
        if(diagLeft != null && (isThreat || (diagLeft.getPiece() != null && diagLeft.getPiece().getChessColor() != turnColor))){
            validCells.add(diagLeft);
        }
        return  validCells;
    }

    @Override
    public ChessCellList getThreats(ChessPiece piece, Board board, ChessColor turnColor) {
        return getEatingMoves(piece,board,turnColor,true);
    }

    private void checkEnPassant() {
    }
}
