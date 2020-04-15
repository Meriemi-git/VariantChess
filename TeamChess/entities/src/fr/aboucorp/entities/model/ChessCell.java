package fr.aboucorp.entities.model;


public class ChessCell extends GameElement {

    private String LetterLabel;
    private int NumberLabel;
    protected Location location;
    private ChessPiece piece;

    public ChessCell(Location location, ChessColor chessColor) {
        super(location, chessColor);
        this.setLabels(location);
        this.location = location;
    }

    private void setLabels(Location location) {
        this.LetterLabel = String.valueOf( this.location.getX() + 65);
        this.NumberLabel = this.location.getZ();
    }

    @Override
    public String toString() {
        return '[' + this.LetterLabel+this.NumberLabel + ']';
    }

    public String getLetterLabel() {
        return this.LetterLabel;
    }

    public void setLetterLabel(String letterLabel) {
        this.LetterLabel = letterLabel;
    }

    public int getNumberLabel() {
        return this.NumberLabel;
    }

    public void setNumberLabel(int numberLabel) {
        this.NumberLabel = numberLabel;
    }

    public Location getLocation() {
        return this.location;
    }

    public ChessPiece getPiece() {
        return piece;
    }

    public void setPiece(ChessPiece piece) {
        this.piece = piece;
    }

    public ChessColor getColor(){
        return this.chessColor;
    }

}
