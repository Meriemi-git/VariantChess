package fr.aboucorp.variantchess.app.managers.boards;

import android.util.Log;

import com.badlogic.gdx.graphics.PerspectiveCamera;

import java.util.ArrayList;
import java.util.Optional;

import fr.aboucorp.variantchess.app.listeners.GDXGestureListener;
import fr.aboucorp.variantchess.app.listeners.GDXInputAdapter;
import fr.aboucorp.variantchess.app.utils.state.BoardStateBuilder;
import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.ChessMatch;
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
import fr.aboucorp.variantchess.entities.events.models.TurnStartEvent;
import fr.aboucorp.variantchess.entities.exceptions.FenStringBadFormatException;
import fr.aboucorp.variantchess.entities.rules.AbstractRuleSet;
import fr.aboucorp.variantchess.entities.utils.SquareList;
import fr.aboucorp.variantchess.libgdx.Board3dManager;
import fr.aboucorp.variantchess.libgdx.utils.GraphicGameArray;

public abstract class BoardManager implements GameEventSubscriber, PartyLifeCycle {
    protected final Board board;
    protected final Board3dManager board3dManager;
    protected final AbstractRuleSet ruleSet;
    protected final GameEventManager gameEventManager;
    protected Piece selectedPiece;
    protected Turn previousTurn;
    protected Turn actualTurn;
    protected SquareList possiblesMoves;
    protected BoardLoadingListener boardLoadingListener;
    protected GameState gameState;
    protected BoardStateBuilder boardStateBuilder;

    BoardManager(Board board, Board3dManager board3dManager, AbstractRuleSet ruleSet, GameEventManager gameEventManager, BoardStateBuilder boardStateBuilder) {
        this.board = board;
        this.board3dManager = board3dManager;
        this.ruleSet = ruleSet;
        GDXInputAdapter inputAdapter = new GDXInputAdapter(board3dManager);
        board3dManager.setAndroidInputAdapter(inputAdapter);
        GDXGestureListener gestureListener = new GDXGestureListener(this);
        board3dManager.setAndroidListener(gestureListener);
        this.gameState = GameState.PIECE_SELECTION;
        this.gameEventManager = gameEventManager;
        this.boardStateBuilder = boardStateBuilder;
    }

    @Override
    public abstract void receiveEvent(GameEvent event);

    @Override
    public abstract void startParty(ChessMatch chessMatch);

    @Override
    public abstract void stopParty();


    public GraphicGameArray getModelsForTurn() {
        if (this.actualTurn.getTurnColor() == ChessColor.BLACK) {
            return this.board3dManager.getBlackPieceModels();
        } else {
            return this.board3dManager.getWhitePieceModels();
        }
    }

    public Piece getPieceFromLocation(Location location) {
        ArrayList<Piece> pieces;
        if (this.actualTurn.getTurnColor() == ChessColor.WHITE) {
            pieces = this.board.getWhitePieces();
        } else {
            pieces = this.board.getBlackPieces();
        }
        Optional<Piece> piece = pieces.stream().filter(p -> p.getLocation().equals(location)).findFirst();
        return piece.isPresent() ? piece.get() : null;
    }

    public abstract GraphicGameArray getPossibleSquareModels();

    public abstract Square getSquareFromLocation(Location location);

    public abstract ChessColor getWinner();

    public abstract ChessColor loadBoard(String fenString) throws FenStringBadFormatException;

    protected abstract void manageTurnEnd();

    protected abstract void manageTurnStart(TurnStartEvent event);

    protected abstract Piece moveToSquare(Square to);

    public abstract void selectPiece(Piece touched);

    public abstract void selectSquare(Square to);

    public void setBoardLoadingListener(BoardLoadingListener boardLoadingListener) {
        this.boardLoadingListener = boardLoadingListener;
    }

    public abstract void toogleTacticalView();

    public void unHighlight() {
        this.gameState = this.gameState == GameState.WAIT_FOR_NEXT_TURN ? GameState.WAIT_FOR_NEXT_TURN : GameState.PIECE_SELECTION;
    }

    public abstract void playTheOppositeMove(String fenState);


    public void waitForNextTurn() {
        Log.i("fr.aboucorp.variantchess", "Wainting for next turn");
        this.gameState = GameState.WAIT_FOR_NEXT_TURN;
    }

    public void stopWaitingForNextTurn() {
        Log.i("fr.aboucorp.variantchess", "Wainting for next turn");
        this.gameState = GameState.PIECE_SELECTION;
    }

    public boolean IsTacticalViewOn() {
        return this.board3dManager.isTacticalViewEnabled();
    }

    public PerspectiveCamera getCamera() {
        return this.board3dManager.getCamera();
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public String getBoardState() {
        return this.boardStateBuilder.getStateFromBoard(this.actualTurn);
    }

    public String getFenFromBoard() {
        return this.boardStateBuilder.getFenFromBoard(this.actualTurn);
    }
}
