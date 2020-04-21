package fr.aboucorp.teamchess.app.listeners;

import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

import fr.aboucorp.teamchess.app.managers.PartyManager;
import fr.aboucorp.teamchess.entities.model.ChessCell;
import fr.aboucorp.teamchess.entities.model.ChessPiece;
import fr.aboucorp.teamchess.libgdx.models.ChessModel;


public class GDXGestureListener implements GestureDetector.GestureListener {

    private PartyManager partyManager;
    private TouchedModelFinder touchedModelFinder;
    public GDXGestureListener(PartyManager partyManager) {
        this.partyManager = partyManager;
        this.touchedModelFinder = new TouchedModelFinder(partyManager);
    }

    @Override
    public boolean touchDown(float screenX, float screenY, int pointer, int button) {
        switch (partyManager.getGameState()) {
            case SelectPiece:
                ChessModel touchedModel = touchedModelFinder.getTouchedModel(screenX, screenY, this.partyManager.getPiecesModelsFromActualTurn());
                if (touchedModel != null) {
                    ChessPiece touchedPiece = this.partyManager.getPieceFromLocation(touchedModel.getLocation());
                    if (touchedPiece != null) {
                        this.partyManager.selectPiece(touchedPiece);
                    } else {
                        this.partyManager.resetSelection();
                    }
                }
                break;
            case SelectCase:
                ChessModel otherModel = touchedModelFinder.getTouchedModel(screenX, screenY, this.partyManager.getPiecesModelsFromActualTurn());
                if (otherModel != null) {
                    ChessPiece otherTouchedPiece = this.partyManager.getPieceFromLocation(otherModel.getLocation());
                    if (otherTouchedPiece != null) {
                        this.partyManager.resetSelection();
                        this.partyManager.selectPiece(otherTouchedPiece);
                    }
                } else {
                    ChessModel cellModel = touchedModelFinder.getTouchedModel(screenX, screenY, this.partyManager.getPossibleCellModels());
                    if(cellModel != null) {
                        ChessCell chessCell = this.partyManager.getCellFromLocation(cellModel.getLocation());
                        if (chessCell != null) {
                            this.partyManager.selectCell(chessCell);
                        }
                    }else {
                        this.partyManager.resetSelection();
                    }
                }
                break;
                default:
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
