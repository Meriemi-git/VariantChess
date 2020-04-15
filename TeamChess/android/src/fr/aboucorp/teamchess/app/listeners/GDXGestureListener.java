package fr.aboucorp.teamchess.app.listeners;

import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

import fr.aboucorp.entities.model.ChessCell;
import fr.aboucorp.entities.model.ChessPiece;
import fr.aboucorp.teamchess.app.managers.PartyManager;


public class GDXGestureListener implements GestureDetector.GestureListener {

    private PartyManager partyManager;
    private TouchedModelFinder touchedModelFinder;
    public GDXGestureListener(PartyManager partyManager) {
        this.partyManager = partyManager;
        this.touchedModelFinder = new TouchedModelFinder(partyManager);
    }

    @Override
    public boolean touchDown(float screenX, float screenY, int pointer, int button) {
        switch(partyManager.getGameState()){
            case SelectPiece:
                ChessPiece piece = (ChessPiece) touchedModelFinder.getTouchedModel(screenX, screenY,this.partyManager.getPiecesFromActualTurn());
                if (piece != null) {
                    this.partyManager.selectPiece(piece);
                }else{
                    this.partyManager.resetSelection();
                }
                break;
            case SelectCase:
                ChessPiece otherPiece = (ChessPiece) touchedModelFinder.getTouchedModel(screenX, screenY,this.partyManager.getPiecesFromActualTurn());
                if (otherPiece != null) {
                    this.partyManager.resetSelection();
                    this.partyManager.selectPiece(otherPiece);
                }else{
                    ChessCell cell = (ChessCell)  touchedModelFinder.getTouchedModel(screenX, screenY,this.partyManager.getGame3dManager().getChessCells().getFlattenCells());
                    if (cell != null) {
                        this.partyManager.selectCell(cell);
                    }else{
                        this.partyManager.resetSelection();
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
