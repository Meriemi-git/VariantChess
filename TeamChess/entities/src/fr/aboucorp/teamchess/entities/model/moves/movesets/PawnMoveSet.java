package fr.aboucorp.teamchess.entities.model.moves.movesets;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.Location;
import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.Square;
import fr.aboucorp.teamchess.entities.model.enums.PieceEventType;
import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.entities.model.events.models.PieceEvent;
import fr.aboucorp.teamchess.entities.model.moves.AbstractMoveSet;
import fr.aboucorp.teamchess.entities.model.utils.SquareList;

public class PawnMoveSet extends AbstractMoveSet {

    public PawnMoveSet(Piece thisPiece, Board board) {
        super(thisPiece, board);
    }

    @Override
    protected SquareList getPossibleMoves(Piece piece, Board board, ChessColor turnColor) {
        SquareList validSquares = getClassicMoves(piece, board, turnColor);
        validSquares.addAll(getEatingMoves(piece, board, turnColor, false));
        return validSquares;
    }

    private SquareList getClassicMoves(Piece piece, Board board, ChessColor turnColor) {
        SquareList classicMoves = new SquareList();
        Location start = piece.getLocation();
        Square simpleMove;
        int zpos = piece.getChessColor() == ChessColor.WHITE ? start.getZ() + 1 : start.getZ() - 1;
        simpleMove = (Square) board.getSquares().getItemByLocation(new Location(start.getX(), 0, zpos));
        if (simpleMove != null && simpleMove.getPiece() == null) {
            classicMoves.add(simpleMove);
            Square doubleMove;
            if (piece.isFirstMove()) {
                zpos = piece.getChessColor() == ChessColor.WHITE ? start.getZ() + 2 : start.getZ() - 2;
                doubleMove = (Square) board.getSquares().getItemByLocation(new Location(start.getX(), 0, zpos));
                if (doubleMove != null && doubleMove.getPiece() == null) {
                    classicMoves.add(doubleMove);
                }
            }
        }
        return classicMoves;
    }


    private SquareList getEatingMoves(Piece piece, Board board, ChessColor turnColor, boolean isThreat) {
        Location start = piece.getLocation();
        SquareList validSquares = new SquareList();
        Square diagRight;
        Square diagLeft;
        int zpos = piece.getChessColor() == ChessColor.WHITE ? 1 : -1;
        diagRight = (Square) board.getSquares().getItemByLocation(new Location(start.getX() - 1, 0, start.getZ() + zpos));
        diagLeft = (Square) board.getSquares().getItemByLocation(new Location(start.getX() + 1, 0, start.getZ() + zpos));

        if (diagRight != null
                && (isThreat
                || (diagRight.getPiece() != null && diagRight.getPiece().getChessColor() != turnColor))) {
            validSquares.add(diagRight);
        } else if (diagRight != null && diagRight.getPiece() == null && enPassantRightIsPossible()){
            validSquares.add(diagRight);
        }

        if (diagLeft != null
                && (isThreat
                || (diagLeft.getPiece() != null && diagLeft.getPiece().getChessColor() != turnColor))) {
            validSquares.add(diagLeft);
        }else if(diagLeft != null && diagLeft.getPiece() == null && enPassantLeftIsPossible()){
            validSquares.add(diagLeft);
        }
        return validSquares;
    }

    private boolean enPassantRightIsPossible() {
        if (enPassantIsPossible() && this.thisPiece.getLocation().getZ() == this.previousTurn.to.getLocation().getZ()) {
            this.eventManager.sendMessage(new PieceEvent("En passant", PieceEventType.EN_PASSANT, this.thisPiece));
            return true;
        }
        return false;
    }

    private boolean enPassantLeftIsPossible() {
        if (enPassantIsPossible() && this.thisPiece.getLocation().getZ() == this.previousTurn.to.getLocation().getZ()) {
            this.eventManager.sendMessage(new PieceEvent("En passant", PieceEventType.EN_PASSANT, this.thisPiece));
            return true;
        }
        return false;
    }

    private boolean enPassantIsPossible() {
        return ((this.thisPiece.getChessColor() == ChessColor.WHITE && this.thisPiece.getLocation().getZ() == 4)
                || (this.thisPiece.getChessColor() == ChessColor.BLACK && this.thisPiece.getLocation().getZ() == 3))
                && PieceId.isPawn(this.previousTurn.played.getPieceId())
                && Math.abs(this.previousTurn.to.getLocation().getZ() - this.previousTurn.from.getLocation().getZ()) == 2;
    }

    @Override
    public SquareList getThreats(Piece piece, Board board, ChessColor turnColor) {
        return getEatingMoves(piece, board, turnColor, true);
    }

}
