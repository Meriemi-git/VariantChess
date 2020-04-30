package fr.aboucorp.teamchess.entities.model.rules;

import java.util.List;

import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.Square;
import fr.aboucorp.teamchess.entities.model.Turn;
import fr.aboucorp.teamchess.entities.model.boards.ClassicBoard;
import fr.aboucorp.teamchess.entities.model.enums.BoardEventType;
import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.entities.model.events.GameEventManager;
import fr.aboucorp.teamchess.entities.model.events.GameEventSubscriber;
import fr.aboucorp.teamchess.entities.model.events.models.BoardEvent;
import fr.aboucorp.teamchess.entities.model.events.models.CastlingEvent;
import fr.aboucorp.teamchess.entities.model.events.models.CheckInEvent;
import fr.aboucorp.teamchess.entities.model.events.models.CheckOutEvent;
import fr.aboucorp.teamchess.entities.model.events.models.EnPassantEvent;
import fr.aboucorp.teamchess.entities.model.events.models.GameEvent;
import fr.aboucorp.teamchess.entities.model.events.models.PartyEvent;
import fr.aboucorp.teamchess.entities.model.events.models.PieceEvent;
import fr.aboucorp.teamchess.entities.model.events.models.TurnEndEvent;
import fr.aboucorp.teamchess.entities.model.events.models.TurnEvent;
import fr.aboucorp.teamchess.entities.model.events.models.TurnStartEvent;
import fr.aboucorp.teamchess.entities.model.utils.PieceList;

public class ClassicRuleSet extends AbstracRuleSet implements GameEventSubscriber {

    private final ClassicBoard classicBoard;
    public static int FIFTY_MOVE_RULE_NUMBER = 75;
    public int fiftyMoveCounter = 0;
    public boolean whiteCanCastleKing;
    public boolean whiteCanCastleQueen;
    public boolean blackCanCastleKing;
    public boolean blackCanCastleQueen;
    private boolean kingIsInCheck;
    public Square enPassant;
    private Turn previousTurn;
    private Turn actualTurn;

    private GameEventManager eventManager;

    public ClassicRuleSet(ClassicBoard classicBoard) {
        this.classicBoard = classicBoard;
        this.eventManager = GameEventManager.getINSTANCE();
        this.eventManager.subscribe(TurnEvent.class,this,1);
    }

    public void isKingInCheck(Piece piece) {
        if (kingIsInCheck) {
            this.eventManager.sendMessage(new CheckOutEvent("King out of check", BoardEventType.CHECK_OUT, piece));
            this.kingIsInCheck = false;
        }
        List<Piece> causingCheck = piece.getMoveSet().moveCauseCheck(actualTurn.getTurnColor());
        if (causingCheck.size() > 0) {
            this.kingIsInCheck = true;
            Piece kingInCheck = this.previousTurn.getTurnColor() == ChessColor.WHITE
                    ? this.classicBoard.getWhitePieces().getPieceById(PieceId.WK)
                    : this.classicBoard.getBlackPieces().getPieceById(PieceId.BK);
            this.eventManager.sendMessage(new CheckInEvent("King is in check", BoardEventType.CHECK_IN, kingInCheck, causingCheck));
        }
    }

    public boolean isEnPassantMove(Piece piece, Square destination) {
        return this.enPassant != null
                && destination.getPiece() == null
                && piece.getLocation().getX() != destination.getLocation().getX();
    }

    public void checkIfCastling(Square square) {
        ChessColor turnColor = this.actualTurn.getTurnColor();
        Piece rookToMove = null;
        Square destination = null;
        BoardEventType castlingType = null;
        if (this.whiteCanCastleQueen && turnColor == ChessColor.WHITE && square.getSquareLabel().equals("C1")) {
            rookToMove = this.classicBoard.getWhitePieces().getPieceById(PieceId.WLR);
            destination = this.classicBoard.getSquares().getSquareByLabel("D1");
            castlingType = BoardEventType.CASTLE_QUEEN;
        }
        if (this.blackCanCastleQueen && turnColor == ChessColor.BLACK && square.getSquareLabel().equals("C8")) {
            rookToMove = this.classicBoard.getBlackPieces().getPieceById(PieceId.BLR);
            destination = this.classicBoard.getSquares().getSquareByLabel("D8");
            castlingType = BoardEventType.CASTLE_QUEEN;
        }
        if (this.whiteCanCastleKing && turnColor == ChessColor.WHITE && square.getSquareLabel().equals("G1")) {
            rookToMove = this.classicBoard.getWhitePieces().getPieceById(PieceId.WRR);
            destination = this.classicBoard.getSquares().getSquareByLabel("F1");
            castlingType = BoardEventType.CASTLE_KING;
        }
        if (this.blackCanCastleKing && turnColor == ChessColor.BLACK && square.getSquareLabel().equals("G8")) {
            rookToMove = this.classicBoard.getBlackPieces().getPieceById(PieceId.BRR);
            destination = this.classicBoard.getSquares().getSquareByLabel("F8");
            castlingType = BoardEventType.CASTLE_KING;
        }
        if (rookToMove != null && destination != null) {
            this.eventManager.sendMessage(new CastlingEvent("Castling",castlingType,rookToMove,destination));
        }
    }

    public void canCastle() {
        if (whiteCanCastleKingNow()) {
            this.whiteCanCastleKing = true;
            this.eventManager.sendMessage(new PieceEvent("White can castle on king side", BoardEventType.CASTLE_KING, this.classicBoard.getWhitePieces().getPieceById(PieceId.WK)));
        } else {
            this.whiteCanCastleKing = false;
        }
        if (whiteCanCastleQueenNow()) {
            this.whiteCanCastleQueen = true;
            this.eventManager.sendMessage(new PieceEvent("White can castle on queen side", BoardEventType.CASTLE_QUEEN, this.classicBoard.getWhitePieces().getPieceById(PieceId.WK)));
        } else {
            this.whiteCanCastleQueen = false;
        }
        if (blackCanCastleKingNow()) {
            this.blackCanCastleKing = true;
            this.eventManager.sendMessage(new PieceEvent("Black can castle on king side", BoardEventType.CASTLE_KING, this.classicBoard.getBlackPieces().getPieceById(PieceId.BK)));
        } else {
            this.blackCanCastleKing = false;
        }
        if (blackCanCastleQueenNow()) {
            this.blackCanCastleQueen = true;
            this.eventManager.sendMessage(new PieceEvent("Black can castle on queen side", BoardEventType.CASTLE_QUEEN, this.classicBoard.getBlackPieces().getPieceById(PieceId.BK)));
        } else {
            this.blackCanCastleQueen = false;
        }
    }

    private boolean blackCanCastleQueenNow() {
        return this.blackCanCastleQueen()
                && this.classicBoard.getSquares().getSquareByLabel("D8").getPiece() == null
                && this.classicBoard.getSquares().getSquareByLabel("C8").getPiece() == null;
    }

    public boolean blackCanCastleQueen() {
        return this.classicBoard.getBlackPieces().getPieceById(PieceId.BK).isFirstMove()
                && this.classicBoard.getBlackPieces().getPieceById(PieceId.BLR).isFirstMove();
    }

    private boolean blackCanCastleKingNow() {
        return this.blackCanCastleKing()
                && this.classicBoard.getSquares().getSquareByLabel("F8").getPiece() == null
                && this.classicBoard.getSquares().getSquareByLabel("G8").getPiece() == null;
    }

    public boolean blackCanCastleKing() {
        return this.classicBoard.getBlackPieces().getPieceById(PieceId.BK).isFirstMove()
                && this.classicBoard.getBlackPieces().getPieceById(PieceId.BLR).isFirstMove();
    }

    private boolean whiteCanCastleQueenNow() {
        return whiteCanCastleQueen()
                && this.classicBoard.getSquares().getSquareByLabel("D1").getPiece() == null
                && this.classicBoard.getSquares().getSquareByLabel("C1").getPiece() == null
                && this.classicBoard.getSquares().getSquareByLabel("CB1").getPiece() == null;
    }

    public boolean whiteCanCastleQueen() {
        return this.classicBoard.getWhitePieces().getPieceById(PieceId.WK).isFirstMove()
                && this.classicBoard.getWhitePieces().getPieceById(PieceId.WLR).isFirstMove() ;
    }

    private boolean whiteCanCastleKingNow() {
        return whiteCanCastleKing()
                && this.classicBoard.getSquares().getSquareByLabel("G1").getPiece() == null
                && this.classicBoard.getSquares().getSquareByLabel("F1").getPiece() == null;
    }

    public boolean whiteCanCastleKing() {
        return this.classicBoard.getWhitePieces().getPieceById(PieceId.WK).isFirstMove()
                && this.classicBoard.getWhitePieces().getPieceById(PieceId.WRR).isFirstMove();

    }

    public void canClaimADraw() {
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

    public void isGameFinished() {
        if (previousTurn != null) {
            boolean cantMove = true;
            for (Piece piece : classicBoard.getPiecesByColor(this.actualTurn.getTurnColor())) {
                if (piece.getMoveSet().getNextMoves().size() > 0) {
                    cantMove = false;
                }
            }
            if (cantMove) {
                if (this.kingIsInCheck) {
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
            opposites = this.classicBoard.getBlackPieces();
        } else {
            opposites = this.classicBoard.getWhitePieces();
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
        if (event instanceof EnPassantEvent) {
            if (((EnPassantEvent) event).type == BoardEventType.EN_PASSANT) {
                this.enPassant = ((EnPassantEvent) event).destination;
            }
        }else if(event instanceof TurnStartEvent){
            if(previousTurn != null) {
                this.canClaimADraw();
                this.canCastle();
                isGameFinished();
            }
            this.previousTurn = actualTurn;
            this.actualTurn = ((TurnStartEvent) event).turn;
            if (((TurnStartEvent) event).turn.getTurnColor() == ChessColor.WHITE) {
                this.moveNumber++;
            }
        }else if(event instanceof TurnEndEvent){
            this.enPassant = null;
        }
    }

}
