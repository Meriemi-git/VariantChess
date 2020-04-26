package fr.aboucorp.teamchess.app.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;

import java.util.ArrayList;
import java.util.List;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.Location;
import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.Square;
import fr.aboucorp.teamchess.entities.model.Turn;
import fr.aboucorp.teamchess.entities.model.enums.BoardEventType;
import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.entities.model.events.GameEventManager;
import fr.aboucorp.teamchess.entities.model.events.GameEventSubscriber;
import fr.aboucorp.teamchess.entities.model.events.models.BoardEvent;
import fr.aboucorp.teamchess.entities.model.events.models.CheckInEvent;
import fr.aboucorp.teamchess.entities.model.events.models.CheckOutEvent;
import fr.aboucorp.teamchess.entities.model.events.models.GameEvent;
import fr.aboucorp.teamchess.entities.model.events.models.MoveEvent;
import fr.aboucorp.teamchess.entities.model.events.models.PartyEvent;
import fr.aboucorp.teamchess.entities.model.events.models.PieceEvent;
import fr.aboucorp.teamchess.entities.model.events.models.TurnEndEvent;
import fr.aboucorp.teamchess.entities.model.events.models.TurnEvent;
import fr.aboucorp.teamchess.entities.model.events.models.TurnStartEvent;
import fr.aboucorp.teamchess.entities.model.utils.PieceList;
import fr.aboucorp.teamchess.entities.model.utils.SquareList;
import fr.aboucorp.teamchess.libgdx.Board3dManager;
import fr.aboucorp.teamchess.libgdx.models.ChessModel;

public class BoardManager implements GameEventSubscriber {

    public static int FIFTY_MOVE_RULE_NUMBER = 75;
    private int fiftyMoveBlackCounter = 0;
    private int fiftyMoveWhiteCounter = 0;
    private final Board board;
    private final Board3dManager board3dManager;
    private Piece selectedPiece;
    private GameEventManager eventManager;
    private Turn previousTurn;
    private Turn actualTurn;
    private boolean whiteCanCastleKing;
    private boolean whiteCanCastleQueen;
    private boolean blackCanCastleKing;
    private boolean blackCanCastleQueen;
    private boolean waitingForEnPassant;
    private SquareList possiblesMoves;
    private boolean kingIsInCheck;

    public BoardManager(Board3dManager board3dManager) {
        this.board = new Board();
        this.board3dManager = board3dManager;
        this.eventManager = GameEventManager.getINSTANCE();
        this.eventManager.subscribe(PartyEvent.class, this,1);
        this.eventManager.subscribe(TurnStartEvent.class, this,2);
        this.eventManager.subscribe(TurnEndEvent.class, this,1);
        this.eventManager.subscribe(PieceEvent.class, this,1);
    }

    public void createBoard() {
        this.board.initBoard();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        BoardManager.this.board3dManager.createSquares(BoardManager.this.board.getSquares());
                        BoardManager.this.board3dManager.createPieces(BoardManager.this.board.getWhitePieces());
                        BoardManager.this.board3dManager.createPieces(BoardManager.this.board.getBlackPieces());
                    }
                });
            }
        }).start();
    }

    public void moveToSquare(Square square) {
        String eventMessage = String.format("Move %s from %s to %s",
                selectedPiece.getPieceId().name(),
                selectedPiece.getActualSquare(),
                square);
        MoveEvent moveEvent = new MoveEvent(eventMessage, selectedPiece.getActualSquare(), square, selectedPiece, square.getPiece());
        this.eventManager.sendMessage(moveEvent);
        eat(square);
        this.selectedPiece.move(square);
        this.board3dManager.moveSelectedPieceIntoSquare(square);
        kingIsInCheck();
        checkIfCastling(square);
        resetHighlited();
    }

    private void kingIsInCheck() {
        if(kingIsInCheck){
            this.eventManager.sendMessage(new CheckOutEvent("King out of check", BoardEventType.CHECK_OUT, this.selectedPiece));
            this.kingIsInCheck = false;
        }
        List<Piece> causingCheck = this.selectedPiece.getMoveSet().moveCauseCheck(actualTurn.getTurnColor());
        if (causingCheck.size() > 0 ) {
            this.kingIsInCheck = true;
            Piece kingInCheck = this.previousTurn.getTurnColor() == ChessColor.WHITE
                    ? this.board.getWhitePieces().getPieceById(PieceId.WK)
                    : this.board.getBlackPieces().getPieceById(PieceId.BK);
            this.eventManager.sendMessage(new CheckInEvent("King is in check", BoardEventType.CHECK_IN,kingInCheck , causingCheck));
        }
    }

    public void eat(Square square) {
        Piece toBeEaten = square.getPiece();
        if (isEnPassantMove(square)) {
            toBeEaten = this.previousTurn.played;
        }
        if (toBeEaten != null) {
            if (toBeEaten.getChessColor() == ChessColor.WHITE) {
                this.board.getWhitePieces().removeByLocation(toBeEaten.getLocation());
            } else {
                this.board.getBlackPieces().removeByLocation(toBeEaten.getLocation());
            }
            this.board3dManager.moveToEven(toBeEaten);
            String eventMessage = String.format("Piece %s die on %s", toBeEaten.getPieceId().name(), toBeEaten.getLocation());
            this.eventManager.sendMessage(new PieceEvent(eventMessage, BoardEventType.DEATH, toBeEaten));
            toBeEaten.die();
        }
    }

    private boolean isEnPassantMove(Square destination) {
        return this.waitingForEnPassant
                && destination.getPiece() == null
                && this.selectedPiece.getLocation().getX() != destination.getLocation().getX();
    }

    private void resetHighlited() {
        this.board3dManager.unHighlightSquares(this.possiblesMoves);
        this.board3dManager.resetSelection();
        this.possiblesMoves = null;
    }

    private void checkIfCastling(Square square) {
        ChessColor turnColor = this.actualTurn.getTurnColor();
        Piece rookToMove = null;
        Square destination = null;
        if (this.whiteCanCastleQueen && turnColor == ChessColor.WHITE && square.getSquareLabel().equals("C1")) {
            rookToMove = this.board.getWhitePieces().getPieceById(PieceId.WLR);
            destination = this.board.getSquares().getSquareByLabel("D1");
        }
        if (this.blackCanCastleQueen && turnColor == ChessColor.BLACK && square.getSquareLabel().equals("C8")) {
            rookToMove = this.board.getBlackPieces().getPieceById(PieceId.BLR);
            destination = this.board.getSquares().getSquareByLabel("D8");
        }
        if (this.whiteCanCastleKing && turnColor == ChessColor.WHITE && square.getSquareLabel().equals("G1")) {
            rookToMove = this.board.getWhitePieces().getPieceById(PieceId.WRR);
            destination = this.board.getSquares().getSquareByLabel("F1");
        }
        if (this.blackCanCastleKing && turnColor == ChessColor.BLACK && square.getSquareLabel().equals("G8")) {
            rookToMove = this.board.getBlackPieces().getPieceById(PieceId.BRR);
            destination = this.board.getSquares().getSquareByLabel("F8");
        }
        if (rookToMove != null && destination != null) {
            this.board3dManager.moveToSquare(rookToMove, destination);
            rookToMove.move(destination);
        }
    }

    public void selectPiece(Piece touched) {
        this.selectedPiece = touched;
        this.board3dManager.selectPiece(touched);
        this.possiblesMoves = touched.getMoveSet().getNextMoves();
        this.hightLightPossibleMoves(this.possiblesMoves);
    }

    private void hightLightPossibleMoves(SquareList possibleMoves) {
        if (possibleMoves != null) {
            for (Square square : possibleMoves) {
                if (square.getPiece() == null) {
                    this.board3dManager.highlightEmptySquareFromLocation(square);
                } else {
                    this.board3dManager.highlightOccupiedSquareFromLocation(square);
                }
            }
        }
    }

    public void unHighlight() {
        this.board3dManager.resetSelection();
        if (possiblesMoves != null) {
            this.board3dManager.unHighlightSquares(this.possiblesMoves);
        }
    }

    private void dispatchPieceEvent(PieceEvent event) {
        if (event.type == BoardEventType.EN_PASSANT) {
            this.waitingForEnPassant = true;
        }
    }

    public void isGameFinished() {
        if(previousTurn != null) {
            boolean cantMove = true;
            for (Piece piece : board.getPiecesByColor(this.actualTurn.getTurnColor())) {
                if (piece.getMoveSet().getNextMoves().size() > 0) {
                    cantMove = false;
                }
            }
            if (cantMove) {
                if (kingIsInCheck) {
                    this.eventManager.sendMessage(new BoardEvent("Game is finished", BoardEventType.MATE));
                } else {
                    this.eventManager.sendMessage(new BoardEvent("Game is finished", BoardEventType.DRAW));
                }
            }
        }
    }

    public ChessColor getWinner() {
        PieceList opposites;
        ChessColor oppositeColor = actualTurn.getTurnColor() == ChessColor.WHITE ? ChessColor.BLACK : ChessColor.WHITE;
        if (actualTurn.getTurnColor() == ChessColor.WHITE) {
            opposites = this.board.getBlackPieces();
        } else {
            opposites = this.board.getWhitePieces();
        }
        boolean canMove = false;
        for (Piece piece : opposites) {
            piece.getMoveSet().calculateNextMoves(oppositeColor);
            if (piece.getMoveSet().getNextMoves().size() > 0) {
                canMove = true;
            }
        }
        if (canMove) {
            return oppositeColor;
        }
        return null;
    }

    @Override
    public void receiveGameEvent(GameEvent event) {
        if (event instanceof TurnStartEvent) {
            this.previousTurn = actualTurn;
            this.actualTurn = ((TurnEvent) event).turn;
            canClaimADraw();
            canCastle();
            isGameFinished();
        } else if (event instanceof TurnEndEvent) {
            this.selectedPiece = null;
            this.possiblesMoves = null;
            this.waitingForEnPassant = false;
        } else if (event instanceof PieceEvent) {
            dispatchPieceEvent((PieceEvent) event);
        }
    }

    private void canCastle() {
        if(whiteCanCastleKing() ){
            this.whiteCanCastleKing = true;
            this.eventManager.sendMessage(new PieceEvent("White can castle on king side", BoardEventType.CASTLE_KING,this.board.getWhitePieces().getPieceById(PieceId.WK)));
        }else{
            this.whiteCanCastleKing = false;
        }
        if(whiteCanCastleQueen()){
            this.whiteCanCastleQueen = true;
            this.eventManager.sendMessage(new PieceEvent("White can castle on queen side", BoardEventType.CASTLE_QUEEN,this.board.getWhitePieces().getPieceById(PieceId.WK)));
        }else{
            this.whiteCanCastleQueen = false;
        }
        if(blackCanCastleKing()){
            this.blackCanCastleKing = true;
            this.eventManager.sendMessage(new PieceEvent("Black can castle on king side", BoardEventType.CASTLE_KING,this.board.getBlackPieces().getPieceById(PieceId.BK)));
        }else{
            this.blackCanCastleKing = false;
        }
        if(blackCanCastleQueen()){
            this.blackCanCastleQueen = true;
            this.eventManager.sendMessage(new PieceEvent("Black can castle on queen side", BoardEventType.CASTLE_QUEEN,this.board.getBlackPieces().getPieceById(PieceId.BK)));
        }else{
            this.blackCanCastleQueen = false;
        }
    }

    private boolean blackCanCastleQueen() {
        return this.board.getBlackPieces().getPieceById(PieceId.BK).isFirstMove()
                && this.board.getBlackPieces().getPieceById(PieceId.BLR).isFirstMove()
                && this.board.getSquares().getSquareByLabel("D8").getPiece() == null
                && this.board.getSquares().getSquareByLabel("C8").getPiece() == null;
    }

    private boolean blackCanCastleKing() {
        return this.board.getBlackPieces().getPieceById(PieceId.BK).isFirstMove()
                && this.board.getBlackPieces().getPieceById(PieceId.BLR).isFirstMove()
                && this.board.getSquares().getSquareByLabel("F8").getPiece() == null
                && this.board.getSquares().getSquareByLabel("G8").getPiece() == null;
    }

    private boolean whiteCanCastleQueen() {
        return this.board.getWhitePieces().getPieceById(PieceId.WK).isFirstMove()
                && this.board.getWhitePieces().getPieceById(PieceId.WLR).isFirstMove()
                && this.board.getSquares().getSquareByLabel("D1").getPiece() == null
                && this.board.getSquares().getSquareByLabel("C1").getPiece() == null
                && this.board.getSquares().getSquareByLabel("CB1").getPiece() == null;
    }

    private boolean whiteCanCastleKing() {
        return this.board.getWhitePieces().getPieceById(PieceId.WK).isFirstMove()
            && this.board.getWhitePieces().getPieceById(PieceId.WRR).isFirstMove()
                && this.board.getSquares().getSquareByLabel("G1").getPiece() == null
                &&  this.board.getSquares().getSquareByLabel("F1").getPiece() == null;
    }

    private void canClaimADraw() {
        checkPat();
        fiftyMoveRule();
    }

    public void checkPat(){
        
    }

    private void fiftyMoveRule(){
        if (previousTurn != null) {
            if (PieceId.isPawn(this.previousTurn.played.getPieceId()) || previousTurn.getDeadPiece() != null) {
                if(this.previousTurn.getTurnColor() == ChessColor.WHITE){
                    this.fiftyMoveBlackCounter = 0;
                }else{
                    this.fiftyMoveWhiteCounter = 0;
                }
            } else {
                if(this.previousTurn.getTurnColor() == ChessColor.WHITE){
                    this.fiftyMoveBlackCounter++;
                }else{
                    this.fiftyMoveWhiteCounter++;
                }
            }
        }
        if (fiftyMoveWhiteCounter >= FIFTY_MOVE_RULE_NUMBER && fiftyMoveBlackCounter >= FIFTY_MOVE_RULE_NUMBER) {
            this.eventManager.sendMessage(new PartyEvent("Player can claim a draw following the fifty move rule "));
        }
    }

    public ArrayList<ChessModel> getPossibleSquareModels() {
        return this.board3dManager.getSquareModelsFromPossibleMoves(this.possiblesMoves);
    }

    public Piece getPieceFromLocation(Location location, ChessColor color) {
        ArrayList<Piece> arrayList = null;
        if (color == ChessColor.WHITE) {
            arrayList = this.board.getWhitePieces();
        } else {
            arrayList = this.board.getBlackPieces();
        }
        for (Piece piece : arrayList) {
            if (piece.getLocation().equals(location)) {
                return piece;
            }
        }
        return null;
    }

    public Square getSquareFromLocation(Location location) {
        for (Square square : this.board.getSquares()) {
            if (square.getLocation().equals(location)) {
                return square;
            }
        }
        return null;
    }

    public ArrayList<ChessModel> getChessSquareModels() {
        return this.board3dManager.getChessSquareModels();
    }

    public Camera getCamera() {
        return this.board3dManager.getCamera();
    }

    public String getFenFromBoard(){
        return "";
    }

    public ArrayList<ChessModel> getBlackPieceModels() {
        return this.board3dManager.getBlackPieceModels();
    }

    public ArrayList<ChessModel> getWhitePieceModels() {
        return this.board3dManager.getWhitePieceModels();
    }

}
