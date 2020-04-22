package fr.aboucorp.teamchess.app.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;

import java.util.ArrayList;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessCell;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.ChessPiece;
import fr.aboucorp.teamchess.entities.model.ChessTurn;
import fr.aboucorp.teamchess.entities.model.Location;
import fr.aboucorp.teamchess.entities.model.enums.PieceEventType;
import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.entities.model.events.BoardEvent;
import fr.aboucorp.teamchess.entities.model.events.CheckEvent;
import fr.aboucorp.teamchess.entities.model.events.GameEvent;
import fr.aboucorp.teamchess.entities.model.events.GameEventManager;
import fr.aboucorp.teamchess.entities.model.events.GameEventSubscriber;
import fr.aboucorp.teamchess.entities.model.events.MoveEvent;
import fr.aboucorp.teamchess.entities.model.events.PartyEvent;
import fr.aboucorp.teamchess.entities.model.events.PieceEvent;
import fr.aboucorp.teamchess.entities.model.events.TurnEndEvent;
import fr.aboucorp.teamchess.entities.model.events.TurnEvent;
import fr.aboucorp.teamchess.entities.model.events.TurnStartEvent;
import fr.aboucorp.teamchess.entities.model.utils.ChessCellList;
import fr.aboucorp.teamchess.libgdx.Board3dManager;
import fr.aboucorp.teamchess.libgdx.models.ChessModel;

public class BoardManager implements GameEventSubscriber {

    private final Board board;
    private final Board3dManager board3dManager;
    private ChessPiece selectedPiece;
    private GameEventManager eventManager;
    private ChessTurn actualTurn;
    private boolean waitingForBigCastle;
    private boolean waitingForLittleCastle;
    private ChessCellList possiblesMoves;

    public BoardManager(Board3dManager board3dManager) {
        this.board = new Board();
        this.board3dManager = board3dManager;
        this.eventManager = GameEventManager.getINSTANCE();
        this.eventManager.subscribe(PartyEvent.class,this);
        this.eventManager.subscribe(TurnStartEvent.class,this);
        this.eventManager.subscribe(TurnEndEvent.class,this);
        this.eventManager.subscribe(PieceEvent.class,this);
    }

    @Override
    public void receiveGameEvent(GameEvent event) {
        if (event instanceof TurnStartEvent) {
            this.actualTurn = ((TurnEvent) event).turn;
        }else if (event instanceof TurnEndEvent){
            this.selectedPiece = null;
            this.possiblesMoves = null;
            this.waitingForLittleCastle = false;
            this.waitingForBigCastle = false;
        }
        else if (event instanceof PieceEvent){
            dispatchPieceEvent((PieceEvent) event);
        }
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

    public void moveSelectedPieceToCell(ChessCell cell) {
        String eventMessage =  String.format("Move %s: from %s; to %s",
                selectedPiece.getPieceId().name(),
                selectedPiece.getActualCell(),
                cell);
        if(cell.getPiece() != null){
            eatPiece(cell.getPiece());
        }
        MoveEvent moveEvent = new MoveEvent(eventMessage,selectedPiece.getActualCell(),cell,selectedPiece, cell.getPiece());
        this.selectedPiece.move(cell);
        this.board3dManager.moveSelectedPieceIntoCell(cell);
        if(this.waitingForBigCastle || this.waitingForLittleCastle){
            checkIfCastling(cell);
        }
        resetHighlited();
        this.eventManager.sendMessage(moveEvent);
        kingIsInCheck();
    }

    private void kingIsInCheck() {
        if(this.selectedPiece.getMoveSet().isInCheck(this.selectedPiece,board,actualTurn.getTurnColor())){
            this.eventManager.sendMessage(new CheckEvent("King is in check",actualTurn.getTurnColor()));
        }
    }

    public void eatPiece(ChessPiece piece){
        if(piece.getChessColor() == ChessColor.WHITE){
            this.board.getWhitePieces().removeByLocation(piece.getLocation());
        }else{
            this.board.getBlackPieces().removeByLocation(piece.getLocation());
        }
        this.board3dManager.moveToEven(piece);
        piece.die();
    }
    
    private void resetHighlited() {
        this.board3dManager.unHighlightCells(this.possiblesMoves);
        this.board3dManager.resetSelection();
        this.possiblesMoves = null;
    }

    private void checkIfCastling(ChessCell cell) {
        ChessColor turnColor = this.actualTurn.getTurnColor();
        ChessPiece rookToMove = null;
        ChessCell destination = null;
        if (this.waitingForBigCastle) {
            if (turnColor == ChessColor.WHITE && cell.getCellLabel().equals("C1")) {
                rookToMove = this.board.getWhitePieces().getPieceById(PieceId.WLR);
                destination = this.board.getChessCells().getChessCellByLabel("D1");
            } else if (turnColor == ChessColor.BLACK && cell.getCellLabel().equals("C8")) {
                this.board.getWhitePieces().getPieceById(PieceId.BLR).getLocation();
                destination = this.board.getChessCells().getChessCellByLabel("D8");
            }
            this.waitingForBigCastle = false;
        }
        if (this.waitingForLittleCastle){
            if (turnColor == ChessColor.WHITE && cell.getCellLabel().equals("G1")) {
                rookToMove = this.board.getWhitePieces().getPieceById(PieceId.WRR);
                destination = this.board.getChessCells().getChessCellByLabel("F1");
            }else if (turnColor == ChessColor.BLACK && cell.getCellLabel().equals("G8")) {
                rookToMove = this.board.getWhitePieces().getPieceById(PieceId.BRR);
                destination = this.board.getChessCells().getChessCellByLabel("F8");
            }
            this.waitingForLittleCastle = false;
        }
        if(rookToMove != null && destination != null){
            this.board3dManager.moveToCell(rookToMove,destination);
            rookToMove.move(destination);
        }
    }

    public void selectPiece(ChessPiece touched) {
        this.selectedPiece = touched;
        this.board3dManager.selectPiece(touched);
        this.possiblesMoves = touched.getMoveSet().getPossibleMoves(touched,this.board,this.actualTurn.getTurnColor());
        this.hightLightPossibleMoves(this.possiblesMoves);
    }

    private void hightLightPossibleMoves(ChessCellList possibleMoves) {
        if(possibleMoves != null) {
            for (ChessCell cell : possibleMoves) {
                if(cell.getPiece() == null){
                    this.board3dManager.highlightEmptyCellFromLocation(cell);
                }else{
                    this.board3dManager.highlightOccupiedCellFromLocation(cell);
                }
            }
        }else{
            this.eventManager.sendMessage(new BoardEvent("Piece cannot Move"));
        }
    }

    public void unHighlight() {
        this.board3dManager.resetSelection();
        if(possiblesMoves != null) {
            this.board3dManager.unHighlightCells(this.possiblesMoves);
        }
    }

    private void dispatchPieceEvent(PieceEvent event) {
        if(event.type == PieceEventType.LITTLE_CASTLING || event.type == PieceEventType.BIG_CASTLING){
            manageCastling(event.type);
        }
    }

    private void manageCastling(PieceEventType type){
        if(type == PieceEventType.BIG_CASTLING){
            this.waitingForBigCastle = true;
        }else if(type == PieceEventType.LITTLE_CASTLING){
            this.waitingForLittleCastle = true;
        }
    }

    public ArrayList<ChessModel> getPossibleCellModels() {
        return this.board3dManager.getCellModelsFromPossibleMoves(this.possiblesMoves);
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

    public Camera getCamera(){
        return this.board3dManager.getCamera();
    }

    public ArrayList<ChessModel> getBlackPieceModels() {
        return  this.board3dManager.getBlackPieceModels();
    }

    public ArrayList<ChessModel> getWhitePieceModels() {
        return  this.board3dManager.getWhitePieceModels();
    }

    public ChessPiece getSelectedPiece() {
        return selectedPiece;
    }
}
