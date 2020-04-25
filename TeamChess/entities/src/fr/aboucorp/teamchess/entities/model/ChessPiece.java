package fr.aboucorp.teamchess.entities.model;


import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.entities.model.moves.AbstractMoveSet;
import fr.aboucorp.teamchess.entities.model.moves.Movable;

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

    public PieceId getPieceId(){
        return this.pieceId;
    }

    public boolean isFirstMove() {
        return isFirstMove;
    }

    public ChessCell getActualCell() {
        return actualCell;
    }

    public void die(){
        this.actualCell.setPiece(null);
        this.actualCell = null;
        this.setLocation(null);
    }

    @Override
    public String toString() {
        return this.pieceId.toString() + " [" + (this.actualCell != null ? this.actualCell.getCellLabel() : "EVEN") + "]";
    }

    public AbstractMoveSet getMoveSet() {
        return moveSet;
    }
}
