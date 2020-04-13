package fr.aboucorp.teamchess.app;

import java.util.LinkedList;

import fr.aboucorp.generic.model.Team;
import fr.aboucorp.generic.model.enums.Color;
import fr.aboucorp.teamchess.libgdx.models.ChessTurn;

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
        return this.turns.getLast().getTeam().getColor();
    }
}
