package fr.aboucorp.teamchess.entities.model;


import java.util.List;

public class Team {
    private String name;
    private List<Player> players;
    private final ChessColor chessColor;

    public Team(String name,ChessColor color) {
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

    public fr.aboucorp.teamchess.entities.model.ChessColor getChessColor() {
        return chessColor;
    }
}
