package fr.aboucorp.teamchess.entities.model.moves.movesets;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.Location;
import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.Square;
import fr.aboucorp.teamchess.entities.model.moves.AbstractMoveSet;
import fr.aboucorp.teamchess.entities.model.utils.SquareList;

public class KnightMoveSet extends AbstractMoveSet {

    public KnightMoveSet(Piece thisPiece, Board board) {
        super(thisPiece, board);
    }

    @Override
    protected SquareList getPossibleMoves(Piece piece, ChessColor turnColor) {
        SquareList validSquares = new SquareList();
        Location start = piece.getLocation();

        for (Square square : board.getSquares()) {
            Location end = square.getLocation();
            if (end.getX() == start.getX() + 2
                    && (end.getZ() == start.getZ() + 1
                    || end.getZ() == start.getZ() - 1)) {
                if(square.getPiece() == null ||  square.getPiece().getChessColor() != turnColor) {
                    validSquares.add(square);
                }
            }
            if (end.getX() == start.getX() - 2
                    && (end.getZ() == start.getZ() + 1
                    || end.getZ() == start.getZ() - 1)) {
                if(square.getPiece() == null || square.getPiece().getChessColor() != turnColor) {
                    validSquares.add(square);
                }
            }
            if (end.getZ() == start.getZ() + 2
                    && (end.getX() == start.getX() + 1
                    || end.getX() == start.getX() - 1)) {
                if(square.getPiece() == null || square.getPiece().getChessColor() != turnColor) {
                    validSquares.add(square);
                }
            }
            if (end.getZ() == start.getZ() - 2
                    && (end.getX() == start.getX() + 1
                    || end.getX() == start.getX() - 1)) {
                if(square.getPiece() == null || square.getPiece().getChessColor() != turnColor) {
                    validSquares.add(square);
                }
            }
        }
        return  validSquares;
    }

    @Override
    public SquareList getThreats(Piece piece, ChessColor turnColor) {
        return getPossibleMoves(piece,turnColor);
    }
}
