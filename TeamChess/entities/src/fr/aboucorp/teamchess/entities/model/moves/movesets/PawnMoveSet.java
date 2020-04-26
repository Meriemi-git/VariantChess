package fr.aboucorp.teamchess.entities.model.moves.movesets;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.Location;
import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.Square;
import fr.aboucorp.teamchess.entities.model.enums.BoardEventType;
import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.entities.model.events.models.PieceEvent;
import fr.aboucorp.teamchess.entities.model.moves.AbstractMoveSet;
import fr.aboucorp.teamchess.entities.model.utils.SquareList;

public class PawnMoveSet extends AbstractMoveSet {

    public PawnMoveSet(Piece thisPiece, Board board) {
        super(thisPiece, board);
    }

    @Override
    protected SquareList getPossibleMoves(Piece piece, ChessColor turnColor) {
        SquareList validSquares = getClassicMoves(piece, turnColor);
        validSquares.addAll(getEatingMoves(piece,  turnColor, false));
        return validSquares;
    }

    private SquareList getClassicMoves(Piece piece, ChessColor turnColor) {
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


    private SquareList getEatingMoves(Piece piece, ChessColor turnColor, boolean isThreat) {
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
        if (enPassantIsPossible() && this.piece.getLocation().getZ() == this.previousTurn.to.getLocation().getZ()) {
            this.eventManager.sendMessage(new PieceEvent("En passant", BoardEventType.EN_PASSANT, this.piece));
            return true;
        }
        return false;
    }

    private boolean enPassantLeftIsPossible() {
        if (enPassantIsPossible() && this.piece.getLocation().getZ() == this.previousTurn.to.getLocation().getZ()) {
            this.eventManager.sendMessage(new PieceEvent("En passant", BoardEventType.EN_PASSANT, this.piece));
            return true;
        }
        return false;
    }

    private boolean enPassantIsPossible() {
        return ((this.piece.getChessColor() == ChessColor.WHITE && this.piece.getLocation().getZ() == 4)
                || (this.piece.getChessColor() == ChessColor.BLACK && this.piece.getLocation().getZ() == 3))
                && PieceId.isPawn(this.previousTurn.played.getPieceId())
                && Math.abs(this.previousTurn.to.getLocation().getZ() - this.previousTurn.from.getLocation().getZ()) == 2;
    }

    @Override
    public SquareList getThreats(Piece piece, ChessColor turnColor) {
        return getEatingMoves(piece, turnColor, true);
    }

}
