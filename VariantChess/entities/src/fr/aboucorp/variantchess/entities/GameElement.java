package fr.aboucorp.variantchess.entities;

public abstract class GameElement {
    private final fr.aboucorp.variantchess.entities.Location initial;
    private fr.aboucorp.variantchess.entities.Location location;
    private fr.aboucorp.variantchess.entities.ChessColor chessColor;

    GameElement(fr.aboucorp.variantchess.entities.Location location, fr.aboucorp.variantchess.entities.ChessColor chessColor) {
        this.location = location;
        this.initial = location.clone();
        this.chessColor = chessColor;
    }

    public fr.aboucorp.variantchess.entities.Location getLocation() {
        return this.location;
    }

    void setLocation(Location location) {
        this.location = location;
    }

    public ChessColor getChessColor() {
        return this.chessColor;
    }
}
