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
        // TODO implements "en passant rule"
        ChessCellList allCells = board.getChessCells();
        ChessCellList validCells = new  ChessCellList();
        Location start = piece.getLocation();
        ChessCell evaluated;
        if(piece.getChessColor() == ChessColor.WHITE) {
            evaluated = (ChessCell) allCells.getItemByLocation(new Location(start.getX(), 0, start.getZ() + 1));
        }else{
            evaluated = (ChessCell) allCells.getItemByLocation(new Location(start.getX(), 0, start.getZ() - 1));
        }
        if(evaluated != null && evaluated.getPiece() == null){
            validCells.add(evaluated);
        }
        if(piece.isFirstMove()){
            if(piece.getChessColor() == ChessColor.WHITE) {
                evaluated = (ChessCell) allCells.getItemByLocation(new Location(start.getX(), 0, start.getZ() + 2));
            }else{
                evaluated = (ChessCell) allCells.getItemByLocation(new Location(start.getX(), 0, start.getZ() - 2));
            }
            if(evaluated != null && evaluated.getPiece() == null){
                validCells.add(evaluated);
            }
        }
        return  validCells;
    }
}
