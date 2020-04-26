package fr.aboucorp.teamchess.entities.model;


import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.entities.model.moves.AbstractMoveSet;

public abstract class Piece extends GameElement {
    protected AbstractMoveSet moveSet;
    private Square actualSquare;
    private PieceId pieceId;
    private boolean isFirstMove = true;

    public Piece(Square square, ChessColor chessColor, PieceId pieceId){
        super(square.getLocation(), chessColor);
        this.actualSquare = square;
        this.pieceId = pieceId;
        square.setPiece(this);
    }

    public void move(Square square) {
        this.actualSquare.setPiece(null);
        this.actualSquare = square;
        square.setPiece(this);
        this.setLocation(square.getLocation());
        this.isFirstMove = false;
    }

    public PieceId getPieceId(){
        return this.pieceId;
    }

    public boolean isFirstMove() {
        return isFirstMove;
    }

    public Square getActualSquare() {
        return actualSquare;
    }

    public void die(){
        this.actualSquare.setPiece(null);
        this.actualSquare = null;
        this.setLocation(null);
    }

    @Override
    public String toString() {
        return this.pieceId.toString() + " [" + (this.actualSquare != null ? this.actualSquare.getSquareLabel() : "EVEN") + "]";
    }

    public AbstractMoveSet getMoveSet() {
        return moveSet;
    }
}
