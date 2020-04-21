package fr.aboucorp.teamchess.app.managers;

import java.util.LinkedList;

import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.ChessTurn;
import fr.aboucorp.teamchess.entities.model.Team;
import fr.aboucorp.teamchess.entities.model.events.GameEventManager;
import fr.aboucorp.teamchess.entities.model.events.TurnEvent;

public class TurnManager {

    private LinkedList<ChessTurn> turns;
    private GameEventManager eventManager;
    private final Team white;
    private final Team black;

    public TurnManager(Team white,Team black) {
        this.turns = new LinkedList<ChessTurn>();
        this.eventManager = GameEventManager.getINSTANCE();
        this.white = white;
        this.black = black;
    }


    public void nextTurn(){
        ChessTurn nextTurn;
        if(this.turns.isEmpty()){
            nextTurn = new ChessTurn(1,white);
        }else{
            if (this.turns.getLast().getTurnColor() == ChessColor.WHITE) {
                nextTurn = new ChessTurn(this.turns.size()+1,black);
            }else{
                nextTurn = new ChessTurn(this.turns.size()+1,white);
            }
        }
        this.turns.add(nextTurn);
        this.eventManager.sendMessage(new TurnEvent(String.format("Next turn, color : %s;",nextTurn.getTurnColor()),nextTurn));
    }

    public ChessColor getTurnColor(){
        if(!this.turns.isEmpty()) {
            return this.turns.getLast().getTeam().getChessColor();
        }else{
            return ChessColor.WHITE;
        }
    }
}
