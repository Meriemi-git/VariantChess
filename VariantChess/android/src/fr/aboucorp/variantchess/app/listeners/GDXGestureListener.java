package fr.aboucorp.variantchess.app.listeners;

import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

import fr.aboucorp.variantchess.app.managers.boards.BoardManager;
import fr.aboucorp.variantchess.entities.Piece;
import fr.aboucorp.variantchess.entities.Square;
import fr.aboucorp.variantchess.libgdx.models.ChessModel;


public class GDXGestureListener implements GestureDetector.GestureListener {

    private BoardManager boardManager;
    private TouchedModelFinder touchedModelFinder;
    public GDXGestureListener(BoardManager boardManager) {
        this.boardManager = boardManager;
        this.touchedModelFinder = new TouchedModelFinder(boardManager);
    }

    @Override
    public boolean touchDown(float screenX, float screenY, int pointer, int button) {
        switch (this.boardManager.getGameState()) {
            case SelectPiece:
                ChessModel touchedModel = this.touchedModelFinder.getTouchedModel(screenX, screenY, this.boardManager.getPiecesModelsFromActualTurn());
                if (touchedModel != null) {
                    Piece touchedPiece = this.boardManager.getPieceFromLocation(touchedModel.getLocation());
                    if (touchedPiece != null) {
                        this.boardManager.selectPiece(touchedPiece);
                    } else {
                        this.boardManager.unHighlight();
                    }
                }
                break;
            case SelectCase:
                ChessModel otherModel = this.touchedModelFinder.getTouchedModel(screenX, screenY, this.boardManager.getPiecesModelsFromActualTurn());
                if (otherModel != null) {
                    Piece otherTouchedPiece = this.boardManager.getPieceFromLocation(otherModel.getLocation());
                    if (otherTouchedPiece != null) {
                        this.boardManager.unHighlight();
                        this.boardManager.selectPiece(otherTouchedPiece);
                    }
                } else {
                    ChessModel squareModel = this.touchedModelFinder.getTouchedModel(screenX, screenY, this.boardManager.getPossibleSquareModels());
                    if(squareModel != null) {
                        Square square = this.boardManager.getSquareFromLocation(squareModel.getLocation());
                        if (square != null) {
                            this.boardManager.selectSquare(square);
                        }
                    }else {
                        this.boardManager.unHighlight();
                    }
                }
                break;
                default:
                    break;
        }
        return this.boardManager.IsTacticalViewOn();
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }
}
