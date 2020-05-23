package fr.aboucorp.variantchess.entities;

import java.util.LinkedList;

public class Match{
    protected LinkedList<Turn> turns = new LinkedList<>();
    protected Player whitePlayer;
    protected Player blackPlayer;

    public LinkedList<Turn> getTurns() {
        return this.turns;
    }

    public void setTurns(LinkedList<Turn> turns) {
        this.turns = turns;
    }

    public void setWhitePlayer(Player whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    public Player getBlackPlayer() {
        return this.blackPlayer;
    }

    public void setBlackPlayer(Player blackPlayer) {
        this.blackPlayer = blackPlayer;
    }

    public Player getWhitePlayer() {
        return whitePlayer;
    }
}
