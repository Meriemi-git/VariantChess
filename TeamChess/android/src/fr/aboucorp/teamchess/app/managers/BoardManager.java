package fr.aboucorp.teamchess.app.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;

import java.util.ArrayList;
import java.util.List;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessCell;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.ChessPiece;
import fr.aboucorp.teamchess.entities.model.Location;
import fr.aboucorp.teamchess.entities.model.enums.PieceEventType;
import fr.aboucorp.teamchess.entities.model.events.BoardEvent;
import fr.aboucorp.teamchess.entities.model.events.ChessEventManager;
import fr.aboucorp.teamchess.entities.model.events.GameEvent;
import fr.aboucorp.teamchess.entities.model.events.GameEventSubscriber;
import fr.aboucorp.teamchess.entities.model.events.PartyEvent;
import fr.aboucorp.teamchess.entities.model.events.PieceEvent;
import fr.aboucorp.teamchess.libgdx.Board3dManager;
import fr.aboucorp.teamchess.libgdx.models.ChessModel;

public class BoardManager implements GameEventSubscriber {

    private final Board board;
    private final Board3dManager board3dManager;
    private ChessPiece selectedPiece;
    private ChessEventManager eventManager;


    public BoardManager(Board3dManager board3dManager) {
        this.board = new Board();
        this.board3dManager = board3dManager;
        this.eventManager = ChessEventManager.getINSTANCE();
        this.eventManager.subscribe(PartyEvent.class,this);
    }

    public void createBoard() {
        this.board.initBoard();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        BoardManager.this.board3dManager.createCells(BoardManager.this.board.getChessCells());
                        BoardManager.this.board3dManager.createPieces(BoardManager.this.board.getWhitePieces());
                        BoardManager.this.board3dManager.createPieces(BoardManager.this.board.getBlackPieces());
                    }
                });
            }
        }).start();
    }

    public ChessPiece getPieceFromLocation(Location location,ChessColor color) {
        ArrayList<ChessPiece> arrayList = null;
        if(color == ChessColor.WHITE){
            arrayList = this.board.getWhitePieces();
        }else{
            arrayList = this.board.getBlackPieces();
        }
        for (ChessPiece piece : arrayList){
            if(piece.getLocation().equals(location)){
                return piece;
            }
        }
        return null;
    }

    public ChessCell getCellFromLocation(Location location) {
        for (ChessCell cell : this.board.getChessCells()){
            if(cell.getLocation().equals(location)){
                return cell;
            }
        }
        return null;
    }

    public ArrayList<ChessModel> getChessCellModels() {
        return this.board3dManager.getChessCellModels();
    }


    public void moveSelectedPieceToLocation(Location location) {
        this.selectedPiece.move(this.getCellFromLocation(location));
        this.board3dManager.moveSelectedModelToLocation(location);
        // Send Move event
        this.eventManager.sendMessage(new PieceEvent("Move event sent by BoardManager",PieceEventType.MOVE,this.selectedPiece));
    }

    public ArrayList<ChessModel> getBlackPieceModels() {
        return  this.board3dManager.getBlackPieceModels();
    }

    public ArrayList<ChessModel> getWhitePieceModels() {
        return  this.board3dManager.getWhitePieceModels();
    }

    public void selectPiece(ChessPiece touched) {
        this.selectedPiece = touched;
        this.board3dManager.selectPiece(touched);
        this.hightLightPossibleMoves(touched);
    }

    private void hightLightPossibleMoves(ChessPiece selected) {
        List<ChessCell> validCells = selected.getNextMoves(selected,this.board);
        if(validCells != null) {
            for (ChessCell cell : validCells) {
                this.board3dManager.highlightedCellFromLocation(cell.getLocation());
            }
        }else{
            this.eventManager.sendMessage(new BoardEvent("Piece cannot Move"));
        }
    }

    public void resetSelection() {
        this.board3dManager.resetSelection();
    }

    public Camera getCamera(){
        return this.board3dManager.getCamera();
    }

    @Override
    public void receiveGameEvent(GameEvent event) {

    }



    public void selectCell(ChessCell chessCell) {
        this.moveSelectedPieceToLocation(chessCell.getLocation());
    }
}
