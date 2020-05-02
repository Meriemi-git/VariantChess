package fr.aboucorp.teamchess.app.managers.boards;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

import fr.aboucorp.teamchess.app.utils.GdxPostRunner;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.Location;
import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.Square;
import fr.aboucorp.teamchess.entities.model.boards.Board;
import fr.aboucorp.teamchess.entities.model.enums.BoardEventType;
import fr.aboucorp.teamchess.entities.model.events.GameEventSubscriber;
import fr.aboucorp.teamchess.entities.model.events.models.CastlingEvent;
import fr.aboucorp.teamchess.entities.model.events.models.EnPassantEvent;
import fr.aboucorp.teamchess.entities.model.events.models.GameEvent;
import fr.aboucorp.teamchess.entities.model.events.models.LogEvent;
import fr.aboucorp.teamchess.entities.model.events.models.PieceEvent;
import fr.aboucorp.teamchess.entities.model.events.models.TurnEndEvent;
import fr.aboucorp.teamchess.entities.model.events.models.TurnStartEvent;
import fr.aboucorp.teamchess.entities.model.exceptions.FenStringBadFormatException;
import fr.aboucorp.teamchess.entities.model.rules.ClassicRuleSet;
import fr.aboucorp.teamchess.entities.model.utils.SquareList;
import fr.aboucorp.teamchess.libgdx.Board3dManager;
import fr.aboucorp.teamchess.libgdx.models.ChessModel;

public class ClassicBoardManager extends BoardManager implements GameEventSubscriber {


    public ClassicBoardManager(Board3dManager board3dManager, Board board, ClassicRuleSet ruleSet) {
        super(board,board3dManager,ruleSet);
        this.eventManager.subscribe(EnPassantEvent.class, this, 1);
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
    @Override
    public void createBoard() {
        this.board.initBoard();
        new Thread(() -> Gdx.app.postRunnable(() -> {
            ClassicBoardManager.this.board3dManager.createSquares(ClassicBoardManager.this.board.getSquares());
            ClassicBoardManager.this.board3dManager.createPieces(ClassicBoardManager.this.board.getWhitePieces());
            ClassicBoardManager.this.board3dManager.createPieces(ClassicBoardManager.this.board.getBlackPieces());
        })).start();
    }

    @Override
    public ChessColor loadBoard(String fenString) throws FenStringBadFormatException,NumberFormatException {

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
                    ClassicBoardManager.this.board3dManager.createPieces(ClassicBoardManager.this.board.getWhitePieces());
                    ClassicBoardManager.this.board3dManager.createPieces(ClassicBoardManager.this.board.getBlackPieces());
                } catch (FenStringBadFormatException e) {
                    e.printStackTrace();
                }

            }
        };
        runner.start();

        ((ClassicRuleSet)this.ruleSet).whiteCanCastleKing = parts[2].contains("K");
        ((ClassicRuleSet)this.ruleSet).whiteCanCastleQueen = parts[2].contains("Q");
        ((ClassicRuleSet)this.ruleSet).blackCanCastleKing = parts[2].contains("k");
        ((ClassicRuleSet)this.ruleSet).blackCanCastleQueen = parts[2].contains("q");
        if(!parts[3].equals("-")) {
            ((ClassicRuleSet)this.ruleSet).enPassant = this.board.getSquares().getSquareByLabel(parts[3]);
        }
        this.ruleSet.moveNumber = Integer.parseInt(parts[4]);
        ((ClassicRuleSet)this.ruleSet).fiftyMoveCounter = Integer.parseInt(parts[5]);
        if(parts[1].equals("w")){
            return ChessColor.WHITE;
        }else{
            return ChessColor.BLACK;
        }
    }

    @Override
    public Piece moveToSquare(Square square) {
        Piece eated = eat(square);
        this.selectedPiece.move(square);
        this.board3dManager.moveSelectedPieceIntoSquare(square);
        ((ClassicRuleSet)this.ruleSet).isKingInCheck(this.selectedPiece);
        ((ClassicRuleSet)this.ruleSet).checkIfCastling(square);
        resetHighlited();
        return eated;
    }

    private void resetHighlited() {
        this.board3dManager.unHighlightSquares(this.possiblesMoves);
        this.board3dManager.resetSelection();
        this.possiblesMoves = null;
    }

    @Override
    public void selectPiece(Piece touched) {
        super.selectPiece(touched);
        this.selectedPiece = touched;
        this.board3dManager.selectPiece(touched);
        this.possiblesMoves = touched.getMoveSet().getNextMoves();
        this.hightLightPossibleMoves(this.possiblesMoves);
    }

    @Override
    public void unHighlight() {
        super.unHighlight();
        this.board3dManager.resetSelection();
        if (possiblesMoves != null) {
            this.board3dManager.unHighlightSquares(this.possiblesMoves);
        }
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

    @Override
    protected void manageTurnStart(TurnStartEvent event) {
        this.previousTurn = actualTurn;
        this.actualTurn = event.turn;
        this.eventManager.sendMessage(new LogEvent(this.getFenFromBoard()));
        GdxPostRunner runner = new GdxPostRunner() {
            @Override
            public void execute() {
                ClassicBoardManager.this.board3dManager.moveCameraOnNewTurn(ClassicBoardManager.this.actualTurn.getTurnColor());
            }
        };
        runner.startAsync();
    }

    @Override
    protected void manageTurnEnd() {
        this.selectedPiece = null;
        this.possiblesMoves = null;
    }

    private Piece eat(Square square) {
        Piece toBeEaten = square.getPiece();
        if (((ClassicRuleSet)this.ruleSet).isEnPassantMove(this.selectedPiece, square)) {
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
        return toBeEaten;
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
        if (((ClassicRuleSet)this.ruleSet).whiteCanCastleKing()) {
            fenString.append('K');
        }
        if (((ClassicRuleSet)this.ruleSet).whiteCanCastleQueen()) {
            fenString.append('Q');
        }
        if (((ClassicRuleSet)this.ruleSet).blackCanCastleKing()) {
            fenString.append('k');
        }
        if (((ClassicRuleSet)this.ruleSet).blackCanCastleQueen()) {
            fenString.append('q');
        }
        fenString.append(' ');
        if (((ClassicRuleSet)this.ruleSet).enPassant != null) {
            fenString.append(((ClassicRuleSet)this.ruleSet).enPassant.getSquareLabel());
        } else {
            fenString.append('-');
        }
        fenString.append(' ');
        fenString.append(((ClassicRuleSet)this.ruleSet).fiftyMoveCounter);
        fenString.append(' ');
        fenString.append(this.ruleSet.moveNumber);
        return fenString.toString();
    }

    public ArrayList<ChessModel> getPossibleSquareModels() {
        return this.board3dManager.getSquareModelsFromPossibleMoves(this.possiblesMoves);
    }

    public Square getSquareFromLocation(Location location) {
        for (Square square : this.board.getSquares()) {
            if (square.getLocation().equals(location)) {
                return square;
            }
        }
        return null;
    }

    public ArrayList<ChessModel> getBlackPieceModels() {
        return this.board3dManager.getBlackPieceModels();
    }

    public ArrayList<ChessModel> getWhitePieceModels() {
        return this.board3dManager.getWhitePieceModels();
    }

    public ChessColor getWinner() {
        return ((ClassicRuleSet)this.ruleSet).getWinner();
    }
}
