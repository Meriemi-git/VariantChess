package fr.aboucorp.variantchess.app.managers.boards;

import com.badlogic.gdx.graphics.Camera;

import java.util.ArrayList;

import fr.aboucorp.variantchess.app.listeners.GDXGestureListener;
import fr.aboucorp.variantchess.app.listeners.GDXInputAdapter;
import fr.aboucorp.variantchess.app.utils.GdxPostRunner;
import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.Location;
import fr.aboucorp.variantchess.entities.PartyLifeCycle;
import fr.aboucorp.variantchess.entities.Piece;
import fr.aboucorp.variantchess.entities.Square;
import fr.aboucorp.variantchess.entities.Turn;
import fr.aboucorp.variantchess.entities.boards.Board;
import fr.aboucorp.variantchess.entities.enums.GameState;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.events.GameEventSubscriber;
import fr.aboucorp.variantchess.entities.events.models.GameEvent;
import fr.aboucorp.variantchess.entities.events.models.MoveEvent;
import fr.aboucorp.variantchess.entities.events.models.PartyEvent;
import fr.aboucorp.variantchess.entities.events.models.PieceEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnEndEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnStartEvent;
import fr.aboucorp.variantchess.entities.exceptions.FenStringBadFormatException;
import fr.aboucorp.variantchess.entities.rules.AbstractRuleSet;
import fr.aboucorp.variantchess.entities.utils.SquareList;
import fr.aboucorp.variantchess.libgdx.Board3dManager;
import fr.aboucorp.variantchess.libgdx.utils.ChessModelList;

public abstract class BoardManager implements GameEventSubscriber, PartyLifeCycle {
    protected final Board board;
    protected final Board3dManager board3dManager;
    private final GDXInputAdapter inputAdapter;
    private final GDXGestureListener gestureListener;
    protected Piece selectedPiece;
    protected GameEventManager eventManager;
    protected  Turn previousTurn;
    protected Turn actualTurn;
    protected final AbstractRuleSet ruleSet;
    protected SquareList possiblesMoves;
    protected BoardLoadingListener boardLoadingListener;

    private GameState gameState;


    public BoardManager(Board board, Board3dManager board3dManager, AbstractRuleSet ruleSet) {
        this.board = board;
        this.board3dManager = board3dManager;
        this.ruleSet = ruleSet;
        inputAdapter = new GDXInputAdapter(board3dManager);
        board3dManager.setAndroidInputAdapter(inputAdapter);
        gestureListener = new GDXGestureListener(this);
        board3dManager.setAndroidListener(gestureListener);
        this.gameState = GameState.SelectPiece;
        this.eventManager = GameEventManager.getINSTANCE();
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

    @Override
    public void stopParty() {
        this.gameState = GameState.SelectPiece;
        this.selectedPiece = null;
        this.previousTurn = null;
        this.actualTurn = null;
        this.board.clearBoard();
        this.board3dManager.clearBoard();
        this.ruleSet.moveNumber = 0;
    }

    @Override
    public void startParty() {
        this.eventManager.subscribe(PartyEvent.class, this, 1);
        this.eventManager.subscribe(TurnEvent.class, this, 1);
        this.eventManager.subscribe(PieceEvent.class, this, 1);
    }

    public abstract ChessColor loadBoard(String fenString) throws FenStringBadFormatException;

    public void selectPiece(Piece touched){
        this.gameState = GameState.SelectCase;
    }

    public void unHighlight(){
        this.gameState = GameState.SelectPiece;
    }

    public ChessModelList getPiecesModelsFromActualTurn(){
        if( this.actualTurn.getTurnColor() == ChessColor.BLACK){
            return this.getBlackPieceModels();
        }else {
            return this.getWhitePieceModels();
        }
    }

    public abstract ChessModelList getBlackPieceModels();

    public abstract ChessModelList getWhitePieceModels();

    public abstract Square getSquareFromLocation(Location location);

    public abstract ChessColor getWinner();

    public abstract ChessModelList getPossibleSquareModels();

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
        GdxPostRunner runner = new GdxPostRunner() {
            @Override
            public void execute() {
                BoardManager.this.board3dManager.toogleTacticalView();
            }
        };
        runner.startAsync();
    }

    public boolean IsTacticalViewOn(){
        return this.board3dManager.tacticalViewEnabled;
    }

    public void setBoardLoadingListener(BoardLoadingListener boardLoadingListener) {
        this.boardLoadingListener = boardLoadingListener;
    }

    public interface BoardLoadingListener{
        void OnBoardLoaded();
    }


}
