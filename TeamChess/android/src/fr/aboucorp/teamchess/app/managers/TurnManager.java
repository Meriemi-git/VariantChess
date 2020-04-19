package fr.aboucorp.teamchess.app.managers;

import java.util.LinkedList;

import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.ChessTurn;
import fr.aboucorp.teamchess.entities.model.Team;

public class TurnManager {
    private LinkedList<ChessTurn> turns;

    public TurnManager() {
        this.turns = new LinkedList<ChessTurn>();
    }

    public void startParty(){
        Team one = new Team("Team white");
        one.setChessColor(ChessColor.WHITE);
        ChessTurn firstTurn = new ChessTurn(1,one);
        this.turns.add(firstTurn);
    }

    public ChessColor getTurnColor(){
        if(!this.turns.isEmpty()) {
            return this.turns.getLast().getTeam().getChessColor();
        }else{
            return ChessColor.WHITE;
        }
    }
}
