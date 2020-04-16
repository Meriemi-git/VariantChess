package fr.aboucorp.teamchess.app.listeners;

import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

import fr.aboucorp.entities.model.ChessCell;
import fr.aboucorp.entities.model.ChessPiece;
import fr.aboucorp.teamchess.app.managers.BoardManager;
import fr.aboucorp.teamchess.libgdx.models.ChessModel;


public class GDXGestureListener implements GestureDetector.GestureListener {

    private BoardManager boardManager;
    private TouchedModelFinder touchedModelFinder;
    public GDXGestureListener(BoardManager boardManager) {
        this.boardManager = boardManager;
        this.touchedModelFinder = new TouchedModelFinder(boardManager);
    }

    @Override
    public boolean touchDown(float screenX, float screenY, int pointer, int button) {
        switch(boardManager.getGameState()){
            case SelectPiece:
                ChessModel touchedModel = touchedModelFinder.getTouchedModel(screenX, screenY,this.boardManager.getPiecesModelsFromActualTurn());
                if(touchedModel != null) {
                    ChessPiece touchedPiece = this.boardManager.getPieceFromLocation(touchedModel.getLocation());
                    if (touchedPiece != null) {
                        this.boardManager.selectPiece(touchedPiece);
                    } else {
                        this.boardManager.resetSelection();
                    }
                }
                break;
            case SelectCase:
                ChessModel otherModel = touchedModelFinder.getTouchedModel(screenX, screenY,this.boardManager.getPiecesModelsFromActualTurn());
                if(otherModel != null) {
                    ChessPiece otherTouchedPiece = this.boardManager.getPieceFromLocation(otherModel.getLocation());
                    if (otherTouchedPiece != null) {
                        this.boardManager.resetSelection();
                        this.boardManager.selectPiece(otherTouchedPiece);
                    } else {
                        ChessModel cellModel = touchedModelFinder.getTouchedModel(screenX, screenY, this.boardManager.getChessCellModels());
                        ChessCell chessCell = this.boardManager.getCellFromLocation(cellModel.getLocation());
                        if (chessCell != null) {
                            this.boardManager.selectCell(chessCell);
                        } else {
                            this.boardManager.resetSelection();
                        }
                    }
                }
                break;
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
