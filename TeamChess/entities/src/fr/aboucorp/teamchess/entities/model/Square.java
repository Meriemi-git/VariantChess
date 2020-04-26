package fr.aboucorp.teamchess.entities.model;


public class Square extends GameElement {

    private String cellLabel;

    private Piece piece;

    public Square(Location location, ChessColor chessColor) {
        super(location, chessColor);
        this.cellLabel = Character.toString( (char)( 65 + (7 - this.getLocation().getX())) )+ (this.getLocation().getZ()+1);
    }

    @Override
    public String toString() {
        return '[' + this.cellLabel + ']';
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public String getCellLabel() {
        return cellLabel;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Square && ((Square) obj).getLocation().equals(getLocation());
    }
}
