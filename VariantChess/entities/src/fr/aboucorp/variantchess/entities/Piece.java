package fr.aboucorp.variantchess.entities;


import fr.aboucorp.variantchess.entities.enums.PieceId;
import fr.aboucorp.variantchess.entities.moves.AbstractMoveSet;

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
        return this.isFirstMove;
    }

    public void setFirstMove(boolean firstMove) {
        this.isFirstMove = firstMove;
    }

    public void setActualSquare(Square actualSquare) {
        this.actualSquare = actualSquare;
    }

    public Square getSquare() {
        return this.actualSquare;
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
        return this.moveSet;
    }

    public abstract char fen();


}
