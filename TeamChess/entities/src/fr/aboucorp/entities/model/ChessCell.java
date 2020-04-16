package fr.aboucorp.entities.model;


public class ChessCell extends GameElement {

    private String LetterLabel;
    private int NumberLabel;

    private ChessPiece piece;

    public ChessCell(Location location, ChessColor chessColor) {
        super(location, chessColor);
        this.setLabels(location);
    }
    private void setLabels(Location location) {
        this.LetterLabel = String.valueOf( this.getLocation().getX() + 65);
        this.NumberLabel = this.getLocation().getZ();
    }

    @Override
    public String toString() {
        return '[' + this.LetterLabel+this.NumberLabel + ']';
    }

    public ChessPiece getPiece() {
        return piece;
    }

    public void setPiece(ChessPiece piece) {
        this.piece = piece;
    }


}
