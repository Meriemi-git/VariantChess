package fr.aboucorp.teamchess.app.managers;

import android.util.Log;

import java.util.ArrayList;

import fr.aboucorp.teamchess.entities.model.ChessCell;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.ChessPiece;
import fr.aboucorp.teamchess.entities.model.Location;
import fr.aboucorp.teamchess.entities.model.Team;
import fr.aboucorp.teamchess.entities.model.enums.GameState;
import fr.aboucorp.teamchess.entities.model.events.GameEvent;
import fr.aboucorp.teamchess.entities.model.events.GameEventManager;
import fr.aboucorp.teamchess.entities.model.events.GameEventSubscriber;
import fr.aboucorp.teamchess.entities.model.events.PartyEvent;
import fr.aboucorp.teamchess.libgdx.models.ChessModel;


public class PartyManager implements GameEventSubscriber {
    private final BoardManager boardManager;
    private GameState gameState;
    private final TurnManager turnManager;
    private GameEventManager eventManager;



    public PartyManager(BoardManager boardManager) {
        Team white = new Team("white",ChessColor.WHITE);
        Team black = new Team("white",ChessColor.BLACK);
        this.boardManager = boardManager;
        this.gameState = GameState.SelectPiece;
        this.turnManager = new TurnManager(white,black);
        this.eventManager = GameEventManager.getINSTANCE();
        this.eventManager.subscribe(PartyEvent.class,this);
    }

    public void startGame(){
        this.boardManager.createBoard();
        this.turnManager.nextTurn();
    }


    public void selectPiece(ChessPiece touched) {
        this.gameState = GameState.SelectCase;
        this.boardManager.selectPiece(touched);
    }

    public void resetSelection() {
        this.gameState = GameState.SelectPiece;
        this.boardManager.resetSelection();
    }

    public ArrayList<ChessModel> getPiecesModelsFromActualTurn(){
        if( this.turnManager.getTurnColor() == ChessColor.BLACK){
            return this.getBlackPieceModels();
        }else {
            return this.getWhitePieceModels();
        }
    }

    public void selectCell(ChessCell chessCell) {
        this.boardManager.moveSelectedPieceToCell(chessCell);
    }


    public ArrayList<ChessModel> getBlackPieceModels() {
        return this.boardManager.getBlackPieceModels();
    }

    public ArrayList<ChessModel> getWhitePieceModels() {
        return  this.boardManager.getWhitePieceModels();
    }

    public ChessPiece getPieceFromLocation(Location location) {
        return this.boardManager.getPieceFromLocation(location,this.turnManager.getTurnColor());
    }

    public ChessCell getCellFromLocation(Location location) {
       return boardManager.getCellFromLocation(location);
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public ArrayList<ChessModel> getChessCellModels() {
        return this.boardManager.getChessCellModels();
    }

    @Override
    public void receiveGameEvent(GameEvent event) {
        // TODO react to game event
        Log.i("fr.aboucorp.teamchess","GameManager receive PartyEvent");
    }

    public BoardManager getBoardManager() {
        return boardManager;
    }

    public ArrayList<ChessModel> getPossibleCellModels() {
        return this.boardManager.getPossibleCellModels();
    }
}
