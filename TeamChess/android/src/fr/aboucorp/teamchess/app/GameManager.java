package fr.aboucorp.teamchess.app;

import com.badlogic.gdx.utils.Array;

import java.util.LinkedList;

import fr.aboucorp.generic.model.Team;
import fr.aboucorp.generic.model.enums.Color;
import fr.aboucorp.teamchess.app.models.enums.GameState;
import fr.aboucorp.teamchess.libgdx.Game3dManager;
import fr.aboucorp.teamchess.libgdx.models.ChessCell;
import fr.aboucorp.teamchess.libgdx.models.ChessModel;
import fr.aboucorp.teamchess.libgdx.models.ChessPiece;
import fr.aboucorp.teamchess.libgdx.models.ChessTurn;

public class GameManager {
    private GameState gameState;
    private Game3dManager game3dManager;
    private LinkedList<ChessTurn> turns;
    private ChessPiece selectedPiece;

    public GameManager(Game3dManager game3dManager) {
        this.game3dManager = game3dManager;
        this.gameState = GameState.SelectPiece;
        this.turns = new LinkedList();
        startGame();
    }

    private void startGame(){
        Team one = new Team("Team white");
        one.setColor(Color.BLACK);
        ChessTurn firstTurn = new ChessTurn(1,one);
        this.turns.add(firstTurn);
    }

    public Array<ChessModel> getPiecesFromActualTurn(){
        if(this.turns.getLast().getTeam().getColor() == Color.BLACK){
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
}
