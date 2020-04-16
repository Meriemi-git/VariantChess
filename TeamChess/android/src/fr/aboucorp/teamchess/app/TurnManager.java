package fr.aboucorp.teamchess.app;

import java.util.LinkedList;

import fr.aboucorp.entities.model.ChessColor;
import fr.aboucorp.entities.model.ChessTurn;
import fr.aboucorp.entities.model.Team;

public class TurnManager {
    private LinkedList<ChessTurn> turns;

    public TurnManager() {
        this.turns = new LinkedList();
    }

    public void startParty(){
        Team one = new Team("Team white");
        one.setChessColor(ChessColor.BLACK);
        ChessTurn firstTurn = new ChessTurn(1,one);
        this.turns.add(firstTurn);
    }

    public ChessColor getTurnColor(){
        return this.turns.getLast().getTeam().getChessColor();
    }
}
