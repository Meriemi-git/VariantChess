package fr.aboucorp.variantchess.entities;

import java.io.Serializable;
import java.util.LinkedList;

public class ChessMatch implements Serializable {
    public LinkedList turns = new LinkedList<>();
    public Player whitePlayer;
    public Player blackPlayer;
    private String matchId;
    private Player currentPlayer;

    public Player getBlackPlayer() {
        return this.blackPlayer;
    }

    public void setBlackPlayer(Player blackPlayer) {
        this.blackPlayer = blackPlayer;
    }

    public Player getWhitePlayer() {
        return this.whitePlayer;
    }

    public void setWhitePlayer(Player whitePlayer) {
        this.whitePlayer = whitePlayer;
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

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
}
