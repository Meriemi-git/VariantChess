package fr.aboucorp.variantchess.entities;

import java.util.LinkedList;

public class Match {
    private LinkedList<Turn> turns = new LinkedList<>();

    public LinkedList<Turn> getTurns() {
        return this.turns;
    }

    public void setTurns(LinkedList<Turn> turns) {
        this.turns = turns;
    }
}
