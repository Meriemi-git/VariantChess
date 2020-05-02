package fr.aboucorp.teamchess.app.managers.boards;

import com.badlogic.gdx.graphics.Camera;

import java.util.ArrayList;

import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.Location;
import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.Square;
import fr.aboucorp.teamchess.entities.model.Turn;
import fr.aboucorp.teamchess.entities.model.boards.Board;
import fr.aboucorp.teamchess.entities.model.enums.GameState;
import fr.aboucorp.teamchess.entities.model.events.GameEventManager;
import fr.aboucorp.teamchess.entities.model.events.GameEventSubscriber;
import fr.aboucorp.teamchess.entities.model.events.models.GameEvent;
import fr.aboucorp.teamchess.entities.model.events.models.MoveEvent;
import fr.aboucorp.teamchess.entities.model.events.models.PartyEvent;
import fr.aboucorp.teamchess.entities.model.events.models.PieceEvent;
import fr.aboucorp.teamchess.entities.model.events.models.TurnEndEvent;
import fr.aboucorp.teamchess.entities.model.events.models.TurnEvent;
import fr.aboucorp.teamchess.entities.model.events.models.TurnStartEvent;
import fr.aboucorp.teamchess.entities.model.exceptions.FenStringBadFormatException;
import fr.aboucorp.teamchess.entities.model.rules.AbstracRuleSet;
import fr.aboucorp.teamchess.entities.model.utils.SquareList;
import fr.aboucorp.teamchess.libgdx.Board3dManager;
import fr.aboucorp.teamchess.libgdx.models.ChessModel;

public abstract class BoardManager implements GameEventSubscriber {
    protected final Board board;
    protected final Board3dManager board3dManager;
    protected Piece selectedPiece;
    protected GameEventManager eventManager;
    protected Turn previousTurn;
    protected Turn actualTurn;
    protected final AbstracRuleSet ruleSet;
    protected SquareList possiblesMoves;
    private GameState gameState;


    public BoardManager(Board board, Board3dManager board3dManager, AbstracRuleSet ruleSet) {
        this.board = board;
        this.board3dManager = board3dManager;
        this.ruleSet = ruleSet;
        this.gameState = GameState.SelectPiece;
        this.eventManager = GameEventManager.getINSTANCE();
        this.eventManager.subscribe(PartyEvent.class, this, 1);
        this.eventManager.subscribe(TurnEvent.class, this, 1);
        this.eventManager.subscribe(PieceEvent.class, this, 1);
    }


    @Override
    public void receiveGameEvent(GameEvent event) {
        if (event instanceof TurnStartEvent) {
            manageTurnStart((TurnStartEvent) event);
        } else if (event instanceof TurnEndEvent) {
            manageTurnEnd();
        }
    }

    protected abstract void manageTurnStart(TurnStartEvent event);

    protected abstract void manageTurnEnd();

    void clearBoard() {
        this.selectedPiece = null;
        this.previousTurn = null;
        this.actualTurn = null;
        this.board.clearBoard();
        this.board3dManager.clearBoard();
    }

    public abstract void createBoard();

    public abstract ChessColor loadBoard(String fenString) throws FenStringBadFormatException;

    public void selectPiece(Piece touched){
        this.gameState = GameState.SelectCase;
    }

    public void unHighlight(){
        this.gameState = GameState.SelectPiece;
    }

    public ArrayList<ChessModel> getPiecesModelsFromActualTurn(){
        if( this.actualTurn.getTurnColor() == ChessColor.BLACK){
            return this.getBlackPieceModels();
        }else {
            return this.getWhitePieceModels();
        }
    }

    public abstract ArrayList<ChessModel> getBlackPieceModels();

    public abstract ArrayList<ChessModel> getWhitePieceModels();


    public abstract Square getSquareFromLocation(Location location);

    public abstract ChessColor getWinner();

    public abstract ArrayList<ChessModel> getPossibleSquareModels();

    public Camera getCamera() {
        return this.board3dManager.getCamera();
    }

    public GameState getGameState() {
        return gameState;
    }

    public Piece getPieceFromLocation(Location location) {
        ArrayList<Piece> pieces;
        if (this.actualTurn.getTurnColor() == ChessColor.WHITE) {
            pieces = this.board.getWhitePieces();
        } else {
            pieces = this.board.getBlackPieces();
        }
        return pieces.stream().filter(p -> p.getLocation().equals(location)).findFirst().get();
    }

    public abstract Piece moveToSquare(Square to);

    public void selectSquare(Square to){
        Square from = this.selectedPiece.getSquare();
        Piece deadPiece = moveToSquare(to);
        String message = String.format("Move %s to %s",this.selectedPiece,to);
        this.eventManager.sendMessage(new MoveEvent(message,from,to, this.selectedPiece,deadPiece));
    }

    public void toogleTacticalView(){
        this.board3dManager.toogleTacticalView(this.actualTurn.getTurnColor());
    }

    public boolean IsTacticalViewOn(){
        return this.board3dManager.tacticalViewEnabled;
    }
}
