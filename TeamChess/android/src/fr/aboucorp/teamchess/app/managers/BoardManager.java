package fr.aboucorp.teamchess.app.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;

import java.util.ArrayList;
import java.util.List;

import fr.aboucorp.teamchess.app.utils.GdxPostRunner;
import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.Location;
import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.RuleSet;
import fr.aboucorp.teamchess.entities.model.Square;
import fr.aboucorp.teamchess.entities.model.Turn;
import fr.aboucorp.teamchess.entities.model.enums.BoardEventType;
import fr.aboucorp.teamchess.entities.model.events.GameEventManager;
import fr.aboucorp.teamchess.entities.model.events.GameEventSubscriber;
import fr.aboucorp.teamchess.entities.model.events.models.CastlingEvent;
import fr.aboucorp.teamchess.entities.model.events.models.EnPassantEvent;
import fr.aboucorp.teamchess.entities.model.events.models.GameEvent;
import fr.aboucorp.teamchess.entities.model.events.models.LogEvent;
import fr.aboucorp.teamchess.entities.model.events.models.MoveEvent;
import fr.aboucorp.teamchess.entities.model.events.models.PartyEvent;
import fr.aboucorp.teamchess.entities.model.events.models.PieceEvent;
import fr.aboucorp.teamchess.entities.model.events.models.TurnEndEvent;
import fr.aboucorp.teamchess.entities.model.events.models.TurnStartEvent;
import fr.aboucorp.teamchess.entities.model.exceptions.FenStringBadFormatException;
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
    private final RuleSet ruleSet;
    private SquareList possiblesMoves;

    public BoardManager(Board3dManager board3dManager) {
        this.board = new Board();
        this.board3dManager = board3dManager;
        this.eventManager = GameEventManager.getINSTANCE();
        this.eventManager.subscribe(PartyEvent.class, this, 1);
        this.eventManager.subscribe(TurnStartEvent.class, this, 2);
        this.eventManager.subscribe(TurnEndEvent.class, this, 1);
        this.eventManager.subscribe(PieceEvent.class, this, 1);
        this.eventManager.subscribe(EnPassantEvent.class, this, 1);
        this.ruleSet = new RuleSet(board);
    }

    @Override
    public void receiveGameEvent(GameEvent event) {
        if (event instanceof TurnStartEvent) {
            manageTurnStart((TurnStartEvent) event);
        } else if (event instanceof TurnEndEvent) {
            manageTurnEnd();
        }else if(event instanceof CastlingEvent){
            ((CastlingEvent) event).piece.move(((CastlingEvent) event).detination);
            this.board3dManager.moveToSquare(((CastlingEvent) event).piece,((CastlingEvent) event).detination);
        }
    }

    void createBoard() {
        this.board.initBoard();
        new Thread(() -> Gdx.app.postRunnable(() -> {
            BoardManager.this.board3dManager.createSquares(BoardManager.this.board.getSquares());
            BoardManager.this.board3dManager.createPieces(BoardManager.this.board.getWhitePieces());
            BoardManager.this.board3dManager.createPieces(BoardManager.this.board.getBlackPieces());
        })).start();
    }

    ChessColor loadBoard(String fenString) throws FenStringBadFormatException,NumberFormatException {

        String[] parts = fenString.trim().split(" ");
        if(parts.length < 5 || parts.length > 6){
            throw new FenStringBadFormatException("Cannot load game from fen string, fen string doesn't contains enought parts");
        }

        GdxPostRunner runner = new GdxPostRunner() {
            @Override
            public void execute() {
                try {
                    clearBoard();
                    board.loadBoard(parts[0]);
                    BoardManager.this.board3dManager.createPieces(BoardManager.this.board.getWhitePieces());
                    BoardManager.this.board3dManager.createPieces(BoardManager.this.board.getBlackPieces());
                } catch (FenStringBadFormatException e) {
                    e.printStackTrace();
                }

            }
        };
        runner.start();

        this.ruleSet.whiteCanCastleKing = parts[2].contains("K");
        this.ruleSet.whiteCanCastleQueen = parts[2].contains("Q");
        this.ruleSet.blackCanCastleKing = parts[2].contains("k");
        this.ruleSet.blackCanCastleQueen = parts[2].contains("q");
        if(!parts[3].equals("-")) {
            this.ruleSet.enPassant = this.board.getSquares().getSquareByLabel(parts[3]);
        }
        this.ruleSet.moveNumber = Integer.parseInt(parts[4]);
        this.ruleSet.fiftyMoveCounter = Integer.parseInt(parts[5]);
        if(parts[1].equals("w")){
            return ChessColor.WHITE;
        }else{
            return ChessColor.BLACK;
        }
    }

    private void clearBoard() {
        this.selectedPiece = null;
        this.previousTurn = null;
        this.actualTurn = null;
        this.board.clearBoard();
        this.board3dManager.clearBoard();
    }


    void moveToSquare(Square square) {
        String eventMessage = String.format("Move %s from %s to %s",
                selectedPiece.getPieceId().name(),
                selectedPiece.getActualSquare(),
                square);
        MoveEvent moveEvent = new MoveEvent(eventMessage, selectedPiece.getActualSquare(), square, selectedPiece, square.getPiece());
        this.eventManager.sendMessage(moveEvent);
        eat(square);
        this.selectedPiece.move(square);
        this.board3dManager.moveSelectedPieceIntoSquare(square);
        this.ruleSet.isKingInCheck(this.selectedPiece);
        this.ruleSet.checkIfCastling(square);
        resetHighlited();
    }



    private void eat(Square square) {
        Piece toBeEaten = square.getPiece();
        if (ruleSet.isEnPassantMove(this.selectedPiece, square)) {
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

    private void resetHighlited() {
        this.board3dManager.unHighlightSquares(this.possiblesMoves);
        this.board3dManager.resetSelection();
        this.possiblesMoves = null;
    }


    void selectPiece(Piece touched) {
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

    void unHighlight() {
        this.board3dManager.resetSelection();
        if (possiblesMoves != null) {
            this.board3dManager.unHighlightSquares(this.possiblesMoves);
        }
    }


    private void manageTurnStart(TurnStartEvent event) {
        this.previousTurn = actualTurn;
        this.actualTurn = event.turn;
        this.eventManager.sendMessage(new LogEvent(this.getFenFromBoard()));
    }

    private void manageTurnEnd() {
        this.selectedPiece = null;
        this.possiblesMoves = null;
    }


    private String getFenFromBoard() {
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
        if (this.ruleSet.whiteCanCastleKing()) {
            fenString.append('K');
        }
        if (this.ruleSet.whiteCanCastleQueen()) {
            fenString.append('Q');
        }
        if (this.ruleSet.blackCanCastleKing()) {
            fenString.append('k');
        }
        if (this.ruleSet.blackCanCastleQueen()) {
            fenString.append('q');
        }
        fenString.append(' ');
        if (this.ruleSet.enPassant != null) {
            fenString.append(this.ruleSet.enPassant.getSquareLabel());
        } else {
            fenString.append('-');
        }
        fenString.append(' ');
        fenString.append(this.ruleSet.fiftyMoveCounter);
        fenString.append(' ');
        fenString.append(this.ruleSet.moveNumber);
        return fenString.toString();
    }

    ArrayList<ChessModel> getPossibleSquareModels() {
        return this.board3dManager.getSquareModelsFromPossibleMoves(this.possiblesMoves);
    }

    Piece getPieceFromLocation(Location location, ChessColor color) {
        ArrayList<Piece> arrayList;
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

    Square getSquareFromLocation(Location location) {
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

    ArrayList<ChessModel> getBlackPieceModels() {
        return this.board3dManager.getBlackPieceModels();
    }

    ArrayList<ChessModel> getWhitePieceModels() {
        return this.board3dManager.getWhitePieceModels();
    }

    public ChessColor getWinner() {
        return this.ruleSet.getWinner();
    }
}
