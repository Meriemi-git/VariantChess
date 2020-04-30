package fr.aboucorp.teamchess.app.managers;

import android.util.Log;

import java.util.ArrayList;

import fr.aboucorp.teamchess.app.managers.boards.ClassicBoardManager;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.Location;
import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.Square;
import fr.aboucorp.teamchess.entities.model.enums.BoardEventType;
import fr.aboucorp.teamchess.entities.model.enums.GameState;
import fr.aboucorp.teamchess.entities.model.events.GameEventManager;
import fr.aboucorp.teamchess.entities.model.events.GameEventSubscriber;
import fr.aboucorp.teamchess.entities.model.events.models.GameEvent;
import fr.aboucorp.teamchess.entities.model.events.models.LogEvent;
import fr.aboucorp.teamchess.entities.model.events.models.PartyEvent;
import fr.aboucorp.teamchess.entities.model.events.models.PieceEvent;
import fr.aboucorp.teamchess.libgdx.models.ChessModel;


public class PartyManager implements GameEventSubscriber {
    private final ClassicBoardManager classicBoardManager;
    private GameState gameState;
    private final TurnManager turnManager;
    private GameEventManager eventManager;



    public PartyManager(ClassicBoardManager classicBoardManager) {

        this.classicBoardManager = classicBoardManager;
        this.gameState = GameState.SelectPiece;
        this.turnManager = TurnManager.getINSTANCE();
        this.eventManager = GameEventManager.getINSTANCE();
        this.eventManager.subscribe(PartyEvent.class,this,1);
    }

    public void startGame(){
        this.classicBoardManager.createBoard();
        this.turnManager.newParty(ChessColor.WHITE);
    }

    public void loadBoard(String fenString){
        try {

            ChessColor color = this.classicBoardManager.loadBoard(fenString);
            this.turnManager.newParty(color);
        } catch (Exception e) {
            e.printStackTrace();
            this.eventManager.sendMessage(new LogEvent(String.format("Error during parsing fen string. Message : %s",e.getMessage())));
        }
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
        this.classicBoardManager.selectPiece(touched);
    }

    public void unHightlight() {
        this.gameState = GameState.SelectPiece;
        this.classicBoardManager.unHighlight();
    }

    public ArrayList<ChessModel> getPiecesModelsFromActualTurn(){
        if( this.turnManager.getTurnColor() == ChessColor.BLACK){
            return this.getBlackPieceModels();
        }else {
            return this.getWhitePieceModels();
        }
    }

    public void selectSquare(Square square) {
        this.classicBoardManager.moveToSquare(square);
        this.turnManager.endTurn();
    }


    public ArrayList<ChessModel> getBlackPieceModels() {
        return this.classicBoardManager.getBlackPieceModels();
    }

    public ArrayList<ChessModel> getWhitePieceModels() {
        return  this.classicBoardManager.getWhitePieceModels();
    }

    public Piece getPieceFromLocation(Location location) {
        return this.classicBoardManager.getPieceFromLocation(location,this.turnManager.getTurnColor());
    }

    public Square getSquareFromLocation(Location location) {
       return classicBoardManager.getSquareFromLocation(location);
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
            ChessColor winner = classicBoardManager.getWinner();
            this.eventManager.sendMessage(new PartyEvent(String.format("Game finished ! Winner : %s",winner != null ? winner.name() : "EQUALITY")));
        }
    }

    public ClassicBoardManager getClassicBoardManager() {
        return classicBoardManager;
    }

    public ArrayList<ChessModel> getPossibleSquareModels() {
        return this.classicBoardManager.getPossibleSquareModels();
    }

}
