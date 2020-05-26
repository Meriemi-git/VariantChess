package fr.aboucorp.variantchess.app.listeners;

import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

import fr.aboucorp.variantchess.app.managers.boards.BoardManager;
import fr.aboucorp.variantchess.entities.Piece;
import fr.aboucorp.variantchess.entities.Square;
import fr.aboucorp.variantchess.entities.enums.GameState;
import fr.aboucorp.variantchess.libgdx.models.ChessModel;
import fr.aboucorp.variantchess.libgdx.models.GraphicsGameElement;


public class GDXGestureListener implements GestureDetector.GestureListener {

    private BoardManager boardManager;
    private TouchedModelFinder touchedModelFinder;
    public GDXGestureListener(BoardManager boardManager) {
        this.boardManager = boardManager;
        this.touchedModelFinder = new TouchedModelFinder(boardManager);
    }

    @Override
    public boolean touchDown(float screenX, float screenY, int pointer, int button) {
        if(this.boardManager.IsTacticalViewOn()){
            this.reactOn2DModelTouched(this.boardManager.getGameState(),screenX,screenY);
        }else{
            this.reactOn3DModelTouched(this.boardManager.getGameState(),screenX,screenY);
        }
        return this.boardManager.IsTacticalViewOn();
    }

    private void reactOn2DModelTouched(GameState gameState, float screenX, float screenY) {
        if (gameState == GameState.SelectPiece) {
            ChessModel touchedModel = this.touchedModelFinder.getTouched2DModel(screenX, screenY, this.boardManager.get2DModelsForTurn());
        } else if (gameState == GameState.SelectCase) {

        }
    }


    private void reactOn3DModelTouched(GameState gameState, float screenX, float screenY) {
        if(gameState == GameState.SelectPiece){
            GraphicsGameElement piece = this.touchedModelFinder.getTouched3DModel(screenX, screenY, this.boardManager.get3DModelsForTurn());
            if (piece != null) {
                Piece touchedPiece = this.boardManager.getPieceFromLocation(piece.getLocation());
                if (touchedPiece != null) {
                    this.boardManager.selectPiece(touchedPiece);
                } else {
                    this.boardManager.unHighlight();
                }
            }
        }else if(gameState == GameState.SelectCase){
            GraphicsGameElement otherPiece = this.touchedModelFinder.getTouched3DModel(screenX, screenY, this.boardManager.get3DModelsForTurn());
            if (otherPiece != null) {
                Piece otherTouchedPiece = this.boardManager.getPieceFromLocation(otherPiece.getLocation());
                if (otherTouchedPiece != null) {
                    this.boardManager.unHighlight();
                    this.boardManager.selectPiece(otherTouchedPiece);
                }
            } else {
                GraphicsGameElement square = this.touchedModelFinder.getTouched3DModel(screenX, screenY, this.boardManager.getPossibleSquareModels());
                if(square != null) {
                    Square touchedSquare = this.boardManager.getSquareFromLocation(square.getLocation());
                    if (touchedSquare != null) {
                        this.boardManager.selectSquare(touchedSquare);
                    }
                }else {
                    this.boardManager.unHighlight();
                }
            }
        }
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
