package fr.aboucorp.variantchess.entities.moves;

import java.util.ArrayList;
import java.util.List;

import fr.aboucorp.variantchess.entities.enums.PieceId;
import fr.aboucorp.variantchess.entities.Piece;
import fr.aboucorp.variantchess.entities.boards.ClassicBoard;
import fr.aboucorp.variantchess.entities.events.models.CheckInEvent;
import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.Square;
import fr.aboucorp.variantchess.entities.Turn;
import fr.aboucorp.variantchess.entities.enums.BoardEventType;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.events.GameEventSubscriber;
import fr.aboucorp.variantchess.entities.events.models.CheckOutEvent;
import fr.aboucorp.variantchess.entities.events.models.GameEvent;
import fr.aboucorp.variantchess.entities.events.models.PieceEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnStartEvent;
import fr.aboucorp.variantchess.entities.pieces.King;
import fr.aboucorp.variantchess.entities.utils.SquareList;

public abstract class AbstractMoveSet implements GameEventSubscriber {

    protected fr.aboucorp.variantchess.entities.events.GameEventManager eventManager;
    protected final Piece piece;
    protected final ClassicBoard classicBoard;
    protected boolean isChecking;
    protected fr.aboucorp.variantchess.entities.pieces.King kingInCheck;
    protected List<Piece> checkingPieces;
    protected fr.aboucorp.variantchess.entities.utils.SquareList nextMoves;
    protected fr.aboucorp.variantchess.entities.Turn actualTurn;
    protected Turn previousTurn;

    public AbstractMoveSet(Piece piece, ClassicBoard classicBoard) {
        this.piece = piece;
        this.classicBoard = classicBoard;
        this.eventManager = GameEventManager.getINSTANCE();
        this.eventManager.subscribe(fr.aboucorp.variantchess.entities.events.models.PieceEvent.class, this, 1);
        this.eventManager.subscribe(TurnEvent.class, this, 1);
    }

    @Override
    public void receiveGameEvent(fr.aboucorp.variantchess.entities.events.models.GameEvent event) {
        if (event instanceof fr.aboucorp.variantchess.entities.events.models.PieceEvent) {
            if (event instanceof CheckInEvent && ((CheckInEvent) event).piece.getChessColor() == this.piece.getChessColor()) {
                this.isChecking = true;
                this.kingInCheck = (King) ((CheckInEvent) event).piece;
                this.checkingPieces = ((CheckInEvent) event).checkingPieces;
            } else if(event instanceof fr.aboucorp.variantchess.entities.events.models.CheckOutEvent && ((CheckOutEvent) event).piece.getChessColor() == this.piece.getChessColor()) {
                this.isChecking = false;
                this.kingInCheck = null;
                this.checkingPieces = null;
            } else if (((fr.aboucorp.variantchess.entities.events.models.PieceEvent) event).type == BoardEventType.DEATH && ((PieceEvent) event).piece.getPieceId() == this.piece.getPieceId()) {
                this.eventManager.unSubscribe(GameEvent.class, this);
            }
        } else if (event instanceof fr.aboucorp.variantchess.entities.events.models.TurnStartEvent) {
            this.previousTurn = this.actualTurn;
            this.actualTurn = ((fr.aboucorp.variantchess.entities.events.models.TurnStartEvent) event).turn;
            if (((fr.aboucorp.variantchess.entities.events.models.TurnStartEvent) event).turn.getTurnColor() == this.piece.getChessColor()) {
                this.nextMoves = calculateNextMoves(((TurnStartEvent) event).turn.getTurnColor());
            }
        }
    }

    public fr.aboucorp.variantchess.entities.utils.SquareList calculateNextMoves(fr.aboucorp.variantchess.entities.ChessColor turnColor) {
        if (isChecking) {
            return this.calculateUncheckingMoves(turnColor);
        } else {
            return this.getPossibleMoves(this.piece, turnColor);
        }
    }

    private fr.aboucorp.variantchess.entities.utils.SquareList calculateUncheckingMoves(fr.aboucorp.variantchess.entities.ChessColor turnColor) {
        fr.aboucorp.variantchess.entities.utils.SquareList uncheckingMoves = new fr.aboucorp.variantchess.entities.utils.SquareList();
        fr.aboucorp.variantchess.entities.Square originalPosition = this.piece.getSquare();
        fr.aboucorp.variantchess.entities.ChessColor oppositeColor = turnColor == fr.aboucorp.variantchess.entities.ChessColor.WHITE ? fr.aboucorp.variantchess.entities.ChessColor.BLACK : fr.aboucorp.variantchess.entities.ChessColor.WHITE;
        for (fr.aboucorp.variantchess.entities.Square checkedMove : this.getPossibleMoves(this.piece, turnColor)) {
            Piece originalPiece = checkedMove.getPiece();
            if (this.checkingPieces.size() == 1 && checkedMove.equals(this.checkingPieces.get(0).getSquare())) {
                uncheckingMoves.add(checkedMove);
            } else {
                checkedMove.setPiece(this.piece);
                this.piece.move(checkedMove);
                boolean isUnchecking = true;
                for (Piece ckecking : this.checkingPieces) {
                    if (this.kingInCheck.getMoveSet().moveCausingSingleCheck(ckecking, oppositeColor) != null) {
                        isUnchecking = false;
                    }
                }
                if (isUnchecking) {
                    uncheckingMoves.add(checkedMove);
                }
                this.piece.move(originalPosition);
                checkedMove.setPiece(originalPiece);
            }
        }
        return uncheckingMoves;
    }

    public List<Piece> moveCauseCheck(fr.aboucorp.variantchess.entities.ChessColor color) {
        List<Piece> causingChecks = new ArrayList<>();
        for (Piece piece : classicBoard.getPiecesByColor(color)) {
             Piece causingCheck = moveCausingSingleCheck(piece, color);
             if(causingCheck != null) {
                 causingChecks.add(causingCheck);
             }
        }
        return causingChecks;
    }

    private Piece moveCausingSingleCheck(Piece piece, fr.aboucorp.variantchess.entities.ChessColor color) {
        for (Square possibleMove : piece.getMoveSet().getPossibleMoves(piece, color)) {
            if (possibleMove.getPiece() != null) {
                if ((color == fr.aboucorp.variantchess.entities.ChessColor.WHITE && possibleMove.getPiece().getPieceId() == PieceId.BK)
                        || (color == fr.aboucorp.variantchess.entities.ChessColor.BLACK && possibleMove.getPiece().getPieceId() == fr.aboucorp.variantchess.entities.enums.PieceId.WK)) {
                    return piece;
                }
            }
        }
        return null;
    }

    protected abstract fr.aboucorp.variantchess.entities.utils.SquareList getPossibleMoves(Piece piece, fr.aboucorp.variantchess.entities.ChessColor turnColor);

    public abstract fr.aboucorp.variantchess.entities.utils.SquareList getThreats(Piece piece, ChessColor turnColor);

    public SquareList getNextMoves() {
        return nextMoves;
    }
}
