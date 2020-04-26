package fr.aboucorp.teamchess.entities.model.moves;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.Square;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.Turn;
import fr.aboucorp.teamchess.entities.model.enums.PieceEventType;
import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.entities.model.events.GameEventManager;
import fr.aboucorp.teamchess.entities.model.events.GameEventSubscriber;
import fr.aboucorp.teamchess.entities.model.events.models.CheckEvent;
import fr.aboucorp.teamchess.entities.model.events.models.GameEvent;
import fr.aboucorp.teamchess.entities.model.events.models.LogEvent;
import fr.aboucorp.teamchess.entities.model.events.models.PieceEvent;
import fr.aboucorp.teamchess.entities.model.events.models.TurnEvent;
import fr.aboucorp.teamchess.entities.model.events.models.TurnStartEvent;
import fr.aboucorp.teamchess.entities.model.pieces.King;
import fr.aboucorp.teamchess.entities.model.utils.ChessCellList;

public abstract class AbstractMoveSet implements GameEventSubscriber {

    protected GameEventManager eventManager;
    protected final Piece thisPiece;
    protected final Board board;
    protected boolean isChecking;
    protected King kingInCheck;
    protected Piece checkingPiece;
    protected ChessCellList nextMoves;
    protected Turn actualTurn;
    protected Turn previousTurn;

    public AbstractMoveSet(Piece thisPiece, Board board){
        this.thisPiece = thisPiece;
        this.board = board;
        this.eventManager = GameEventManager.getINSTANCE();
        this.eventManager.subscribe(PieceEvent.class,this);
        this.eventManager.subscribe(TurnEvent.class,this);
    }

    @Override
    public void receiveGameEvent(GameEvent event) {
        if(event instanceof PieceEvent ){
            if(event instanceof CheckEvent && ((CheckEvent) event).piece.getChessColor() == this.thisPiece.getChessColor()) {
                this.isChecking = true;
                this.kingInCheck = (King) ((CheckEvent) event).piece;
                this.checkingPiece = ((CheckEvent) event).checkingPiece;
            }else if(((PieceEvent) event).type == PieceEventType.DEATH && ((PieceEvent) event).piece.getPieceId() == this.thisPiece.getPieceId()){
                this.eventManager.unSubscribe(GameEvent.class,this);
            }
        }else if(event instanceof TurnStartEvent){
            this.previousTurn = this.actualTurn;
            this.actualTurn = ((TurnStartEvent) event).turn;
            if(((TurnStartEvent) event).turn.getTurnColor() == this.thisPiece.getChessColor()){
                this.nextMoves = calculateNextMoves(this.board,((TurnStartEvent) event).turn.getTurnColor());
            }
        }
    }

    public ChessCellList calculateNextMoves(Board board, ChessColor turnColor){
        if(isChecking){
           return this.calculateUncheckingMoves(board,turnColor);
        }else{
           return this.getPossibleMoves(this.thisPiece,this.board,turnColor);
        }
    }

    private ChessCellList calculateUncheckingMoves(Board board, ChessColor turnColor){
        ChessCellList uncheckingMoves = new ChessCellList();
        Square originalPosition = this.thisPiece.getActualSquare();
        ChessColor oppositeColor = turnColor == ChessColor.WHITE ? ChessColor.BLACK : ChessColor.WHITE;
        for (Square checkedMove : this.getPossibleMoves(this.thisPiece, this.board, turnColor)) {
            Piece originalPiece = checkedMove.getPiece();
            if(checkedMove.equals(this.checkingPiece.getActualSquare())){
                uncheckingMoves.add(checkedMove);
            }else{
                checkedMove.setPiece(this.thisPiece);
                this.thisPiece.move(checkedMove);
                if(this.kingInCheck.getMoveSet().pieceMoveCauseCheck(this.checkingPiece,board,oppositeColor) == null){
                    uncheckingMoves.add(checkedMove);
                }
                this.thisPiece.move(originalPosition);
                checkedMove.setPiece(originalPiece);
            }
        }
        String logMessage = String.format("%s: ischecking : %s; turncolor : %s; moves : %s",this.thisPiece.getPieceId(),isChecking,turnColor.name(),uncheckingMoves.size());
        this.eventManager.sendMessage(new LogEvent(logMessage));
        return uncheckingMoves;
    }

    public Piece pieceMoveCauseCheck(Piece piece, Board board, ChessColor turnColor){
        for (Square possibleMove : piece.getMoveSet().getPossibleMoves(piece,board,turnColor)) {
            if(possibleMove.getPiece() != null){
                if(turnColor == ChessColor.WHITE && possibleMove.getPiece().getPieceId() == PieceId.BK){
                    return possibleMove.getPiece();
                }else if(turnColor == ChessColor.BLACK && possibleMove.getPiece().getPieceId() == PieceId.WK){
                    return possibleMove.getPiece();
                }
            }
        }
        return null;
    }

    protected abstract ChessCellList getPossibleMoves(Piece piece, Board board, ChessColor turnColor);

    public abstract ChessCellList getThreats(Piece piece, Board board, ChessColor turnColor);

    public ChessCellList getNextMoves() {
        return nextMoves;
    }
}
