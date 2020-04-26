package fr.aboucorp.teamchess.app.managers;

import android.util.Log;

import java.util.ArrayList;

import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.Location;
import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.Square;
import fr.aboucorp.teamchess.entities.model.enums.BoardEventType;
import fr.aboucorp.teamchess.entities.model.enums.GameState;
import fr.aboucorp.teamchess.entities.model.events.GameEventManager;
import fr.aboucorp.teamchess.entities.model.events.GameEventSubscriber;
import fr.aboucorp.teamchess.entities.model.events.models.GameEvent;
import fr.aboucorp.teamchess.entities.model.events.models.PartyEvent;
import fr.aboucorp.teamchess.entities.model.events.models.PieceEvent;
import fr.aboucorp.teamchess.libgdx.models.ChessModel;


public class PartyManager implements GameEventSubscriber {
    private final BoardManager boardManager;
    private GameState gameState;
    private final TurnManager turnManager;
    private GameEventManager eventManager;



    public PartyManager(BoardManager boardManager) {

        this.boardManager = boardManager;
        this.gameState = GameState.SelectPiece;
        this.turnManager = TurnManager.getINSTANCE();
        this.eventManager = GameEventManager.getINSTANCE();
        this.eventManager.subscribe(PartyEvent.class,this,1);
    }

    public void startGame(){
        this.boardManager.createBoard();
        this.turnManager.startTurn();
    }

    public void endTurn() {
        this.turnManager.endTurn();
        this.turnManager.startTurn();
    }



    public String getPartyInfos(){
        return this.turnManager.getTurnColor().name();
    }

    public void selectPiece(Piece touched) {
        this.gameState = GameState.SelectCase;
        this.boardManager.selectPiece(touched);
    }

    public void unHightlight() {
        this.gameState = GameState.SelectPiece;
        this.boardManager.unHighlight();
    }

    public ArrayList<ChessModel> getPiecesModelsFromActualTurn(){
        if( this.turnManager.getTurnColor() == ChessColor.BLACK){
            return this.getBlackPieceModels();
        }else {
            return this.getWhitePieceModels();
        }
    }

    public void selectSquare(Square square) {
        this.boardManager.moveToSquare(square);
        this.turnManager.endTurn();
    }


    public ArrayList<ChessModel> getBlackPieceModels() {
        return this.boardManager.getBlackPieceModels();
    }

    public ArrayList<ChessModel> getWhitePieceModels() {
        return  this.boardManager.getWhitePieceModels();
    }

    public Piece getPieceFromLocation(Location location) {
        return this.boardManager.getPieceFromLocation(location,this.turnManager.getTurnColor());
    }

    public Square getSquareFromLocation(Location location) {
       return boardManager.getSquareFromLocation(location);
    }

    public GameState getGameState() {
        return gameState;
    }


    @Override
    public void receiveGameEvent(GameEvent event) {
        if(event instanceof PartyEvent){
            // TODO display draw claim option
            Log.i("fr.aboucorp.teamchess",event.message);
        }else if(event instanceof PieceEvent && ((PieceEvent) event).type == BoardEventType.CHECKMATE){
            ChessColor winner = boardManager.getWinner();
            this.eventManager.sendMessage(new PartyEvent(String.format("Game finished ! Winner : %s",winner != null ? winner.name() : "EQUALITY")));
        }
    }

    public BoardManager getBoardManager() {
        return boardManager;
    }

    public ArrayList<ChessModel> getPossibleSquareModels() {
        return this.boardManager.getPossibleSquareModels();
    }

}
