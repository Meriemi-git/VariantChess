package fr.aboucorp.variantchess.entities.moves.movesets;

import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.Location;
import fr.aboucorp.variantchess.entities.Piece;
import fr.aboucorp.variantchess.entities.Square;
import fr.aboucorp.variantchess.entities.boards.ClassicBoard;
import fr.aboucorp.variantchess.entities.enums.EventType;
import fr.aboucorp.variantchess.entities.enums.PieceId;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.events.GameEventSubscriber;
import fr.aboucorp.variantchess.entities.events.models.GameEvent;
import fr.aboucorp.variantchess.entities.events.models.PieceEvent;
import fr.aboucorp.variantchess.entities.moves.AbstractMoveSet;
import fr.aboucorp.variantchess.entities.utils.GameElementList;
import fr.aboucorp.variantchess.entities.utils.SquareList;

public class KingMoveSet extends AbstractMoveSet implements GameEventSubscriber {


    private boolean canCastleKingSide;
    private boolean canCastleQueenSide;

    public KingMoveSet(Piece thisPiece, ClassicBoard classicBoard, GameEventManager gameEventManager) {
        super(thisPiece, classicBoard, gameEventManager);
    }

    @Override
    public void receiveGameEvent(GameEvent event) {
        super.receiveGameEvent(event);
        if (event instanceof PieceEvent && this.piece.getChessColor() == PieceId.getColor(((PieceEvent) event).played)) {
            this.canCastleQueenSide = ((PieceEvent) event).boardEventType == EventType.CASTLE_QUEEN;
            this.canCastleKingSide = ((PieceEvent) event).boardEventType == EventType.CASTLE_KING;
        }
    }

    @Override
    protected SquareList getPossibleMoves(Piece piece, ChessColor turnColor) {
        SquareList validSquares = this.getClassicMoves(piece, turnColor);
        if (piece.getChessColor() == turnColor) {
            if (turnColor == ChessColor.WHITE) {
                Square c1 = this.classicBoard.getSquares().getSquareByLabel("C1");
                Square g1 = this.classicBoard.getSquares().getSquareByLabel("G1");
                if (this.canCastleQueenSide && !this.isChecking && this.isLocationSafe(c1, turnColor)) {
                    validSquares.add(c1);
                }
                if (this.canCastleKingSide && !this.isChecking && this.isLocationSafe(g1, turnColor)) {
                    validSquares.add(g1);
                }
            } else {
                Square c8 = this.classicBoard.getSquares().getSquareByLabel("C8");
                Square g8 = this.classicBoard.getSquares().getSquareByLabel("G8");
                if (this.canCastleQueenSide && this.isLocationSafe(c8, turnColor)) {
                    validSquares.add(c8);
                }
                if (this.canCastleKingSide && this.isLocationSafe(g8, turnColor)) {
                    validSquares.add(g8);
                }
            }
        }
        return validSquares;
    }

    @Override
    public SquareList getThreats(Piece piece, ChessColor turnColor) {
        return this.getClassicMoves(piece, turnColor);
    }

    @Override
    public void clear() {
        super.clear();
        this.canCastleKingSide = false;
        this.canCastleQueenSide = false;
    }

    private SquareList getClassicMoves(Piece piece, ChessColor turnColor) {
        SquareList allSquares = this.classicBoard.getSquares();
        SquareList validSquares = new SquareList();
        Location start = piece.getLocation();
        Square up = (Square) allSquares.getItemByLocation(new Location(start.getX() + 1, 0, start.getZ()));
        if (up != null
                && (up.getPiece() == null || up.getPiece().getChessColor() != turnColor)
                && (turnColor != piece.getChessColor() || this.isLocationSafe(up, turnColor))) {
            validSquares.add(up);
        }
        Square upRight = (Square) allSquares.getItemByLocation(new Location(start.getX() + 1, 0, start.getZ() - 1));
        if (upRight != null
                && (upRight.getPiece() == null || upRight.getPiece().getChessColor() != turnColor)
                && (turnColor != piece.getChessColor() || this.isLocationSafe(upRight, turnColor))) {
            validSquares.add(upRight);
        }
        Square upLeft = (Square) allSquares.getItemByLocation(new Location(start.getX() + 1, 0, start.getZ() + 1));
        if (upLeft != null
                && (upLeft.getPiece() == null || upLeft.getPiece().getChessColor() != turnColor)
                && (turnColor != piece.getChessColor() || this.isLocationSafe(upLeft, turnColor))) {
            validSquares.add(upLeft);
        }
        Square down = (Square) allSquares.getItemByLocation(new Location(start.getX() - 1, 0, start.getZ()));
        if (down != null
                && (down.getPiece() == null || down.getPiece().getChessColor() != turnColor)
                && (turnColor != piece.getChessColor() || this.isLocationSafe(down, turnColor))) {
            validSquares.add(down);
        }
        Square downLeft = (Square) allSquares.getItemByLocation(new Location(start.getX() - 1, 0, start.getZ() + 1));
        if (downLeft != null
                && (downLeft.getPiece() == null || downLeft.getPiece().getChessColor() != turnColor)
                && (turnColor != piece.getChessColor() || this.isLocationSafe(downLeft, turnColor))) {
            validSquares.add(downLeft);
        }
        Square downRight = (Square) allSquares.getItemByLocation(new Location(start.getX() - 1, 0, start.getZ() - 1));
        if (downRight != null
                && (downRight.getPiece() == null || downRight.getPiece().getChessColor() != turnColor)
                && (turnColor != piece.getChessColor() || this.isLocationSafe(downRight, turnColor))) {
            validSquares.add(downRight);
        }
        Square left = (Square) allSquares.getItemByLocation(new Location(start.getX(), 0, start.getZ() + 1));
        if (left != null
                && (left.getPiece() == null || left.getPiece().getChessColor() != turnColor)
                && (turnColor != piece.getChessColor() || this.isLocationSafe(left, turnColor))) {
            validSquares.add(left);
        }
        Square right = (Square) allSquares.getItemByLocation(new Location(start.getX(), 0, start.getZ() - 1));
        if (right != null
                && (right.getPiece() == null || right.getPiece().getChessColor() != turnColor)
                && (turnColor != piece.getChessColor() || this.isLocationSafe(right, turnColor))) {
            validSquares.add(right);
        }
        return validSquares;
    }

    private boolean isLocationSafe(Square square, ChessColor turnColor) {
        GameElementList<Piece> pieces;
        if (turnColor == ChessColor.BLACK) {
            pieces = this.classicBoard.getWhitePieces();
        } else {
            pieces = this.classicBoard.getBlackPieces();
        }
        for (Piece piece : pieces) {
            GameElementList<Square> squares = piece.getMoveSet().getThreats(piece, turnColor);
            if (squares != null && squares.getItemByLocation(square.getLocation()) != null) {
                return false;
            }
        }
        return true;
    }
}
