package fr.aboucorp.teamchess.entities.model.moves.movesets;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessCell;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.ChessPiece;
import fr.aboucorp.teamchess.entities.model.Location;
import fr.aboucorp.teamchess.entities.model.enums.PieceEventType;
import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.entities.model.events.ChessEventManager;
import fr.aboucorp.teamchess.entities.model.events.GameEvent;
import fr.aboucorp.teamchess.entities.model.events.GameEventSubscriber;
import fr.aboucorp.teamchess.entities.model.events.PieceEvent;
import fr.aboucorp.teamchess.entities.model.utils.ChessList;

public class KingMoveSet extends AbstractMoveSet implements GameEventSubscriber {

    private ChessEventManager eventManager;
    private boolean kingFirstMove = true;
    private boolean rookLeftFirstMove = true;
    private boolean rookRightFirstMove = true;

    public KingMoveSet() {
        this.eventManager =  ChessEventManager.getINSTANCE();
        this.eventManager.subscribe(PieceEvent.class,this);
    }

    @Override
    public  ChessList<ChessCell> getMoves(ChessPiece piece, Board board) {
        ChessList<ChessCell> validCells = getClassicMoves(piece, board);
        if(piece.getChessColor() == ChessColor.WHITE) {
            if(kingFirstMove && rookLeftFirstMove && isPossibleLittleCastling(piece, board)) {
                Location g1 = new Location(1,0,0);
                validCells.add((ChessCell) board.getChessCells().getItemByLocation(g1));
            }
            if(kingFirstMove && rookRightFirstMove && isPossibleBigCastling(piece, board)) {
                Location c1 = new Location(5,0,0);
                validCells.add((ChessCell) board.getChessCells().getItemByLocation(c1));
            }
        }else {
            if(kingFirstMove && rookRightFirstMove && isPossibleLittleCastling(piece, board)) {
                Location g8 = new Location(1,0,7);
                validCells.add((ChessCell) board.getChessCells().getItemByLocation(g8));
            }
            if(kingFirstMove && rookLeftFirstMove && isPossibleBigCastling(piece, board)) {
                Location c8 = new Location(5,0,7);
                validCells.add((ChessCell) board.getChessCells().getItemByLocation(c8));
            }
        }
        return validCells;
    }

    private boolean isPossibleLittleCastling(ChessPiece piece, Board board) {
        if(piece.getChessColor() == ChessColor.WHITE){
            ChessCell b8 = new ChessCell(new Location(6,0,0),ChessColor.WHITE);
            ChessCell c8 = new ChessCell(new Location(5,0,0),ChessColor.WHITE);
            ChessCell d8 = new ChessCell(new Location(4,0,0),ChessColor.WHITE);
            return board.isCellFree(b8)
                    && board.isCellFree(c8)
                    && board.isCellFree(d8)
                    && isLocationSafe(b8,board,piece.getChessColor())
                    && isLocationSafe(c8,board,piece.getChessColor())
                    && isLocationSafe(d8,board,piece.getChessColor());
        }else{
            ChessCell b1 = new ChessCell(new Location(6,0,0),ChessColor.BLACK);
            ChessCell c1 = new ChessCell(new Location(5,0,0),ChessColor.BLACK);
            ChessCell d1 = new ChessCell(new Location(4,0,0),ChessColor.BLACK);
            return board.isCellFree(b1)
                    && board.isCellFree(c1)
                    && board.isCellFree(d1)
                    && isLocationSafe(b1,board,piece.getChessColor())
                    && isLocationSafe(c1,board,piece.getChessColor())
                    && isLocationSafe(d1,board,piece.getChessColor());
        }
    }


    private boolean isPossibleBigCastling(ChessPiece piece, Board board) {
        if(piece.getChessColor() == ChessColor.WHITE){
            ChessCell f8 = new ChessCell(new Location(2,0,0),ChessColor.WHITE);
            ChessCell g8 = new ChessCell(new Location(1,0,0),ChessColor.WHITE);
            return board.isCellFree(f8)
                    && board.isCellFree(g8)
                    && isLocationSafe(f8,board,piece.getChessColor())
                    && isLocationSafe(g8,board,piece.getChessColor());
        }else{
            ChessCell f1 = new ChessCell(new Location(2,0,7),ChessColor.BLACK);
            ChessCell g1 = new ChessCell(new Location(1,0,7),ChessColor.BLACK);
            return board.isCellFree(f1)
                    && board.isCellFree(g1)
                    && isLocationSafe(f1,board,piece.getChessColor())
                    && isLocationSafe(g1,board,piece.getChessColor());
        }
    }

    private boolean isLocationSafe(ChessCell cell,Board board,ChessColor color) {
        ChessList<ChessPiece> pieces;
        if(color == ChessColor.BLACK) {
            pieces = board.getWhitePieces();
        }else {
            pieces = board.getBlackPieces();
        }
        for (ChessPiece piece: pieces) {
            ChessList<ChessCell> cells = piece.getNextMoves(piece,board);
            if(cells != null && cells.getItemByLocation(cell.getLocation()) != null){
                return false;
            }
        }
        return true;
    }


    public  ChessList<ChessCell> getClassicMoves(ChessPiece piece, Board board){
        ChessList<ChessCell> allCells = board.getChessCells();
        ChessList<ChessCell> validCells = new  ChessList<ChessCell>();
        Location start = piece.getLocation();
        ChessCell up = (ChessCell) allCells.getItemByLocation(new Location(start.getX()+1,0,start.getZ()));
        if(up != null && up.getPiece() == null){
            validCells.add(up);
        }
        ChessCell  upRight = (ChessCell) allCells.getItemByLocation(new Location(start.getX()+1,0,start.getZ()-1));
        if(upRight != null && upRight.getPiece() == null){
            validCells.add(upRight);
        }
        ChessCell  upLeft = (ChessCell) allCells.getItemByLocation(new Location(start.getX()+1,0,start.getZ()+1));
        if(upLeft != null && upLeft.getPiece() == null){
            validCells.add(upLeft);
        }
        ChessCell down = (ChessCell) allCells.getItemByLocation(new Location(start.getX()-1,0,start.getZ()));
        if(down != null && down.getPiece() == null){
            validCells.add(down);
        }
        ChessCell  downLeft = (ChessCell) allCells.getItemByLocation(new Location(start.getX()-1,0,start.getZ()+1));
        if(downLeft != null && downLeft.getPiece() == null){
            validCells.add(downLeft);
        }
        ChessCell  downRight = (ChessCell) allCells.getItemByLocation(new Location(start.getX()-1,0,start.getZ()-1));
        if(downRight != null && downRight.getPiece() == null){
            validCells.add(downRight);
        }
        ChessCell  left = (ChessCell) allCells.getItemByLocation(new Location(start.getX(),0,start.getZ()+1));
        if(left != null && left.getPiece() == null){
            validCells.add(left);
        }
        ChessCell  right = (ChessCell) allCells.getItemByLocation(new Location(start.getX(),0,start.getZ()-1));
        if(right != null && right.getPiece() == null){
            validCells.add(right);
        }
        return validCells;
    }

    @Override
    public void receiveGameEvent(GameEvent event) {
        if(event instanceof PieceEvent && ((PieceEvent) event).type == PieceEventType.MOVE){
            ChessPiece movedPiece =  ((PieceEvent) event).pieceConcerned;
            if(movedPiece.getChessColor() == ChessColor.BLACK){
                if(kingFirstMove){
                    this.kingFirstMove = !(movedPiece.getPieceId() == PieceId.BK);
                }
                if(rookLeftFirstMove){
                    this.rookLeftFirstMove = !(movedPiece.getPieceId() == PieceId.BLR);
                }
                if(this.rookRightFirstMove) {
                    this.rookRightFirstMove = !(movedPiece.getPieceId() == PieceId.BRR);
                }
            }else{
                if(kingFirstMove){
                    this.kingFirstMove = !(movedPiece.getPieceId() == PieceId.WK);
                }
                if(rookLeftFirstMove){
                    this.rookLeftFirstMove = !(movedPiece.getPieceId() == PieceId.WLR);
                }
                if(this.rookRightFirstMove) {
                    this.rookRightFirstMove = !(movedPiece.getPieceId() == PieceId.WRR);
                }

            }
        }
    }
}
