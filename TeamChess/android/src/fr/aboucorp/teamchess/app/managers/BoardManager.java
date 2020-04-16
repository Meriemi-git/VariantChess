package fr.aboucorp.teamchess.app.managers;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;

import fr.aboucorp.entities.model.ChessCell;
import fr.aboucorp.entities.model.ChessColor;
import fr.aboucorp.entities.model.ChessPiece;
import fr.aboucorp.entities.model.Location;
import fr.aboucorp.entities.model.enums.GameState;
import fr.aboucorp.entities.model.pieces.Bishop;
import fr.aboucorp.entities.model.pieces.King;
import fr.aboucorp.entities.model.pieces.Knight;
import fr.aboucorp.entities.model.pieces.Pawn;
import fr.aboucorp.entities.model.pieces.Queen;
import fr.aboucorp.entities.model.pieces.Rook;
import fr.aboucorp.teamchess.app.TurnManager;
import fr.aboucorp.teamchess.libgdx.Board3dManager;
import fr.aboucorp.teamchess.libgdx.models.ChessModel;


public class BoardManager  {
    private Board3dManager board3dManager;
    private GameState gameState;
    private TurnManager turnManager;
    private ChessPiece selectedPiece;

    private ArrayList<ChessCell> chessCells;
    private ArrayList<ChessPiece> blackPieces;
    private ArrayList<ChessPiece> whitePieces;

    public BoardManager(Board3dManager board3dManager) {
        this.board3dManager = board3dManager;
        this.blackPieces = new ArrayList<>();
        this.whitePieces = new ArrayList<>();
        this.chessCells = new ArrayList<>();
        this.gameState = GameState.SelectPiece;
        this.turnManager = new TurnManager();
    }

    public void startGame(){
        this.turnManager.startParty();
        this.createBoard();
    }

    public void selectPiece(ChessPiece touched) {
        this.gameState = GameState.SelectCase;
        this.board3dManager.selectPiece(touched);
    }

    public void resetSelection() {
        this.gameState = GameState.SelectPiece;
        this.board3dManager.resetSelection();
    }

    public ArrayList<ChessModel> getPiecesModelsFromActualTurn(){
        if( this.turnManager.getTurnColor() == ChessColor.BLACK){
            return this.getBlackPieceModels();
        }else {
            return this.getWhitePieceModels();
        }
    }

    public void movePieceIntoCell(ChessCell cell) {
        this.board3dManager.moveSelectedPieceToLocation(cell.getLocation());
    }

    public void selectCell(ChessCell chessCell) {

    }

    public void createBoard() {
       createCells();
       createPieces();
    }

    private void createCells(){
        for (int x = 0; x < 8; x++) {
            for (int z = 0; z < 8; z++) {
                ChessCell cell = null;
                if(x % 2 == 0 && z % 2 != 0 || x % 2 != 0 && z % 2 == 0 ){
                    cell = new ChessCell(new Location(x, 0, z), ChessColor.BLACK);
                }else{
                    cell = new ChessCell(new Location(x, 0, z), ChessColor.WHITE);
                }
                this.chessCells.add(cell);
            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        BoardManager.this.board3dManager.createCells(BoardManager.this.chessCells);
                    }
                });
            }
        }).start();
    }

    private void createPieces(){
    this.createWhitePieces();
    this.createBlackPieces();
    this.board3dManager.createPieces(this.whitePieces);
    this.board3dManager.createPieces(this.blackPieces);
    }

    private void createWhitePieces(){
        Knight whiteRightKnight = new Knight(new Location(6,0,0), ChessColor.WHITE);
        Knight whiteLeftKnight =  new Knight(new Location(1,0,0), ChessColor.WHITE);
        Bishop whiteRightBishop = new Bishop(new Location(5,0,0), ChessColor.WHITE);
        Bishop whiteLeftBishop = new Bishop(new Location(2,0,0), ChessColor.WHITE);
        Queen whiteQueen = new Queen(new Location(4,0,0), ChessColor.WHITE);
        King whiteKing = new King(new Location(3,0,0), ChessColor.WHITE);
        Rook whiteLeftRook = new Rook(new Location(7,0,0), ChessColor.WHITE);
        Rook whiteRightRook =  new Rook(new Location(0,0,0), ChessColor.WHITE);
        for(int  i = 0 ; i < 8 ; i++){
            Pawn whitePawn =  new Pawn(new Location(i,0,1), ChessColor.WHITE);
            this.whitePieces.add(whitePawn);
        }
        this.whitePieces.add(whiteRightKnight);
        this.whitePieces.add(whiteLeftKnight);
        this.whitePieces.add(whiteRightBishop);
        this.whitePieces.add(whiteLeftBishop);
        this.whitePieces.add(whiteQueen);
        this.whitePieces.add(whiteKing);
        this.whitePieces.add(whiteLeftRook);
        this.whitePieces.add(whiteRightRook);
    }

    private void createBlackPieces(){
        Knight blackRightKnight = new Knight(new Location(6,0,7), ChessColor.BLACK);
        Knight blackLeftKnight = new Knight(new Location(1,0,7), ChessColor.BLACK);
        Bishop blackLeftBishop = new Bishop(new Location(5,0,7), ChessColor.BLACK);
        Bishop blackRightBishop =  new Bishop(new Location(2,0,7), ChessColor.BLACK);
        Queen blackQueen = new Queen(new Location(4,0,7), ChessColor.BLACK);
        King blackKing = new King(new Location(3,0,7), ChessColor.BLACK);
        Rook blackLeftRook =  new Rook(new Location(7,0,7), ChessColor.BLACK);
        Rook blackRightRook =  new Rook(new Location(0,0,7), ChessColor.BLACK);

        for(int  i = 0 ; i < 8 ; i++){
            Pawn blackPawn = new Pawn(new Location(i,0,6),ChessColor.BLACK);
            this.blackPieces.add(blackPawn);
        }
        this.blackPieces.add(blackRightKnight);
        this.blackPieces.add(blackLeftKnight);
        this.blackPieces.add(blackLeftBishop);
        this.blackPieces.add(blackRightBishop);
        this.blackPieces.add(blackQueen);
        this.blackPieces.add(blackKing);
        this.blackPieces.add(blackLeftRook);
        this.blackPieces.add(blackRightRook);
    }

    public ArrayList<ChessModel> getBlackPieceModels() {
        return this.board3dManager.getBlackPieceModels();
    }

    public ArrayList<ChessModel> getWhitePieceModels() {
        return  this.board3dManager.getWhitePieceModels();
    }

    public ChessPiece getPieceFromLocation(Location location) {

        ArrayList<ChessPiece> arrayList = null;
        if(this.turnManager.getTurnColor() == ChessColor.BLACK){
            arrayList = this.blackPieces;
        }else{
            arrayList = this.whitePieces;
        }
        for (ChessPiece piece : arrayList){
            if(piece.getLocation().equals(location)){
                return piece;
            }
        }
      return null;
    }

    public ChessCell getCellFromLocation(Location location) {
        for (ChessCell cell : this.chessCells){
            if(cell.getLocation().equals(location)){
                return cell;
            }
        }
        return null;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public Board3dManager getBoard3dManager() {
        return board3dManager;
    }

    public ArrayList<ChessModel> getChessCellModels() {
        return this.board3dManager.getChessCellModels();
    }
}
