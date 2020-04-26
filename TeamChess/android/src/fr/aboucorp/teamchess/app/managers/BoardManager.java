package fr.aboucorp.teamchess.app.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;

import java.util.ArrayList;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.Location;
import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.Square;
import fr.aboucorp.teamchess.entities.model.Turn;
import fr.aboucorp.teamchess.entities.model.enums.PieceEventType;
import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.entities.model.events.GameEventManager;
import fr.aboucorp.teamchess.entities.model.events.GameEventSubscriber;
import fr.aboucorp.teamchess.entities.model.events.models.BoardEvent;
import fr.aboucorp.teamchess.entities.model.events.models.CheckEvent;
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

    private final Board board;
    private final Board3dManager board3dManager;
    private Piece selectedPiece;
    private GameEventManager eventManager;
    private Turn previousTurn;
    private Turn actualTurn;
    private boolean waitingForBigCastle;
    private boolean waitingForLittleCastle;
    private boolean waitingForEnPassant;
    private SquareList possiblesMoves;

    public BoardManager(Board3dManager board3dManager) {
        this.board = new Board();
        this.board3dManager = board3dManager;
        this.eventManager = GameEventManager.getINSTANCE();
        this.eventManager.subscribe(PartyEvent.class, this);
        this.eventManager.subscribe(TurnStartEvent.class, this);
        this.eventManager.subscribe(TurnEndEvent.class, this);
        this.eventManager.subscribe(PieceEvent.class, this);
    }

    @Override
    public void receiveGameEvent(GameEvent event) {
        if (event instanceof TurnStartEvent) {
            this.previousTurn = actualTurn;
            this.actualTurn = ((TurnEvent) event).turn;
        } else if (event instanceof TurnEndEvent) {
            this.selectedPiece = null;
            this.possiblesMoves = null;
            this.waitingForLittleCastle = false;
            this.waitingForBigCastle = false;
            this.waitingForEnPassant = false;
        } else if (event instanceof PieceEvent) {
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
                        BoardManager.this.board3dManager.createSquares(BoardManager.this.board.getSquares());
                        BoardManager.this.board3dManager.createPieces(BoardManager.this.board.getWhitePieces());
                        BoardManager.this.board3dManager.createPieces(BoardManager.this.board.getBlackPieces());
                    }
                });
            }
        }).start();
    }

    public void moveSelectedPieceToSquare(Square square) {
        String eventMessage = String.format("Move %s from %s to %s",
                selectedPiece.getPieceId().name(),
                selectedPiece.getActualSquare(),
                square);
        MoveEvent moveEvent = new MoveEvent(eventMessage, selectedPiece.getActualSquare(), square, selectedPiece, square.getPiece());
        this.eventManager.sendMessage(moveEvent);
        eat(square);
        this.selectedPiece.move(square);
        this.board3dManager.moveSelectedPieceIntoSquare(square);
        if (this.waitingForBigCastle || this.waitingForLittleCastle) {
            checkIfCastling(square);
        }
        resetHighlited();
        kingIsInCheck();
    }

    private void kingIsInCheck() {
        Piece kingInCheck = this.selectedPiece.getMoveSet().pieceMoveCauseCheck(this.selectedPiece, board, actualTurn.getTurnColor());
        if (kingInCheck != null) {
            this.eventManager.sendMessage(new CheckEvent("King is in check", PieceEventType.CHECK, kingInCheck, this.selectedPiece));
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
            this.eventManager.sendMessage(new PieceEvent(eventMessage, PieceEventType.DEATH, toBeEaten));
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
        if (this.waitingForBigCastle) {
            if (turnColor == ChessColor.WHITE && square.getSquareLabel().equals("C1")) {
                rookToMove = this.board.getWhitePieces().getPieceById(PieceId.WLR);
                destination = this.board.getSquares().getSquareByLabel("D1");
            } else if (turnColor == ChessColor.BLACK && square.getSquareLabel().equals("C8")) {
                this.board.getWhitePieces().getPieceById(PieceId.BLR).getLocation();
                destination = this.board.getSquares().getSquareByLabel("D8");
            }
        }
        if (this.waitingForLittleCastle) {
            if (turnColor == ChessColor.WHITE && square.getSquareLabel().equals("G1")) {
                rookToMove = this.board.getWhitePieces().getPieceById(PieceId.WRR);
                destination = this.board.getSquares().getSquareByLabel("F1");
            } else if (turnColor == ChessColor.BLACK && square.getSquareLabel().equals("G8")) {
                rookToMove = this.board.getWhitePieces().getPieceById(PieceId.BRR);
                destination = this.board.getSquares().getSquareByLabel("F8");
            }
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
        } else {
            this.eventManager.sendMessage(new BoardEvent("Piece cannot Move"));
        }
    }

    public void unHighlight() {
        this.board3dManager.resetSelection();
        if (possiblesMoves != null) {
            this.board3dManager.unHighlightSquares(this.possiblesMoves);
        }
    }

    private void dispatchPieceEvent(PieceEvent event) {
        if (event.type == PieceEventType.LITTLE_CASTLING || event.type == PieceEventType.BIG_CASTLING) {
            manageCastling(event.type);
        } else if (event.type == PieceEventType.EN_PASSANT) {
            this.waitingForEnPassant = true;
        }
    }

    private void manageCastling(PieceEventType type) {
        if (type == PieceEventType.BIG_CASTLING) {
            this.waitingForBigCastle = true;
        } else if (type == PieceEventType.LITTLE_CASTLING) {
            this.waitingForLittleCastle = true;
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

    public ArrayList<ChessModel> getBlackPieceModels() {
        return this.board3dManager.getBlackPieceModels();
    }

    public ArrayList<ChessModel> getWhitePieceModels() {
        return this.board3dManager.getWhitePieceModels();
    }

    public Piece getSelectedPiece() {
        return selectedPiece;
    }

    public boolean isGameFinished() {
        boolean cantMove = true;
        for (Piece piece : board.getPieceByColor(this.actualTurn.getTurnColor())) {
            if (piece.getMoveSet().getNextMoves().size() > 0) {
                cantMove = false;
            }
        }
        return cantMove;
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
            piece.getMoveSet().calculateNextMoves(this.board, oppositeColor);
            if (piece.getMoveSet().getNextMoves().size() > 0) {
                canMove = true;
            }
        }
        if (canMove) {
            return oppositeColor;
        }
        return null;
    }
}
