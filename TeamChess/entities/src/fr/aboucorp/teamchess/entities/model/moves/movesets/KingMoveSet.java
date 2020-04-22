package fr.aboucorp.teamchess.entities.model.moves.movesets;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessCell;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.ChessPiece;
import fr.aboucorp.teamchess.entities.model.Location;
import fr.aboucorp.teamchess.entities.model.enums.PieceEventType;
import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.entities.model.events.GameEvent;
import fr.aboucorp.teamchess.entities.model.events.GameEventManager;
import fr.aboucorp.teamchess.entities.model.events.GameEventSubscriber;
import fr.aboucorp.teamchess.entities.model.events.PieceEvent;
import fr.aboucorp.teamchess.entities.model.utils.ChessCellList;
import fr.aboucorp.teamchess.entities.model.utils.ChessList;

public class KingMoveSet extends AbstractMoveSet implements GameEventSubscriber {

    private GameEventManager eventManager;

    public KingMoveSet() {
        this.eventManager =  GameEventManager.getINSTANCE();
    }

    @Override
    public ChessCellList getPossibleMoves(ChessPiece piece, Board board, ChessColor turnColor) {
        ChessCellList validCells = getClassicMoves(piece, board,turnColor);
        if(piece.getChessColor() == turnColor) {
            if (turnColor == ChessColor.WHITE) {
                if (board.getWhitePieces().getPieceById(PieceId.WK).isFirstMove()
                        && board.getWhitePieces().getPieceById(PieceId.WLR).isFirstMove()
                        && isPossibleBigCastling( board,turnColor)) {
                    validCells.add(board.getChessCells().getChessCellByLabel("C1"));
                    this.eventManager.sendMessage(new PieceEvent("Waiting for littleCastle", PieceEventType.BIG_CASTLING,piece));
                }
                if (board.getWhitePieces().getPieceById(PieceId.WK).isFirstMove()
                        && board.getWhitePieces().getPieceById(PieceId.WRR).isFirstMove()
                        && isPossibleLittleCastling( board,turnColor)) {
                    validCells.add(board.getChessCells().getChessCellByLabel("G1"));
                    this.eventManager.sendMessage(new PieceEvent("Waiting for bigCastle", PieceEventType.LITTLE_CASTLING,piece));

                }
            } else {
                if (board.getBlackPieces().getPieceById(PieceId.BK).isFirstMove()
                        && board.getBlackPieces().getPieceById(PieceId.BRR).isFirstMove()
                        && isPossibleBigCastling( board,turnColor)) {
                    validCells.add( board.getChessCells().getChessCellByLabel("C8"));
                    this.eventManager.sendMessage(new PieceEvent("Waiting for littleCastle", PieceEventType.BIG_CASTLING,piece));
                }
                if (board.getBlackPieces().getPieceById(PieceId.BK).isFirstMove()
                        && board.getBlackPieces().getPieceById(PieceId.BLR).isFirstMove()
                        && isPossibleLittleCastling( board,turnColor)) {
                    validCells.add( board.getChessCells().getChessCellByLabel("G8"));
                    this.eventManager.sendMessage(new PieceEvent("Waiting for littleCastle", PieceEventType.LITTLE_CASTLING,piece));
                }
            }
        }
        return validCells;
    }

    @Override
    public ChessCellList getThreats(ChessPiece piece, Board board, ChessColor turnColor) {
        return getClassicMoves(piece, board,turnColor);
    }

    private boolean isPossibleBigCastling(Board board, ChessColor turnColor) {
        if(turnColor == ChessColor.WHITE){
            ChessCell b1 = board.getChessCells().getChessCellByLabel("B1");
            ChessCell c1 = board.getChessCells().getChessCellByLabel("C1");
            ChessCell d1 = board.getChessCells().getChessCellByLabel("D1");
            return  b1.getPiece() == null
                    && c1.getPiece() == null
                    && d1.getPiece() == null
                    && isLocationSafe(b1,board,turnColor)
                    && isLocationSafe(c1,board,turnColor)
                    && isLocationSafe(d1,board,turnColor);
        }else{
            ChessCell b8 = board.getChessCells().getChessCellByLabel("B8");
            ChessCell c8 = board.getChessCells().getChessCellByLabel("C8");
            ChessCell d8 = board.getChessCells().getChessCellByLabel("D8");
            return b8.getPiece() == null
                    && c8.getPiece() == null
                    && d8.getPiece() == null
                    && isLocationSafe(b8,board,turnColor)
                    && isLocationSafe(c8,board,turnColor)
                    && isLocationSafe(d8,board,turnColor);
        }
    }

    private boolean isPossibleLittleCastling(Board board, ChessColor turnColor) {
        if(turnColor == ChessColor.WHITE){
            ChessCell f1 = board.getChessCells().getChessCellByLabel("F1");
            ChessCell g1 = board.getChessCells().getChessCellByLabel("G1");
            return f1.getPiece() == null
                    && g1.getPiece() == null
                    && isLocationSafe(f1,board,turnColor)
                    && isLocationSafe(g1,board,turnColor);
        }else{
            ChessCell f8 = board.getChessCells().getChessCellByLabel("F8");
            ChessCell g8 = board.getChessCells().getChessCellByLabel("G8");
            return board.isCellFree(f8)
                    && board.isCellFree(g8)
                    && isLocationSafe(f8,board,turnColor)
                    && isLocationSafe(g8,board,turnColor);
        }
    }

    private boolean isLocationSafe(ChessCell cell,Board board,ChessColor turnColor) {
        ChessList<ChessPiece> pieces;
        if(turnColor == ChessColor.BLACK) {
            pieces = board.getWhitePieces();
        }else {
            pieces = board.getBlackPieces();
        }
        for (ChessPiece piece: pieces) {
            ChessList<ChessCell> cells = piece.getMoveSet().getThreats(piece,board,turnColor);
            if(cells != null && cells.getItemByLocation(cell.getLocation()) != null){
                return false;
            }
        }
        return true;
    }

    public  ChessCellList getClassicMoves(ChessPiece piece, Board board,ChessColor turnColor){
        ChessCellList allCells = board.getChessCells();
        ChessCellList validCells = new ChessCellList();
        Location start = piece.getLocation();
        ChessCell up = (ChessCell) allCells.getItemByLocation(new Location(start.getX()+1,0,start.getZ()));
        if(up != null
                && (up.getPiece() == null || up.getPiece().getChessColor() != turnColor)
                && (turnColor != piece.getChessColor() || isLocationSafe(up,board,turnColor))){
            validCells.add(up);
        }
        ChessCell  upRight = (ChessCell) allCells.getItemByLocation(new Location(start.getX()+1,0,start.getZ()-1));
        if(upRight != null
                && (upRight.getPiece() == null || upRight.getPiece().getChessColor() != turnColor)
                && (turnColor != piece.getChessColor() || isLocationSafe(upRight,board,turnColor))){
            validCells.add(upRight);
        }
        ChessCell  upLeft = (ChessCell) allCells.getItemByLocation(new Location(start.getX()+1,0,start.getZ()+1));
        if(upLeft != null
                && (upLeft.getPiece() == null || upLeft.getPiece().getChessColor() != turnColor)
                && (turnColor != piece.getChessColor() || isLocationSafe(upLeft,board,turnColor))){
            validCells.add(upLeft);
        }
        ChessCell down = (ChessCell) allCells.getItemByLocation(new Location(start.getX()-1,0,start.getZ()));
        if(down != null
                && (down.getPiece() == null || down.getPiece().getChessColor() != turnColor)
                && (turnColor != piece.getChessColor() || isLocationSafe(down,board,turnColor))){
            validCells.add(down);
        }
        ChessCell  downLeft = (ChessCell) allCells.getItemByLocation(new Location(start.getX()-1,0,start.getZ()+1));
        if(downLeft != null
                && (downLeft.getPiece() == null || downLeft.getPiece().getChessColor() != turnColor)
                && (turnColor != piece.getChessColor() || isLocationSafe(downLeft,board,turnColor))) {
            validCells.add(downLeft);
        }
        ChessCell  downRight = (ChessCell) allCells.getItemByLocation(new Location(start.getX()-1,0,start.getZ()-1));
        if(downRight != null
                && (downRight.getPiece() == null || downRight.getPiece().getChessColor() != turnColor)
                && (turnColor != piece.getChessColor() || isLocationSafe(downRight,board,turnColor))){
            validCells.add(downRight);
        }
        ChessCell  left = (ChessCell) allCells.getItemByLocation(new Location(start.getX(),0,start.getZ()+1));
        if(left != null
                && (left.getPiece() == null || left.getPiece().getChessColor() != turnColor)
                && (turnColor != piece.getChessColor() || isLocationSafe(left,board,turnColor))){
            validCells.add(left);
        }
        ChessCell  right = (ChessCell) allCells.getItemByLocation(new Location(start.getX(),0,start.getZ()-1));
        if(right != null
                && (right.getPiece() == null || right.getPiece().getChessColor() != turnColor)
                && (turnColor != piece.getChessColor() || isLocationSafe(right,board,turnColor))){
            validCells.add(right);
        }
        return validCells;
    }

    @Override
    public void receiveGameEvent(GameEvent event) {

    }
}
