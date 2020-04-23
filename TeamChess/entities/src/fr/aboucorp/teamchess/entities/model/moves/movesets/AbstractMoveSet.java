package fr.aboucorp.teamchess.entities.model.moves.movesets;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessCell;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.ChessPiece;
import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.entities.model.events.GameEvent;
import fr.aboucorp.teamchess.entities.model.events.GameEventManager;
import fr.aboucorp.teamchess.entities.model.events.GameEventSubscriber;
import fr.aboucorp.teamchess.entities.model.events.PieceEvent;
import fr.aboucorp.teamchess.entities.model.utils.ChessCellList;

public abstract class AbstractMoveSet implements GameEventSubscriber {

    protected GameEventManager eventManager;
    protected boolean isChecking;

    public AbstractMoveSet(){
        this.eventManager = GameEventManager.getINSTANCE();
        this.eventManager.subscribe(PieceEvent.class,this);
    }

    public abstract ChessCellList getPossibleMoves(ChessPiece piece, Board board, ChessColor turnColor);

    public abstract ChessCellList getThreats(ChessPiece piece, Board board, ChessColor turnColor);

    @Override
    public void receiveGameEvent(GameEvent event) {
        if(event instanceof PieceEvent){
            this.isChecking = true;
        }
    }

    public ChessPiece isInCheck(ChessPiece piece, Board board,ChessColor turnColor){
        for (ChessCell possibleMove : getPossibleMoves(piece,board,turnColor)) {
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
}
