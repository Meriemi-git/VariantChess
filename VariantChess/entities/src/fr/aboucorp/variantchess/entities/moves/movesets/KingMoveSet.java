package fr.aboucorp.variantchess.entities.moves.movesets;

import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.Location;
import fr.aboucorp.variantchess.entities.Piece;
import fr.aboucorp.variantchess.entities.Square;
import fr.aboucorp.variantchess.entities.boards.ClassicBoard;
import fr.aboucorp.variantchess.entities.enums.BoardEventType;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.events.GameEventSubscriber;
import fr.aboucorp.variantchess.entities.events.models.GameEvent;
import fr.aboucorp.variantchess.entities.events.models.LogEvent;
import fr.aboucorp.variantchess.entities.events.models.PieceEvent;
import fr.aboucorp.variantchess.entities.moves.AbstractMoveSet;
import fr.aboucorp.variantchess.entities.utils.ChessList;
import fr.aboucorp.variantchess.entities.utils.SquareList;

public class KingMoveSet extends AbstractMoveSet implements GameEventSubscriber {

    private GameEventManager eventManager;

    private boolean canCastleKingSide;
    private boolean canCastleQueenSide;

    public KingMoveSet(Piece thisPiece, ClassicBoard classicBoard) {
        super(thisPiece, classicBoard);
        this.eventManager = GameEventManager.getINSTANCE();
    }

    @Override
    protected SquareList getPossibleMoves(Piece piece, ChessColor turnColor) {
        SquareList validSquares = getClassicMoves(piece,turnColor);
        if(piece.getChessColor() == turnColor) {
            if (turnColor == ChessColor.WHITE) {
                Square c1 = classicBoard.getSquares().getSquareByLabel("C1");
                Square g1 = classicBoard.getSquares().getSquareByLabel("G1");
                this.eventManager.sendMessage(new LogEvent(String.format("GetPossibleMoves %s => canCastleQueenSide : %s ; canCastleKingSide : %s",this.actualTurn.getTurnColor(),canCastleQueenSide,canCastleKingSide)));
                if (canCastleQueenSide && !this.isChecking && this.isLocationSafe(c1,turnColor)) {
                    validSquares.add(c1);
                }
                if (canCastleKingSide && !this.isChecking && isLocationSafe(g1,turnColor)) {
                    validSquares.add(g1);
                }
            } else {
                Square c8 = classicBoard.getSquares().getSquareByLabel("C8");
                Square g8 = classicBoard.getSquares().getSquareByLabel("G8");
                if (canCastleQueenSide && isLocationSafe(c8,turnColor)) {
                    validSquares.add(c8);
                }
                if (canCastleKingSide && isLocationSafe(g8,turnColor)) {
                    validSquares.add(g8);
                }
            }
        }
        return validSquares;
    }

    @Override
    public SquareList getThreats(Piece piece, ChessColor turnColor) {
        return getClassicMoves(piece,turnColor);
    }

    @Override
    public void receiveGameEvent(GameEvent event) {
        super.receiveGameEvent(event);
        if(event instanceof PieceEvent && this.piece.getChessColor() == ((PieceEvent) event).piece.getChessColor()){
            this.canCastleQueenSide = ((PieceEvent) event).type == BoardEventType.CASTLE_QUEEN;
            this.canCastleKingSide = ((PieceEvent) event).type == BoardEventType.CASTLE_KING;
            String logMessage = String.format("Piece event type : %s ;Turn color : %s ;canCastleQueenSide : %s ; canCastleKingSide : %s",((PieceEvent) event).type.name(), this.actualTurn.getTurnColor().name(),canCastleQueenSide,canCastleKingSide);
            this.eventManager.sendMessage(new LogEvent(logMessage));
        }
    }



    private boolean isLocationSafe(Square square, ChessColor turnColor) {
        ChessList<Piece> pieces;
        if(turnColor == ChessColor.BLACK) {
            pieces = classicBoard.getWhitePieces();
        }else {
            pieces = classicBoard.getBlackPieces();
        }
        for (Piece piece: pieces) {
            ChessList<Square> squares = piece.getMoveSet().getThreats(piece,turnColor);
            if(squares != null && squares.getItemByLocation(square.getLocation()) != null){
                return false;
            }
        }
        return true;
    }

    public SquareList getClassicMoves(Piece piece, ChessColor turnColor){
        SquareList allSquares = classicBoard.getSquares();
        SquareList validSquares = new SquareList();
        Location start = piece.getLocation();
        Square up = (Square) allSquares.getItemByLocation(new Location(start.getX()+1,0,start.getZ()));
        if(up != null
                && (up.getPiece() == null || up.getPiece().getChessColor() != turnColor)
                && (turnColor != piece.getChessColor() || isLocationSafe(up,turnColor))){
            validSquares.add(up);
        }
        Square upRight = (Square) allSquares.getItemByLocation(new Location(start.getX()+1,0,start.getZ()-1));
        if(upRight != null
                && (upRight.getPiece() == null || upRight.getPiece().getChessColor() != turnColor)
                && (turnColor != piece.getChessColor() || isLocationSafe(upRight,turnColor))){
            validSquares.add(upRight);
        }
        Square upLeft = (Square) allSquares.getItemByLocation(new Location(start.getX()+1,0,start.getZ()+1));
        if(upLeft != null
                && (upLeft.getPiece() == null || upLeft.getPiece().getChessColor() != turnColor)
                && (turnColor != piece.getChessColor() || isLocationSafe(upLeft,turnColor))){
            validSquares.add(upLeft);
        }
        Square down = (Square) allSquares.getItemByLocation(new Location(start.getX()-1,0,start.getZ()));
        if(down != null
                && (down.getPiece() == null || down.getPiece().getChessColor() != turnColor)
                && (turnColor != piece.getChessColor() || isLocationSafe(down,turnColor))){
            validSquares.add(down);
        }
        Square downLeft = (Square) allSquares.getItemByLocation(new Location(start.getX()-1,0,start.getZ()+1));
        if(downLeft != null
                && (downLeft.getPiece() == null || downLeft.getPiece().getChessColor() != turnColor)
                && (turnColor != piece.getChessColor() || isLocationSafe(downLeft,turnColor))) {
            validSquares.add(downLeft);
        }
        Square downRight = (Square) allSquares.getItemByLocation(new Location(start.getX()-1,0,start.getZ()-1));
        if(downRight != null
                && (downRight.getPiece() == null || downRight.getPiece().getChessColor() != turnColor)
                && (turnColor != piece.getChessColor() || isLocationSafe(downRight,turnColor))){
            validSquares.add(downRight);
        }
        Square left = (Square) allSquares.getItemByLocation(new Location(start.getX(),0,start.getZ()+1));
        if(left != null
                && (left.getPiece() == null || left.getPiece().getChessColor() != turnColor)
                && (turnColor != piece.getChessColor() || isLocationSafe(left,turnColor))){
            validSquares.add(left);
        }
        Square right = (Square) allSquares.getItemByLocation(new Location(start.getX(),0,start.getZ()-1));
        if(right != null
                && (right.getPiece() == null || right.getPiece().getChessColor() != turnColor)
                && (turnColor != piece.getChessColor() || isLocationSafe(right,turnColor))){
            validSquares.add(right);
        }
        return validSquares;
    }

    @Override
    public void clear() {
        super.clear();
        canCastleKingSide = false;
        canCastleQueenSide = false;
    }
}
