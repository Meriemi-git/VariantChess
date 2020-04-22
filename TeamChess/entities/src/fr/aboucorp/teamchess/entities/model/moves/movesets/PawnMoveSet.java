package fr.aboucorp.teamchess.entities.model.moves.movesets;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessCell;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.ChessPiece;
import fr.aboucorp.teamchess.entities.model.Location;
import fr.aboucorp.teamchess.entities.model.utils.ChessCellList;

public class PawnMoveSet extends AbstractMoveSet {
    @Override
    public ChessCellList getMoves(ChessPiece piece, Board board, ChessColor turnColor) {
        ChessCellList validCells = new  ChessCellList();
        Location start = piece.getLocation();
        ChessCell simpleMove;
        ChessCell diagRight;
        ChessCell diagLeft;
        if(piece.getChessColor() == ChessColor.WHITE) {
            simpleMove = (ChessCell) board.getChessCells().getItemByLocation(new Location(start.getX(), 0, start.getZ() + 1));
            diagRight = (ChessCell) board.getChessCells().getItemByLocation(new Location(start.getX()-1,0,start.getZ() + 1));
            diagLeft = (ChessCell) board.getChessCells().getItemByLocation(new Location(start.getX()+1,0,start.getZ() + 1));
        }else{
            simpleMove = (ChessCell) board.getChessCells().getItemByLocation(new Location(start.getX(), 0, start.getZ() - 1));
            diagRight = (ChessCell) board.getChessCells().getItemByLocation(new Location(start.getX()+1,0,start.getZ() - 1));
            diagLeft = (ChessCell) board.getChessCells().getItemByLocation(new Location(start.getX()-1,0,start.getZ() - 1));
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
        if(diagRight != null && diagRight.getPiece() != null && diagRight.getPiece().getChessColor() != turnColor){
            validCells.add(diagRight);
        }
        if(diagLeft != null && diagLeft.getPiece() != null && diagLeft.getPiece().getChessColor() != turnColor){
            validCells.add(diagLeft);
        }
        checkEnPassant();
        return  validCells;
    }

    private void checkEnPassant() {
    }
}
