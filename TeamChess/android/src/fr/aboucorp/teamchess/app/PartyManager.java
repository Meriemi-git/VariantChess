package fr.aboucorp.teamchess.app;

import java.util.ArrayList;

import fr.aboucorp.generic.model.enums.Color;
import fr.aboucorp.teamchess.app.models.enums.GameState;
import fr.aboucorp.teamchess.libgdx.Game3dManager;
import fr.aboucorp.teamchess.libgdx.models.ChessCell;
import fr.aboucorp.teamchess.libgdx.models.ChessModel;
import fr.aboucorp.teamchess.libgdx.models.ChessPiece;

public class PartyManager {
    private GameState gameState;
    private Game3dManager game3dManager;
    private TurnManager turnManager;
    private ChessPiece selectedPiece;


    public PartyManager(Game3dManager game3dManager) {
        this.game3dManager = game3dManager;
        this.gameState = GameState.SelectPiece;
        this.turnManager = new TurnManager();
        startGame();
    }

    private void startGame(){
        this.turnManager.startParty();
    }

    public ArrayList<ChessModel> getPiecesFromActualTurn(){
        if( this.turnManager.getTurnColor() == Color.BLACK){
            return this.game3dManager.getBlackPieces();
        }else {
            return this.game3dManager.getWhitePieces();
        }
     }

    public Game3dManager getGame3dManager() {
        return game3dManager;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void selectPiece(ChessPiece piece) {
        this.game3dManager.selectPiece(piece);
        this.gameState = GameState.SelectCase;
    }

    public void selectCell(ChessCell cell) {
        // TODO Check move validity
        this.game3dManager.movePieceIntoCell(cell);
    }

    public void resetSelection() {
        this.gameState = GameState.SelectPiece;
        this.game3dManager.resetSelection();
    }
}
