package fr.aboucorp.variantchess.app.managers;

import java.util.LinkedList;

import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.Match;
import fr.aboucorp.variantchess.entities.PartyLifeCycle;
import fr.aboucorp.variantchess.entities.Team;
import fr.aboucorp.variantchess.entities.Turn;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.events.models.MoveEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnEndEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnStartEvent;

public class TurnManager implements PartyLifeCycle {

    private static TurnManager INSTANCE;
    private LinkedList<Turn> turns;
    private GameEventManager eventManager;
    private final Team white;
    private final Team black;

    private TurnManager() {
        this.turns = new LinkedList<>();
        this.eventManager = GameEventManager.getINSTANCE();
        this.white = new Team("white",ChessColor.WHITE);
        this.black = new Team("black",ChessColor.BLACK);
    }

    public void startTurn(){
        Turn nextTurn;
        Team team = null;
        if (this.turns.getLast().getTurnColor() == ChessColor.WHITE) {
            team =  this.black;
        }else{
            team =  this.white;
        }
        nextTurn = new Turn(this.turns.size()+1, this.white);
        this.turns.add(nextTurn);
        String eventMessage = String.format("Next turn, color : %s",nextTurn.getTurnColor());
        this.eventManager.sendMessage(new TurnStartEvent(eventMessage,nextTurn));
    }

    public void endTurn(MoveEvent event){
        if(event !=null) {
            this.turns.getLast().setPlayed(event.played);
            this.turns.getLast().setTo(event.to);
            this.turns.getLast().setFrom( event.from);
            this.turns.getLast().setDeadPiece(event.deadPiece);
        }
        this.eventManager.sendMessage(new TurnEndEvent("Ending turn",this.turns.getLast()));
    }


    public ChessColor getTurnColor(){
        if(!this.turns.isEmpty()) {
            return this.turns.getLast().getTeam().getChessColor();
        }else{
            return ChessColor.WHITE;
        }
    }

    public static TurnManager getINSTANCE(){
        if(INSTANCE == null){
            INSTANCE = new TurnManager();
        }
        return INSTANCE;
    }

    @Override
    public void startParty(Match match) {
        Team team = this.white;
        Turn firsTurn = new Turn(1,team);
        this.turns.add(firsTurn);
        String eventMessage = String.format("Next turn, color : %s",firsTurn.getTurnColor());
        this.eventManager.sendMessage(new TurnStartEvent(eventMessage,firsTurn));
    }

    @Override
    public void stopParty() {
        this.turns = new LinkedList<>();
    }

    public void startAtTurnColor(ChessColor color) {
        Team team = color == ChessColor.WHITE ? this.white : this.black;
        Turn firsTurn = new Turn(1,team);
        this.turns.add(firsTurn);
        String eventMessage = String.format("Turn color : %s",firsTurn.getTurnColor());
        this.eventManager.sendMessage(new TurnStartEvent(eventMessage,firsTurn));
    }
}
