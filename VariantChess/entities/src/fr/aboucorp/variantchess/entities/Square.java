package fr.aboucorp.variantchess.entities;


public class Square extends GameElement {

    private String squareLabel;
    private char columnLetter;
    private fr.aboucorp.variantchess.entities.Piece piece;

    public Square(Location location, ChessColor chessColor) {
        super(location, chessColor);
        this.columnLetter =  (char)( 65 + (7 - (int)this.getLocation().getX()));
        this.squareLabel = this.columnLetter + "" + (int)(this.getLocation().getZ()+1);
    }

    @Override
    public String toString() {
        return '[' + this.squareLabel + ']';
    }

    public fr.aboucorp.variantchess.entities.Piece getPiece() {
        return this.piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public String getSquareLabel() {
        return this.squareLabel;
    }

    public char getColumnLetter(){
        return this.columnLetter;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Square && ((Square) obj).getLocation().equals(this.getLocation());
    }
}
