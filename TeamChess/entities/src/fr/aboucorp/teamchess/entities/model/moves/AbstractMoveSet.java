package fr.aboucorp.teamchess.entities.model.moves;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.Square;
import fr.aboucorp.teamchess.entities.model.Turn;
import fr.aboucorp.teamchess.entities.model.enums.BoardEventType;
import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.entities.model.events.GameEventManager;
import fr.aboucorp.teamchess.entities.model.events.GameEventSubscriber;
import fr.aboucorp.teamchess.entities.model.events.models.CheckInEvent;
import fr.aboucorp.teamchess.entities.model.events.models.GameEvent;
import fr.aboucorp.teamchess.entities.model.events.models.PieceEvent;
import fr.aboucorp.teamchess.entities.model.events.models.TurnEvent;
import fr.aboucorp.teamchess.entities.model.events.models.TurnStartEvent;
import fr.aboucorp.teamchess.entities.model.pieces.King;
import fr.aboucorp.teamchess.entities.model.utils.SquareList;

public abstract class AbstractMoveSet implements GameEventSubscriber {

    protected GameEventManager eventManager;
    protected final Piece piece;
    protected final Board board;
    protected boolean isChecking;
    protected King kingInCheck;
    protected List<Piece> checkingPieces;
    protected SquareList nextMoves;
    protected Turn actualTurn;
    protected Turn previousTurn;

    public AbstractMoveSet(Piece piece, Board board) {
        this.piece = piece;
        this.board = board;
        this.eventManager = GameEventManager.getINSTANCE();
        this.eventManager.subscribe(PieceEvent.class, this, 1);
        this.eventManager.subscribe(TurnEvent.class, this, 1);
    }

    @Override
    public void receiveGameEvent(GameEvent event) {
        if (event instanceof PieceEvent) {
            if (event instanceof CheckInEvent && ((CheckInEvent) event).piece.getChessColor() == this.piece.getChessColor()) {
                this.isChecking = true;
                this.kingInCheck = (King) ((CheckInEvent) event).piece;
                this.checkingPieces = ((CheckInEvent) event).checkingPieces;
            } else if (((PieceEvent) event).type == BoardEventType.DEATH && ((PieceEvent) event).piece.getPieceId() == this.piece.getPieceId()) {
                this.eventManager.unSubscribe(GameEvent.class, this);
            }
        } else if (event instanceof TurnStartEvent) {
            this.previousTurn = this.actualTurn;
            this.actualTurn = ((TurnStartEvent) event).turn;
            if (((TurnStartEvent) event).turn.getTurnColor() == this.piece.getChessColor()) {
                this.nextMoves = calculateNextMoves(((TurnStartEvent) event).turn.getTurnColor());
            }
        }
    }

    public SquareList calculateNextMoves(ChessColor turnColor) {
        if (isChecking) {
            return this.calculateUncheckingMoves(turnColor);
        } else {
            return this.getPossibleMoves(this.piece, turnColor);
        }
    }

    private SquareList calculateUncheckingMoves(ChessColor turnColor) {
        SquareList uncheckingMoves = new SquareList();
        Square originalPosition = this.piece.getActualSquare();
        ChessColor oppositeColor = turnColor == ChessColor.WHITE ? ChessColor.BLACK : ChessColor.WHITE;
        for (Square checkedMove : this.getPossibleMoves(this.piece, turnColor)) {
            Piece originalPiece = checkedMove.getPiece();
            if (this.checkingPieces.size() == 1 && checkedMove.equals(this.checkingPieces.get(0).getActualSquare())) {
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


    public List<Piece> moveCauseCheck(ChessColor color) {
        List<Piece> causingChecks = new ArrayList<>();
        for (Piece piece : board.getPiecesByColor(color)) {
             Piece causingCheck = moveCausingSingleCheck(piece, color);
             if(causingCheck != null) {
                 causingChecks.add(causingCheck);
             }
        }
        return causingChecks;
    }

    private Piece moveCausingSingleCheck(Piece piece, ChessColor color) {
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

    private List<Piece> indirectChecks(Piece piece, ChessColor turnColor) {

        return board.getPiecesByColor(turnColor)
                .stream()
                .filter(p ->
                        p.getMoveSet().getNextMoves().contains(piece.getActualSquare())
                                && pieceCauseCheck(p, turnColor))
                .collect(Collectors.toList());
    }

    private boolean pieceCauseCheck(Piece piece, ChessColor turnColor) {
        return false;
    }


    protected abstract SquareList getPossibleMoves(Piece piece, ChessColor turnColor);

    public abstract SquareList getThreats(Piece piece, ChessColor turnColor);

    public SquareList getNextMoves() {
        return nextMoves;
    }
}
