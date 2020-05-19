package fr.aboucorp.variantchess.entities;


import java.util.List;

public class Team {
    private String name;
    private List<Player> players;
    private final fr.aboucorp.variantchess.entities.ChessColor chessColor;

    public Team(String name, fr.aboucorp.variantchess.entities.ChessColor color) {
        this.name = name;
        this.chessColor = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public ChessColor getChessColor() {
        return chessColor;
    }
}
