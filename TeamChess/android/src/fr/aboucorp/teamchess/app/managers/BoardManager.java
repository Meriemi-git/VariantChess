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
import fr.aboucorp.teamchess.entities.model.events.models.EnPassantEvent;
import fr.aboucorp.teamchess.entities.model.events.models.GameEvent;
import fr.aboucorp.teamchess.entities.model.events.models.LogEvent;
import fr.aboucorp.teamchess.entities.model.events.models.MoveEvent;
import fr.aboucorp.teamchess.entities.model.events.models.PartyEvent;
import fr.aboucorp.teamchess.entities.model.events.models.PieceEvent;
import fr.aboucorp.teamchess.entities.model.events.models.TurnEndEvent;
import fr.aboucorp.teamchess.entities.model.events.models.TurnStartEvent;
import fr.aboucorp.teamchess.entities.model.exceptions.FenStringBadFormatException;
import fr.aboucorp.teamchess.entities.model.utils.PieceList;
import fr.aboucorp.teamchess.entities.model.utils.SquareList;
import fr.aboucorp.teamchess.libgdx.Board3dManager;
import fr.aboucorp.teamchess.libgdx.models.ChessModel;

public class BoardManager implements GameEventSubscriber {

    public static int FIFTY_MOVE_RULE_NUMBER = 75;
    private int fiftyMoveCounter = 0;
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
    private SquareList possiblesMoves;
    private boolean kingIsInCheck;
    private Square enPassant;
    private int moveNumber = 0;
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();

    public BoardManager(Board3dManager board3dManager) {
        this.board = new Board();
        this.board3dManager = board3dManager;
        this.eventManager = GameEventManager.getINSTANCE();
        this.eventManager.subscribe(PartyEvent.class, this, 1);
        this.eventManager.subscribe(TurnStartEvent.class, this, 2);
        this.eventManager.subscribe(TurnEndEvent.class, this, 1);
        this.eventManager.subscribe(PieceEvent.class, this, 1);
        this.eventManager.subscribe(EnPassantEvent.class, this, 1);
    }

    @Override
    public void receiveGameEvent(GameEvent event) {
        if (event instanceof TurnStartEvent) {
            manageTurnStart((TurnStartEvent) event);
        } else if (event instanceof TurnEndEvent) {
            manageTurnEnd();
        } else if (event instanceof EnPassantEvent) {
            if (((EnPassantEvent) event).type == BoardEventType.EN_PASSANT) {
                this.enPassant = ((EnPassantEvent) event).destination;
            }
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
                        BoardManager.this.board3dManager.createSquares(BoardManager.this.board.getSquares());
                        BoardManager.this.board3dManager.createPieces(BoardManager.this.board.getWhitePieces());
                        BoardManager.this.board3dManager.createPieces(BoardManager.this.board.getBlackPieces());
                    }
                });
            }
        }).start();
    }

    public ChessColor loadBoard(String fenString) throws FenStringBadFormatException,NumberFormatException {

        String[] parts = fenString.trim().split(" ");
        if(parts.length < 5 || parts.length > 6){
            throw new FenStringBadFormatException("Cannot load game from fen string, fen string doesn't contains enought parts");
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        clearBoard();
                        try {
                            board.loadBoard(parts[0]);
                            BoardManager.this.board3dManager.createPieces(BoardManager.this.board.getWhitePieces());
                            BoardManager.this.board3dManager.createPieces(BoardManager.this.board.getBlackPieces());
                        } catch (FenStringBadFormatException e) {
                            e.printStackTrace();
                        }finally {
                            synchronized (lock2) {
                                lock2.notify();
                            }
                        }
                    }
                });
                synchronized (lock2) {
                    try {
                        lock2.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                synchronized (lock1) {
                    lock1.notify();
                }
            }
        }).start();
        synchronized (lock1) {
            try {
                lock1.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.whiteCanCastleKing = parts[2].contains("K");
        this.whiteCanCastleQueen = parts[2].contains("Q");
        this.blackCanCastleKing = parts[2].contains("k");
        this.blackCanCastleQueen = parts[2].contains("q");
        if(!parts[3].equals("-")) {
            this.enPassant = this.board.getSquares().getSquareByLabel(parts[3]);
        }
        this.moveNumber = Integer.parseInt(parts[4]);
        this.fiftyMoveCounter = Integer.parseInt(parts[5]);
        if(parts[1] == "w"){
            return ChessColor.WHITE;
        }else{
            return ChessColor.BLACK;
        }
    }

    private void clearBoard() {
        this.selectedPiece = null;
        this.previousTurn = null;
        this.actualTurn = null;
        this.possiblesMoves = null;
        this.kingIsInCheck = false;
        this.board.clearBoard();
        this.board3dManager.clearBoard();
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
        if (kingIsInCheck) {
            this.eventManager.sendMessage(new CheckOutEvent("King out of check", BoardEventType.CHECK_OUT, this.selectedPiece));
            this.kingIsInCheck = false;
        }
        List<Piece> causingCheck = this.selectedPiece.getMoveSet().moveCauseCheck(actualTurn.getTurnColor());
        if (causingCheck.size() > 0) {
            this.kingIsInCheck = true;
            Piece kingInCheck = this.previousTurn.getTurnColor() == ChessColor.WHITE
                    ? this.board.getWhitePieces().getPieceById(PieceId.WK)
                    : this.board.getBlackPieces().getPieceById(PieceId.BK);
            this.eventManager.sendMessage(new CheckInEvent("King is in check", BoardEventType.CHECK_IN, kingInCheck, causingCheck));
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
        return this.enPassant != null
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

    public void isGameFinished() {
        if (previousTurn != null) {
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

    private void manageTurnStart(TurnStartEvent event) {
        this.previousTurn = actualTurn;
        this.actualTurn = event.turn;
        if(previousTurn != null) {
            canClaimADraw();
            canCastle();
            isGameFinished();
            this.eventManager.sendMessage(new LogEvent(this.getFenFromBoard()));
        }
        if (event.turn.getTurnColor() == ChessColor.WHITE) {
            this.moveNumber++;
        }

    }

    private void manageTurnEnd() {
        this.selectedPiece = null;
        this.possiblesMoves = null;
        this.enPassant = null;
    }

    private void canCastle() {
        if (whiteCanCastleKingNow()) {
            this.whiteCanCastleKing = true;
            this.eventManager.sendMessage(new PieceEvent("White can castle on king side", BoardEventType.CASTLE_KING, this.board.getWhitePieces().getPieceById(PieceId.WK)));
        } else {
            this.whiteCanCastleKing = false;
        }
        if (whiteCanCastleQueenNow()) {
            this.whiteCanCastleQueen = true;
            this.eventManager.sendMessage(new PieceEvent("White can castle on queen side", BoardEventType.CASTLE_QUEEN, this.board.getWhitePieces().getPieceById(PieceId.WK)));
        } else {
            this.whiteCanCastleQueen = false;
        }
        if (blackCanCastleKingNow()) {
            this.blackCanCastleKing = true;
            this.eventManager.sendMessage(new PieceEvent("Black can castle on king side", BoardEventType.CASTLE_KING, this.board.getBlackPieces().getPieceById(PieceId.BK)));
        } else {
            this.blackCanCastleKing = false;
        }
        if (blackCanCastleQueenNow()) {
            this.blackCanCastleQueen = true;
            this.eventManager.sendMessage(new PieceEvent("Black can castle on queen side", BoardEventType.CASTLE_QUEEN, this.board.getBlackPieces().getPieceById(PieceId.BK)));
        } else {
            this.blackCanCastleQueen = false;
        }
    }

    private boolean blackCanCastleQueenNow() {
        return this.blackCanCastleQueen()
                && this.board.getSquares().getSquareByLabel("D8").getPiece() == null
                && this.board.getSquares().getSquareByLabel("C8").getPiece() == null;
    }

    private boolean blackCanCastleQueen() {
        return this.board.getBlackPieces().getPieceById(PieceId.BK).isFirstMove()
                && this.board.getBlackPieces().getPieceById(PieceId.BLR).isFirstMove();
    }

    private boolean blackCanCastleKingNow() {
        return this.blackCanCastleKing()
                && this.board.getSquares().getSquareByLabel("F8").getPiece() == null
                && this.board.getSquares().getSquareByLabel("G8").getPiece() == null;
    }

    private boolean blackCanCastleKing() {
        return this.board.getBlackPieces().getPieceById(PieceId.BK).isFirstMove()
                && this.board.getBlackPieces().getPieceById(PieceId.BLR).isFirstMove();
    }

    private boolean whiteCanCastleQueenNow() {
        return whiteCanCastleQueen()
                && this.board.getSquares().getSquareByLabel("D1").getPiece() == null
                && this.board.getSquares().getSquareByLabel("C1").getPiece() == null
                && this.board.getSquares().getSquareByLabel("CB1").getPiece() == null;
    }

    private boolean whiteCanCastleQueen() {
        return this.board.getWhitePieces().getPieceById(PieceId.WK).isFirstMove()
                && this.board.getWhitePieces().getPieceById(PieceId.WLR).isFirstMove() ;
    }

    private boolean whiteCanCastleKingNow() {
        return whiteCanCastleKing()
                && this.board.getSquares().getSquareByLabel("G1").getPiece() == null
                && this.board.getSquares().getSquareByLabel("F1").getPiece() == null;
    }

    private boolean whiteCanCastleKing() {
        return this.board.getWhitePieces().getPieceById(PieceId.WK).isFirstMove()
                && this.board.getWhitePieces().getPieceById(PieceId.WRR).isFirstMove();

    }

    private void canClaimADraw() {
        fiftyMoveRule();
    }

    private void fiftyMoveRule() {
        if (previousTurn != null) {
            if (PieceId.isPawn(this.previousTurn.played.getPieceId()) || previousTurn.getDeadPiece() != null) {
                this.fiftyMoveCounter = 0;
            } else {
                this.fiftyMoveCounter++;
            }
        }
        if (fiftyMoveCounter >= FIFTY_MOVE_RULE_NUMBER * 2) {
            this.eventManager.sendMessage(new PartyEvent("Player can claim a draw following the fifty move rule "));
        }
    }

    public String getFenFromBoard() {
        StringBuilder fenString = new StringBuilder();
        for (int z = 7; z >= 0; z--) {
            List<Square> lines = this.board.getSquares().getSquaresByLine(z);
            int emptySquare = 0;
            for (int x = 7; x >= 0; x--) {
                Square square = lines.get(x);
                Piece piece = square.getPiece();
                if (piece != null) {
                    if (emptySquare > 0) {
                        fenString.append(emptySquare);
                        emptySquare = 0;
                    }
                    fenString.append(piece.fen());
                } else {
                    emptySquare++;
                }
            }
            if (emptySquare > 0) {
                fenString.append(emptySquare);
            }
            fenString.append('/');
        }
        fenString.append(' ');
        fenString.append(this.actualTurn.getTurnColor() == ChessColor.WHITE ? 'w' : 'b');
        fenString.append(' ');
        if (this.whiteCanCastleKing()) {
            fenString.append('K');
        }
        if (this.whiteCanCastleQueen()) {
            fenString.append('Q');
        }
        if (this.blackCanCastleKing()) {
            fenString.append('k');
        }
        if (this.blackCanCastleQueen()) {
            fenString.append('q');
        }
        fenString.append(' ');
        if (enPassant != null) {
            fenString.append(enPassant.getSquareLabel());
        } else {
            fenString.append('-');
        }
        fenString.append(' ');
        fenString.append(this.fiftyMoveCounter);
        fenString.append(' ');
        fenString.append(this.moveNumber);
        return fenString.toString();
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

    public Camera getCamera() {
        return this.board3dManager.getCamera();
    }

    public ArrayList<ChessModel> getBlackPieceModels() {
        return this.board3dManager.getBlackPieceModels();
    }

    public ArrayList<ChessModel> getWhitePieceModels() {
        return this.board3dManager.getWhitePieceModels();
    }

}
