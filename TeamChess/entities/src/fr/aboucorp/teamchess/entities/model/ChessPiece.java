package fr.aboucorp.teamchess.entities.model;


import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.entities.model.moves.Movable;

public abstract class ChessPiece extends GameElement implements Movable {

    private ChessCell actualCell;
    private PieceId pieceId;

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
    }

    public PieceId getPieceId(){
        return this.pieceId;
    }
}
