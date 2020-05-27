package fr.aboucorp.variantchess.app.managers.boards;

import com.badlogic.gdx.graphics.PerspectiveCamera;

import java.util.ArrayList;
import java.util.Optional;

import fr.aboucorp.variantchess.app.listeners.GDXGestureListener;
import fr.aboucorp.variantchess.app.listeners.GDXInputAdapter;
import fr.aboucorp.variantchess.app.utils.GdxPostRunner;
import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.Location;
import fr.aboucorp.variantchess.entities.Match;
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

    private GameState gameState;

    public boolean IsTacticalViewOn() {
        return this.board3dManager.isTacticalViewEnabled();
    }

    public PerspectiveCamera getCamera() {
        return this.board3dManager.getCamera();
    }

    public abstract String getFenFromBoard();

    public GameState getGameState() {
        return this.gameState;
    }

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

    @Override
    public void receiveGameEvent(GameEvent event) {
        if (event instanceof TurnStartEvent) {
            this.manageTurnStart((TurnStartEvent) event);
        } else if (event instanceof TurnEndEvent) {
            this.manageTurnEnd();
        }
    }

    public void selectPiece(Piece touched) {
        this.selectedPiece = touched;
        this.gameState = GameState.SelectCase;
    }

    public void selectSquare(Square to) {
        Square from = this.selectedPiece.getSquare();
        Piece deadPiece = this.moveToSquare(to);
        String message = String.format("Move %s from %s to %s", this.selectedPiece, from, to);
        this.gameEventManager.sendMessage(new MoveEvent(
                message
                , from.getLocation()
                , to.getLocation()
                , this.selectedPiece.getPieceId()
                , deadPiece != null ? deadPiece.getPieceId() : null));
    }

    public void setBoardLoadingListener(BoardLoadingListener boardLoadingListener) {
        this.boardLoadingListener = boardLoadingListener;
    }

    @Override
    public void startParty(Match match) {
        this.gameEventManager.subscribe(PartyEvent.class, this, 1);
        this.gameEventManager.subscribe(TurnEvent.class, this, 1);
        this.gameEventManager.subscribe(PieceEvent.class, this, 1);
    }

    @Override
    public void stopParty() {
        this.gameState = GameState.SelectPiece;
        this.selectedPiece = null;
        this.previousTurn = null;
        this.actualTurn = null;
        this.board.clearBoard();
        this.ruleSet.moveNumber = 0;
    }

    public void toogleTacticalView() {
        GdxPostRunner runner = new GdxPostRunner() {
            @Override
            public void execute() {
                BoardManager.this.board3dManager.toogleTacticalView();
            }
        };
        runner.startAsync();
    }

    public void unHighlight() {
        this.gameState = GameState.SelectPiece;
    }

    BoardManager(Board board, Board3dManager board3dManager, AbstractRuleSet ruleSet, GameEventManager gameEventManager) {
        this.board = board;
        this.board3dManager = board3dManager;
        this.ruleSet = ruleSet;
        GDXInputAdapter inputAdapter = new GDXInputAdapter(board3dManager);
        board3dManager.setAndroidInputAdapter(inputAdapter);
        GDXGestureListener gestureListener = new GDXGestureListener(this);
        board3dManager.setAndroidListener(gestureListener);
        this.gameState = GameState.SelectPiece;
        this.gameEventManager = gameEventManager;
    }

    public interface BoardLoadingListener {
        void OnBoardLoaded();
    }
}
