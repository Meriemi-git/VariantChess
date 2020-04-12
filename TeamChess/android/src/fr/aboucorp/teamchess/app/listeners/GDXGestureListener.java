package fr.aboucorp.teamchess.app.listeners;

import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

import fr.aboucorp.teamchess.app.GameManager;
import fr.aboucorp.teamchess.app.models.enums.GameState;
import fr.aboucorp.teamchess.libgdx.models.ChessPiece;


public class GDXGestureListener implements GestureDetector.GestureListener {

    private GameManager gameManager;
    private TouchedModelFinder touchedModelFinder;
    public GDXGestureListener(GameManager gameManager) {
        this.gameManager = gameManager;
        this.touchedModelFinder = new TouchedModelFinder(gameManager);
    }

    @Override
    public boolean touchDown(float screenX, float screenY, int pointer, int button) {
        if(gameManager.getGameState() == GameState.SelectPiece) {
            ChessPiece piece = touchedModelFinder.getTouchedPiece(screenX, screenY,this.gameManager.getPiecesFromActualTurn());
            if (piece != null) {
                this.gameManager.selectPiece(piece);
            }
        }
        return false;
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
