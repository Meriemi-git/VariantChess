package fr.aboucorp.teamchess.entities.model;


public class Square extends GameElement {

    private String squareLabel;

    private Piece piece;

    public Square(Location location, ChessColor chessColor) {
        super(location, chessColor);
        this.squareLabel = Character.toString( (char)( 65 + (7 - this.getLocation().getX())) )+ (this.getLocation().getZ()+1);
    }

    @Override
    public String toString() {
        return '[' + this.squareLabel + ']';
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public String getSquareLabel() {
        return squareLabel;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Square && ((Square) obj).getLocation().equals(getLocation());
    }
}
