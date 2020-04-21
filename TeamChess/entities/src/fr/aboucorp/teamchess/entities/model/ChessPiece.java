package fr.aboucorp.teamchess.entities.model;


import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.entities.model.moves.Movable;
import fr.aboucorp.teamchess.entities.model.moves.movesets.AbstractMoveSet;
import fr.aboucorp.teamchess.entities.model.utils.ChessCellList;

public abstract class ChessPiece extends GameElement implements Movable {
    protected AbstractMoveSet moveSet;
    private ChessCell actualCell;
    private PieceId pieceId;
    private boolean isFirstMove = true;

    public ChessPiece(ChessCell cell, ChessColor chessColor,PieceId pieceId){
        super(cell.getLocation(), chessColor);
        this.actualCell = cell;
        this.pieceId = pieceId;
        cell.setPiece(this);
    }

    @Override
    public void move(ChessCell cell) {
        this.actualCell.setPiece(null);
        this.actualCell = cell;
        cell.setPiece(this);
        this.setLocation(cell.getLocation());
        this.isFirstMove = false;
    }

    @Override
    public ChessCellList getNextMoves(ChessPiece piece, Board board, ChessColor turnColor) {
        if(moveSet != null){
            return this.moveSet.getMoves(piece,board,turnColor);
        }
        return null;
    }

    public PieceId getPieceId(){
        return this.pieceId;
    }

    public boolean isFirstMove() {
        return isFirstMove;
    }

    @Override
    public String toString() {
        return this.pieceId.toString() + " [" + this.actualCell.getCellLabel() + "]";
    }
}
