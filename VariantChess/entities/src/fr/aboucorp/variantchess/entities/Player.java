package fr.aboucorp.variantchess.entities;


public class Player {
    protected String name;
    protected ChessColor color;

    public Player(String name, ChessColor color) {
        this.name = name;
        this.color = color;
    }

    public Player() {
    }

    public ChessColor getColor() {
        return this.color;
    }

    public String getName() {
        return this.name;
    }

}
