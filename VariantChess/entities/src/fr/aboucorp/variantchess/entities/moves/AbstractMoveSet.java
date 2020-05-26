package fr.aboucorp.variantchess.entities.moves;

import java.util.ArrayList;
import java.util.List;

import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.Piece;
import fr.aboucorp.variantchess.entities.Square;
import fr.aboucorp.variantchess.entities.Turn;
import fr.aboucorp.variantchess.entities.boards.ClassicBoard;
import fr.aboucorp.variantchess.entities.enums.BoardEventType;
import fr.aboucorp.variantchess.entities.enums.PieceId;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.events.GameEventSubscriber;
import fr.aboucorp.variantchess.entities.events.models.CheckInEvent;
import fr.aboucorp.variantchess.entities.events.models.CheckOutEvent;
import fr.aboucorp.variantchess.entities.events.models.GameEvent;
import fr.aboucorp.variantchess.entities.events.models.PieceEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnStartEvent;
import fr.aboucorp.variantchess.entities.moves.movesets.KingMoveSet;
import fr.aboucorp.variantchess.entities.utils.GameElementList;
import fr.aboucorp.variantchess.entities.utils.PieceList;
import fr.aboucorp.variantchess.entities.utils.SquareList;

public abstract class AbstractMoveSet implements GameEventSubscriber {

    protected final GameEventManager eventManager;
    protected final Piece piece;
    protected final ClassicBoard classicBoard;
    protected boolean isChecking;
    protected Turn actualTurn;
    protected Turn previousTurn;
    private PieceId kingInCheck;
    private List<Piece> checkingPieces;
    private SquareList nextMoves;

    protected AbstractMoveSet(Piece piece, ClassicBoard classicBoard, GameEventManager gameEventManager) {
        this.piece = piece;
        this.classicBoard = classicBoard;
        this.eventManager = gameEventManager;
        this.eventManager.subscribe(PieceEvent.class, this, 1);
        this.eventManager.subscribe(TurnEvent.class, this, 1);
    }

    @Override
    public void receiveGameEvent(GameEvent event) {
        if (event instanceof PieceEvent) {
            if (event instanceof CheckInEvent && PieceId.getColor(((CheckInEvent) event).played) == this.piece.getChessColor()) {
                this.isChecking = true;
                this.kingInCheck = ((CheckInEvent) event).played;
                this.checkingPieces = ((CheckInEvent) event).checkingPieces;
            } else if (event instanceof CheckOutEvent && PieceId.getColor(((CheckOutEvent) event).played) == this.piece.getChessColor()) {
                this.isChecking = false;
                this.kingInCheck = null;
                this.checkingPieces = null;
            } else if (((PieceEvent) event).type == BoardEventType.DEATH && ((PieceEvent) event).played == this.piece.getPieceId()) {
                this.eventManager.unSubscribe(GameEvent.class, this);
            }
        } else if (event instanceof TurnStartEvent) {
            this.previousTurn = this.actualTurn;
            this.actualTurn = ((TurnStartEvent) event).turn;
            if (((TurnStartEvent) event).turn.getTurnColor() == this.piece.getChessColor()) {
                this.nextMoves = this.calculateNextMoves(((TurnStartEvent) event).turn.getTurnColor());
            }
        }
    }

    public SquareList calculateNextMoves(ChessColor turnColor) {
        if (this.isChecking) {
            return this.calculateUncheckingMoves(turnColor);
        } else {
            return this.getPossibleMoves(this.piece, turnColor);
        }
    }

    private SquareList calculateUncheckingMoves(ChessColor turnColor) {
        SquareList uncheckingMoves = new SquareList();
        Square originalPosition = this.piece.getSquare();
        ChessColor oppositeColor = turnColor == ChessColor.WHITE ? ChessColor.BLACK : ChessColor.WHITE;
        KingMoveSet kingMoveSet = (KingMoveSet) this.classicBoard.getPieceById(this.kingInCheck).getMoveSet();
        for (Square checkedMove : this.getPossibleMoves(this.piece, turnColor)) {
            Piece originalPiece = checkedMove.getPiece();
            if (this.checkingPieces.size() == 1 && checkedMove.equals(this.checkingPieces.get(0).getSquare())) {
                uncheckingMoves.add(checkedMove);
            } else {
                checkedMove.setPiece(this.piece);
                this.piece.move(checkedMove);
                boolean isUnchecking = true;
                for (Piece ckecking : this.checkingPieces) {
                    if (kingMoveSet.moveCausingSingleCheck(ckecking, oppositeColor) != null) {
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

    protected abstract SquareList getPossibleMoves(Piece piece, ChessColor turnColor);

    protected Piece moveCausingSingleCheck(Piece piece, ChessColor color) {
        for (Square possibleMove : piece.getMoveSet().getPossibleMoves(piece, color)) {
            if (possibleMove.getPiece() != null) {
                if ((color == ChessColor.WHITE && possibleMove.getPiece().getPieceId() == PieceId.BK)
                        || (color == ChessColor.BLACK && possibleMove.getPiece().getPieceId() == PieceId.WK)) {
                    return piece;
                }
            }
        }
        return null;
    }

    public GameElementList<Piece> moveCauseCheck(ChessColor color) {
        GameElementList<Piece> causingChecks = new PieceList();
        for (Piece piece : this.classicBoard.getPiecesByColor(color)) {
            Piece causingCheck = this.moveCausingSingleCheck(piece, color);
            if (causingCheck != null) {
                causingChecks.add(causingCheck);
            }
        }
        return causingChecks;
    }

    public abstract SquareList getThreats(Piece piece, ChessColor turnColor);

    public SquareList getNextMoves() {
        return this.nextMoves;
    }

    public void clear() {
        this.isChecking = false;
        this.kingInCheck = null;
        this.checkingPieces = new ArrayList<>();
        this.nextMoves = new SquareList();
        this.actualTurn = null;
        this.previousTurn = null;
    }
}
