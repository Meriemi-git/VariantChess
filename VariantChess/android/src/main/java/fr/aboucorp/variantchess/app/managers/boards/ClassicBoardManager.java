package fr.aboucorp.variantchess.app.managers.boards;

import fr.aboucorp.variantchess.app.utils.GdxPostRunner;
import fr.aboucorp.variantchess.app.utils.state.ClassicBoardStateBuilder;
import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.ChessMatch;
import fr.aboucorp.variantchess.entities.Location;
import fr.aboucorp.variantchess.entities.Piece;
import fr.aboucorp.variantchess.entities.Square;
import fr.aboucorp.variantchess.entities.Turn;
import fr.aboucorp.variantchess.entities.boards.Board;
import fr.aboucorp.variantchess.entities.enums.EventType;
import fr.aboucorp.variantchess.entities.enums.GameState;
import fr.aboucorp.variantchess.entities.enums.PieceId;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.events.GameEventSubscriber;
import fr.aboucorp.variantchess.entities.events.models.CastlingEvent;
import fr.aboucorp.variantchess.entities.events.models.EnPassantEvent;
import fr.aboucorp.variantchess.entities.events.models.GameEvent;
import fr.aboucorp.variantchess.entities.events.models.MoveEvent;
import fr.aboucorp.variantchess.entities.events.models.PartyEvent;
import fr.aboucorp.variantchess.entities.events.models.PieceEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnEndEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnStartEvent;
import fr.aboucorp.variantchess.entities.exceptions.FenStringBadFormatException;
import fr.aboucorp.variantchess.entities.rules.ClassicRuleSet;
import fr.aboucorp.variantchess.entities.utils.SquareList;
import fr.aboucorp.variantchess.libgdx.Board3dManager;
import fr.aboucorp.variantchess.libgdx.utils.GraphicGameArray;

public class ClassicBoardManager extends BoardManager implements GameEventSubscriber {

    public ClassicBoardManager(Board3dManager board3dManager, Board board, ClassicRuleSet ruleSet, GameEventManager gameEventManager, ClassicBoardStateBuilder classicFenBuilder) {
        super(board, board3dManager, ruleSet, gameEventManager, classicFenBuilder);
        this.gameEventManager.subscribe(EnPassantEvent.class, this, 1);
    }

    @Override
    public void receiveEvent(GameEvent event) {
        if (event instanceof TurnStartEvent) {
            this.manageTurnStart((TurnStartEvent) event);
        } else if (event instanceof TurnEndEvent) {
            this.manageTurnEnd();
        } else if (event instanceof CastlingEvent) {
            Piece piece = this.board.getPieceById(((CastlingEvent) event).played);
            piece.move(((CastlingEvent) event).detination);
            this.board3dManager.moveToSquare(piece, ((CastlingEvent) event).detination);
        }
    }

    @Override
    public void startParty(ChessMatch chessMatch) {
        this.gameEventManager.subscribe(PartyEvent.class, this, 1);
        this.gameEventManager.subscribe(TurnEvent.class, this, 1);
        this.gameEventManager.subscribe(PieceEvent.class, this, 1);
        if (chessMatch.turns.size() == 0) {
            this.board.initBoard();
        } else {
            try {
                this.board.loadBoard(((Turn) chessMatch.turns.getLast()).getFen());
            } catch (FenStringBadFormatException e) {
                e.printStackTrace();
            }
        }
        GdxPostRunner postRunner = new GdxPostRunner() {
            @Override
            public void execute() {
                ClassicBoardManager.this.board3dManager.createSquares(ClassicBoardManager.this.board.getSquares());
                ClassicBoardManager.this.board3dManager.createPieces(ClassicBoardManager.this.board.getWhitePieces());
                ClassicBoardManager.this.board3dManager.createPieces(ClassicBoardManager.this.board.getBlackPieces());
                ClassicBoardManager.this.boardLoadingListener.OnBoardLoaded();
            }
        };
        postRunner.startAsync();
    }

    @Override
    public void stopParty() {
        this.gameState = GameState.PIECE_SELECTION;
        this.selectedPiece = null;
        this.previousTurn = null;
        this.actualTurn = null;
        this.board.clearBoard();
        this.ruleSet.moveNumber = 0;
    }

    @Override
    public GraphicGameArray getPossibleSquareModels() {
        return this.board3dManager.getSquareModelsFromPossibleMoves(this.possiblesMoves);
    }

    @Override
    public Square getSquareFromLocation(Location location) {
        for (Square square : this.board.getSquares()) {
            if (square.getLocation().equals(location)) {
                return square;
            }
        }
        return null;
    }

    @Override
    public ChessColor getWinner() {
        return ((ClassicRuleSet) this.ruleSet).getWinner();
    }

    @Override
    public ChessColor loadBoard(String fenString) throws FenStringBadFormatException, NumberFormatException {
        String[] parts = fenString.trim().split(" ");
        if (parts.length < 5 || parts.length > 6) {
            throw new FenStringBadFormatException("Cannot load game from fen string, fen string doesn't contains enought parts");
        }
        GdxPostRunner runner = new GdxPostRunner() {
            @Override
            public void execute() {
                try {
                    ClassicBoardManager.this.stopParty();
                    ClassicBoardManager.this.board.loadBoard(parts[0]);
                    ClassicBoardManager.this.board3dManager.createPieces(ClassicBoardManager.this.board.getWhitePieces());
                    ClassicBoardManager.this.board3dManager.createPieces(ClassicBoardManager.this.board.getBlackPieces());
                } catch (FenStringBadFormatException e) {
                    e.printStackTrace();
                }
            }
        };
        runner.start();

        ((ClassicRuleSet) this.ruleSet).whiteCanCastleKing = parts[2].contains("K");
        ((ClassicRuleSet) this.ruleSet).whiteCanCastleQueen = parts[2].contains("Q");
        ((ClassicRuleSet) this.ruleSet).blackCanCastleKing = parts[2].contains("k");
        ((ClassicRuleSet) this.ruleSet).blackCanCastleQueen = parts[2].contains("q");
        if (!parts[3].equals("-")) {
            ((ClassicRuleSet) this.ruleSet).enPassant = this.board.getSquares().getSquareByLabel(parts[3]);
        }
        this.ruleSet.moveNumber = Integer.parseInt(parts[4]);
        ((ClassicRuleSet) this.ruleSet).fiftyMoveCounter = Integer.parseInt(parts[5]);
        if (parts[1].equals("w")) {
            return ChessColor.WHITE;
        } else {
            return ChessColor.BLACK;
        }
    }

    @Override
    protected void manageTurnEnd() {
        this.selectedPiece = null;
        this.possiblesMoves = null;
    }

    @Override
    protected void manageTurnStart(TurnStartEvent event) {
        if (this.actualTurn != null) {
            this.previousTurn = this.actualTurn;
        }
        this.actualTurn = event.turn;
/*        GdxPostRunner runner = new GdxPostRunner() {
            @Override
            public void execute() {
                ClassicBoardManager.this.board3dManager.moveCameraOnNewTurn(ClassicBoardManager.this.actualTurn.getTurnColor());
            }
        };
        runner.startAsync();*/
    }

    @Override
    public Piece moveToSquare(Square square) {
        Piece eated = this.eat(square);
        this.selectedPiece.move(square);
        GdxPostRunner runner = new GdxPostRunner() {
            @Override
            protected void execute() {
                ClassicBoardManager.this.board3dManager.moveSelected(square);
            }
        };
        runner.startAsync();
        ((ClassicRuleSet) this.ruleSet).isKingInCheck(this.selectedPiece);
        ((ClassicRuleSet) this.ruleSet).checkIfCastling(square);
        this.resetHighlited();
        return eated;
    }

    @Override
    public void selectPiece(Piece touched) {
        this.selectedPiece = touched;
        this.gameState = this.gameState == GameState.WAIT_FOR_NEXT_TURN ? GameState.WAIT_FOR_NEXT_TURN : GameState.SQUARE_SELECTION;
        this.board3dManager.selectPiece(touched);
        this.possiblesMoves = touched.getMoveSet().getNextMoves();
        this.hightLightPossibleMoves(this.possiblesMoves);
    }

    @Override
    public void selectSquare(Square to) {
        Square from = this.selectedPiece.getSquare();
        Piece deadPiece = this.moveToSquare(to);
        String message = String.format("Move %s from %s to %s", this.selectedPiece, from, to);
        this.gameEventManager.sendEvent(new MoveEvent(
                message
                , from.getLocation()
                , to.getLocation()
                , this.selectedPiece.getPieceId()
                , deadPiece != null ? deadPiece.getPieceId() : null));
    }

    @Override
    public void toogleTacticalView() {
        GdxPostRunner runner = new GdxPostRunner() {
            @Override
            public void execute() {
                ClassicBoardManager.this.board3dManager.toogleTacticalView();
            }
        };
        runner.startAsync();
    }

    @Override
    public void unHighlight() {
        super.unHighlight();
        this.board3dManager.resetSelection();
        if (this.possiblesMoves != null) {
            this.board3dManager.unHighlightSquares(this.possiblesMoves);
        }
    }

    @Override
    public void playTheOppositeMove(String fenState) {
        PieceId played = this.boardStateBuilder.getPiecePlayedFromState(fenState);
        Location to = this.boardStateBuilder.getTo(fenState);
        this.selectPiece(this.board.getPieceById(played));
        Square selectedSquare = (Square) this.board.getSquares().getItemByLocation(to);
        this.selectSquare(selectedSquare);
    }

    private void hightLightPossibleMoves(SquareList possibleMoves) {
        if (possibleMoves != null) {
            for (Square square : possibleMoves) {
                if (square.getPiece() == null) {
                    this.board3dManager.highlightEmpty(square);
                } else {
                    this.board3dManager.highlightOccupied(square);
                }
            }
        }
    }

    private Piece eat(Square square) {
        Piece toBeEaten = square.getPiece();
        if (((ClassicRuleSet) this.ruleSet).isEnPassantMove(this.selectedPiece, square)) {
            toBeEaten = this.board.getPieceById(this.previousTurn.getPlayed());
        }
        if (toBeEaten != null) {
            if (toBeEaten.getChessColor() == ChessColor.WHITE) {
                this.board.getWhitePieces().removeByLocation(toBeEaten.getLocation());
            } else {
                this.board.getBlackPieces().removeByLocation(toBeEaten.getLocation());
            }
            this.board3dManager.moveToEven(toBeEaten);
            String eventMessage = String.format("Piece %s die on %s", toBeEaten.getPieceId().name(), toBeEaten.getLocation());
            this.gameEventManager.sendEvent(new PieceEvent(eventMessage, EventType.DEATH, toBeEaten.getPieceId()));
            toBeEaten.die();
        }
        return toBeEaten;
    }

    private void resetHighlited() {
        GdxPostRunner runner = new GdxPostRunner() {
            @Override
            protected void execute() {
                ClassicBoardManager.this.board3dManager.unHighlightSquares(ClassicBoardManager.this.possiblesMoves);
                ClassicBoardManager.this.board3dManager.resetSelection();
            }
        };
        runner.startAsync();
        this.possiblesMoves = null;
    }


}
