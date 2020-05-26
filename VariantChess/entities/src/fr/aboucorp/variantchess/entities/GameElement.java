package fr.aboucorp.variantchess.entities;

public abstract class GameElement {
    private Location location;
    private ChessColor chessColor;

    public GameElement(Location location, ChessColor chessColor) {
        this.location = location;
        this.chessColor = chessColor;
    }

    public Location getLocation() {
        return this.location;
    }

    void setLocation(Location location) {
        this.location = location;
    }

    public ChessColor getChessColor() {
        return this.chessColor;
    }
}
