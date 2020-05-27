package fr.aboucorp.variantchess.app.listeners;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

import fr.aboucorp.variantchess.app.managers.boards.BoardManager;
import fr.aboucorp.variantchess.entities.Piece;
import fr.aboucorp.variantchess.entities.Square;
import fr.aboucorp.variantchess.entities.enums.GameState;
import fr.aboucorp.variantchess.libgdx.models.GraphicsGameElement;


public class GDXGestureListener implements GestureDetector.GestureListener {

    private BoardManager boardManager;
    private TouchedModelFinder touchedModelFinder;
    private float currentZoom;

    public GDXGestureListener(BoardManager boardManager) {
        this.boardManager = boardManager;
        this.touchedModelFinder = new TouchedModelFinder(boardManager);
    }

    @Override
    public boolean touchDown(float screenX, float screenY, int pointer, int button) {
        if (this.boardManager.getGameState() == GameState.SelectPiece) {
            GraphicsGameElement piece;
            if (this.boardManager.IsTacticalViewOn()) {
                piece = this.touchedModelFinder.getTouched2DModel(screenX, screenY, this.boardManager.get3DModelsForTurn());
            } else {
                piece = this.touchedModelFinder.getTouched3DModel(screenX, screenY, this.boardManager.get3DModelsForTurn());
            }
            if (piece != null) {
                Piece touchedPiece = this.boardManager.getPieceFromLocation(piece.getLocation());
                if (touchedPiece != null) {
                    this.boardManager.selectPiece(touchedPiece);
                } else {
                    this.boardManager.unHighlight();
                }
            }
        } else if (this.boardManager.getGameState() == GameState.SelectCase) {
            GraphicsGameElement otherPiece;
            if (this.boardManager.IsTacticalViewOn()) {
                otherPiece = this.touchedModelFinder.getTouched2DModel(screenX, screenY, this.boardManager.get3DModelsForTurn());
            } else {
                otherPiece = this.touchedModelFinder.getTouched3DModel(screenX, screenY, this.boardManager.get3DModelsForTurn());
            }
            if (otherPiece != null) {
                Piece otherTouchedPiece = this.boardManager.getPieceFromLocation(otherPiece.getLocation());
                if (otherTouchedPiece != null) {
                    this.boardManager.unHighlight();
                    this.boardManager.selectPiece(otherTouchedPiece);
                }
            } else {
                GraphicsGameElement square = this.touchedModelFinder.getTouched3DModel(screenX, screenY, this.boardManager.getPossibleSquareModels());
                if (square != null) {
                    Square touchedSquare = this.boardManager.getSquareFromLocation(square.getLocation());
                    if (touchedSquare != null) {
                        this.boardManager.selectSquare(touchedSquare);
                    }
                } else {
                    this.boardManager.unHighlight();
                }
            }
        }
        return this.boardManager.IsTacticalViewOn();
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        if (this.boardManager.IsTacticalViewOn()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean longPress(float x, float y) {
        if (this.boardManager.IsTacticalViewOn()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        if (this.boardManager.IsTacticalViewOn()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        if (this.boardManager.IsTacticalViewOn()) {
 /*           this.boardManager.getCamera().translate(-deltaX * 0.5f * this.currentZoom, deltaY * 0.5f * this.currentZoom);
            this.boardManager.getCamera().update();*/
            return false;
        } else {
            return false;
        }
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        if (this.boardManager.IsTacticalViewOn()) {
            this.currentZoom = this.boardManager.getCamera().zoom;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        if (this.boardManager.IsTacticalViewOn()) {
            OrthographicCamera camera = this.boardManager.getCamera();
            camera.zoom = (initialDistance / distance) * this.currentZoom;
            camera.update();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        if (this.boardManager.IsTacticalViewOn()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void pinchStop() {

    }
}
