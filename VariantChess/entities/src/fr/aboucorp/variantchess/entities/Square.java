package fr.aboucorp.variantchess.entities;


public class Square extends GameElement {

    private String squareLabel;
    private char columnLetter;
    private fr.aboucorp.variantchess.entities.Piece piece;

    public Square(Location location, ChessColor chessColor) {
        super(location, chessColor);
        this.columnLetter =  (char)( 65 + (7 - (int)this.getLocation().getX()));
        this.squareLabel =  columnLetter + "" + (int)(this.getLocation().getZ()+1);
    }

    @Override
    public String toString() {
        return '[' + this.squareLabel + ']';
    }

    public fr.aboucorp.variantchess.entities.Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public String getSquareLabel() {
        return squareLabel;
    }

    public char getColumnLetter(){
        return columnLetter;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Square && ((Square) obj).getLocation().equals(getLocation());
    }
}
