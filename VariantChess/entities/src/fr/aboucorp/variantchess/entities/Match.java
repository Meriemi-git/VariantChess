package fr.aboucorp.variantchess.entities;

import java.util.LinkedList;

public class Match{
    public LinkedList<Turn> turns = new LinkedList<>();
    public Player whitePlayer;
    public Player blackPlayer;


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

    public LinkedList<Turn> getTurns() {
        return this.turns;
    }

    public void setTurns(LinkedList<Turn> turns) {
        this.turns = turns;
    }
}
