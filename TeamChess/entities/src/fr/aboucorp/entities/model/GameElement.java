package fr.aboucorp.entities.model;

public abstract class GameElement {
    private Location location;
    private ChessColor chessColor;

    public GameElement(Location location, ChessColor chessColor) {
        this.location = location;
        this.chessColor = chessColor;
    }
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ChessColor getChessColor() {
        return chessColor;
    }
}
