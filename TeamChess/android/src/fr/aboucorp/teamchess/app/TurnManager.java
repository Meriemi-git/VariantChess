package fr.aboucorp.teamchess.app;

import java.util.LinkedList;

import fr.aboucorp.entities.model.ChessTurn;
import fr.aboucorp.generic.model.Color;
import fr.aboucorp.generic.model.Team;

public class TurnManager {
    private LinkedList<ChessTurn> turns;

    public TurnManager() {
        this.turns = new LinkedList();
    }

    public void startParty(){
        Team one = new Team("Team white");
        one.setColor(Color.BLACK);
        ChessTurn firstTurn = new ChessTurn(1,one);
        this.turns.add(firstTurn);
    }

    public Color getTurnColor(){
        return this.turns.getLast().getTeam().getChessColor();
    }
}
