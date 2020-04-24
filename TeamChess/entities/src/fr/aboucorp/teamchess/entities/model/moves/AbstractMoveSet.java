package fr.aboucorp.teamchess.entities.model.moves;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessCell;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.ChessPiece;
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
    protected final ChessPiece thisPiece;
    protected final Board board;
    private boolean isChecking;
    private King kingInCheck;
    private ChessPiece checkingPiece;
    private ChessCellList nextMoves;

    public AbstractMoveSet(ChessPiece thisPiece, Board board){
        this.thisPiece = thisPiece;
        this.board = board;
        this.eventManager = GameEventManager.getINSTANCE();
        this.eventManager.subscribe(PieceEvent.class,this);
        this.eventManager.subscribe(TurnEvent.class,this);
    }

    protected abstract ChessCellList getPossibleMoves(ChessPiece piece, Board board, ChessColor turnColor);

    public abstract ChessCellList getThreats(ChessPiece piece, Board board, ChessColor turnColor);

    @Override
    public void receiveGameEvent(GameEvent event) {
        if(event instanceof PieceEvent ){
            if(event instanceof CheckEvent && ((CheckEvent) event).piece.getChessColor() == this.thisPiece.getChessColor()) {
                this.isChecking = true;
                this.kingInCheck = (King) ((CheckEvent) event).piece;
                this.checkingPiece = ((CheckEvent) event).checkingPiece;
            }else if(((PieceEvent) event).type == PieceEventType.DEATH){
                if(((PieceEvent) event).piece.getLocation() == this.thisPiece.getLocation()){
                    this.eventManager.unSubscribe(GameEvent.class,this);
                }
            }
        }else if(event instanceof TurnStartEvent && ((TurnStartEvent) event).turn.getTurnColor() == this.thisPiece.getChessColor()){
            this.nextMoves = calculateNextMoves(this.board,((TurnStartEvent) event).turn.getTurnColor());
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
        ChessCell originalPosition = this.thisPiece.getActualCell();
        ChessColor oppositeColor = turnColor == ChessColor.WHITE ? ChessColor.BLACK : ChessColor.WHITE;
        for (ChessCell checkedMove : this.getPossibleMoves(this.thisPiece, this.board, turnColor)) {
            ChessPiece originalPiece = checkedMove.getPiece();
            if(checkedMove.equals(this.checkingPiece.getActualCell())){
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


    public ChessPiece pieceMoveCauseCheck(ChessPiece piece, Board board, ChessColor turnColor){
        for (ChessCell possibleMove : piece.getMoveSet().getPossibleMoves(piece,board,turnColor)) {
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

    public ChessCellList getNextMoves() {
        return nextMoves;
    }
}
