package fr.aboucorp.teamchess.app.managers;

import java.util.ArrayList;

import fr.aboucorp.entities.model.ChessCell;
import fr.aboucorp.entities.model.ChessPiece;
import fr.aboucorp.entities.model.enums.GameState;
import fr.aboucorp.teamchess.app.TurnManager;
import fr.aboucorp.teamchess.libgdx.Game3dManager;
import fr.aboucorp.teamchess.libgdx.models.ChessModel;

public class PartyManager {
    private GameState gameState;
    private Game3dManager game3dManager;
    private TurnManager turnManager;
    private BoardManager boardManager;
    private ChessPiece selectedPiece;


    public PartyManager(Game3dManager game3dManager) {
        this.game3dManager = game3dManager;
        this.gameState = GameState.SelectPiece;
        this.turnManager = new TurnManager();
        this.boardManager = new BoardManager();
        startGame();
    }

    private void startGame(){
        this.boardManager.createBoard();
        this.turnManager.startParty();

        this.game3dManager.startParty();
    }

    public ArrayList<ChessModel> getPiecesFromActualTurn(){
        if( this.turnManager.getTurnColor() == ChessColor.BLACK){
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
