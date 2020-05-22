package fr.aboucorp.variantchess.app.managers;

import java.util.LinkedList;

import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.Party;
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
        this.black = new Team("white",ChessColor.BLACK);
    }

    public void startTurn(){
        Turn nextTurn;
        if (this.turns.getLast().getTurnColor() == ChessColor.WHITE) {
            nextTurn = new Turn(this.turns.size()+1,black);
        }else{
            nextTurn = new Turn(this.turns.size()+1,white);
        }
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
            return this.turns.getLast().team.getChessColor();
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
    public void startParty(Party party) {
        Team team = white;
        Turn firsTurn = new Turn(1,team);
        this.turns.add(firsTurn);
        String eventMessage = String.format("Next turn, color : %s",firsTurn.getTurnColor());
        this.eventManager.sendMessage(new TurnStartEvent(eventMessage,firsTurn));
    }

    @Override
    public void stopParty(Party party) {
        this.turns = new LinkedList<>();
    }

    public void startAtTurnColor(ChessColor color) {
        Team team = color == ChessColor.WHITE ? white : black;
        Turn firsTurn = new Turn(1,team);
        this.turns.add(firsTurn);
        String eventMessage = String.format("Turn color : %s",firsTurn.getTurnColor());
        this.eventManager.sendMessage(new TurnStartEvent(eventMessage,firsTurn));
    }
}
