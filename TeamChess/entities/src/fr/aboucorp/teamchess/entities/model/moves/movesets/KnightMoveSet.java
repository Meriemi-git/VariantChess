package fr.aboucorp.teamchess.entities.model.moves.movesets;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.Square;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.Location;
import fr.aboucorp.teamchess.entities.model.moves.AbstractMoveSet;
import fr.aboucorp.teamchess.entities.model.utils.ChessCellList;

public class KnightMoveSet extends AbstractMoveSet {

    public KnightMoveSet(Piece thisPiece, Board board) {
        super(thisPiece, board);
    }

    @Override
    protected ChessCellList getPossibleMoves(Piece piece, Board board, ChessColor turnColor) {
        ChessCellList validCells = new ChessCellList();
        Location start = piece.getLocation();

        for (Square cell : board.getChessCells()) {
            Location end = cell.getLocation();
            if (end.getX() == start.getX() + 2
                    && (end.getZ() == start.getZ() + 1
                    || end.getZ() == start.getZ() - 1)) {
                if(cell.getPiece() == null ||  cell.getPiece().getChessColor() != turnColor) {
                    validCells.add(cell);
                }
            }
            if (end.getX() == start.getX() - 2
                    && (end.getZ() == start.getZ() + 1
                    || end.getZ() == start.getZ() - 1)) {
                if(cell.getPiece() == null || cell.getPiece().getChessColor() != turnColor) {
                    validCells.add(cell);
                }
            }
            if (end.getZ() == start.getZ() + 2
                    && (end.getX() == start.getX() + 1
                    || end.getX() == start.getX() - 1)) {
                if(cell.getPiece() == null || cell.getPiece().getChessColor() != turnColor) {
                    validCells.add(cell);
                }
            }
            if (end.getZ() == start.getZ() - 2
                    && (end.getX() == start.getX() + 1
                    || end.getX() == start.getX() - 1)) {
                if(cell.getPiece() == null || cell.getPiece().getChessColor() != turnColor) {
                    validCells.add(cell);
                }
            }
        }
        return  validCells;
    }

    @Override
    public ChessCellList getThreats(Piece piece, Board board, ChessColor turnColor) {
        return getPossibleMoves(piece,board,turnColor);
    }
}
