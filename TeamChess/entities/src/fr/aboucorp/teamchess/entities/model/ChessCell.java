package fr.aboucorp.teamchess.entities.model;


public class ChessCell extends GameElement {

    private String cellLabel;

    private ChessPiece piece;

    public ChessCell(Location location, ChessColor chessColor) {
        super(location, chessColor);
        this.cellLabel = Character.toString( (char)( 65 + (7 - this.getLocation().getX())) )+ (this.getLocation().getZ()+1);
    }

    @Override
    public String toString() {
        return '[' + this.cellLabel + ']';
    }

    public ChessPiece getPiece() {
        return piece;
    }

    public void setPiece(ChessPiece piece) {
        this.piece = piece;
    }

    public String getCellLabel() {
        return cellLabel;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ChessCell && ((ChessCell) obj).getLocation().equals(getLocation());
    }
}
