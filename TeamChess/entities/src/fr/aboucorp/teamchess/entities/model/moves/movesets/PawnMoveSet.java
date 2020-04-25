package fr.aboucorp.teamchess.entities.model.moves.movesets;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessCell;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.ChessPiece;
import fr.aboucorp.teamchess.entities.model.Location;
import fr.aboucorp.teamchess.entities.model.enums.PieceEventType;
import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.entities.model.events.models.PieceEvent;
import fr.aboucorp.teamchess.entities.model.moves.AbstractMoveSet;
import fr.aboucorp.teamchess.entities.model.utils.ChessCellList;

public class PawnMoveSet extends AbstractMoveSet {

    public PawnMoveSet(ChessPiece thisPiece, Board board) {
        super(thisPiece, board);
    }

    @Override
    protected ChessCellList getPossibleMoves(ChessPiece piece, Board board, ChessColor turnColor) {
        ChessCellList validCells = getClassicMoves(piece, board, turnColor);
        validCells.addAll(getEatingMoves(piece, board, turnColor, false));
        return validCells;
    }

    private ChessCellList getClassicMoves(ChessPiece piece, Board board, ChessColor turnColor) {
        ChessCellList classicMoves = new ChessCellList();
        Location start = piece.getLocation();
        ChessCell simpleMove;
        int zpos = piece.getChessColor() == ChessColor.WHITE ? start.getZ() + 1 : start.getZ() - 1;
        simpleMove = (ChessCell) board.getChessCells().getItemByLocation(new Location(start.getX(), 0, zpos));
        if (simpleMove != null && simpleMove.getPiece() == null) {
            classicMoves.add(simpleMove);
            ChessCell doubleMove;
            if (piece.isFirstMove()) {
                zpos = piece.getChessColor() == ChessColor.WHITE ? start.getZ() + 2 : start.getZ() - 2;
                doubleMove = (ChessCell) board.getChessCells().getItemByLocation(new Location(start.getX(), 0, zpos));
                if (doubleMove != null && doubleMove.getPiece() == null) {
                    classicMoves.add(doubleMove);
                }
            }
        }
        return classicMoves;
    }


    private ChessCellList getEatingMoves(ChessPiece piece, Board board, ChessColor turnColor, boolean isThreat) {
        Location start = piece.getLocation();
        ChessCellList validCells = new ChessCellList();
        ChessCell diagRight;
        ChessCell diagLeft;
        int zpos = piece.getChessColor() == ChessColor.WHITE ? 1 : -1;
        diagRight = (ChessCell) board.getChessCells().getItemByLocation(new Location(start.getX() - 1, 0, start.getZ() + zpos));
        diagLeft = (ChessCell) board.getChessCells().getItemByLocation(new Location(start.getX() + 1, 0, start.getZ() + zpos));

        if (diagRight != null
                && (isThreat
                || (diagRight.getPiece() != null && diagRight.getPiece().getChessColor() != turnColor))) {
            validCells.add(diagRight);
        } else if (diagRight != null && diagRight.getPiece() == null && enPassantRightIsPossible()){
            validCells.add(diagRight);
        }

        if (diagLeft != null
                && (isThreat
                || (diagLeft.getPiece() != null && diagLeft.getPiece().getChessColor() != turnColor))) {
            validCells.add(diagLeft);
        }else if(diagLeft != null && diagLeft.getPiece() == null && enPassantLeftIsPossible()){
            validCells.add(diagLeft);
        }
        return validCells;
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
    public ChessCellList getThreats(ChessPiece piece, Board board, ChessColor turnColor) {
        return getEatingMoves(piece, board, turnColor, true);
    }

}
