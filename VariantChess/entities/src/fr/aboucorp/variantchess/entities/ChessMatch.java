package fr.aboucorp.variantchess.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChessMatch implements Serializable {
    private LinkedList turns = new LinkedList<>();
    private List<Player> players;
    private String matchId;

    public ChessMatch() {
        this.players = new ArrayList<>();
    }

    public Player getPlayerByColor(ChessColor color) {
        return this.players.stream().filter(player -> player.getColor() == color).findFirst().get();
    }

    public LinkedList<Turn> getTurns() {
        return this.turns;
    }

    public void setTurns(LinkedList<Turn> turns) {
        this.turns = turns;
    }

    public String getMatchId() {
        return this.matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public List<Player> getPlayers() {
        return this.players;
    }
}
