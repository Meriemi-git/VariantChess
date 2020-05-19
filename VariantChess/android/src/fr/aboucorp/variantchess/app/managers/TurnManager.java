package fr.aboucorp.variantchess.app.managers;

import java.util.LinkedList;

import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.Team;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.events.GameEventSubscriber;
import fr.aboucorp.variantchess.entities.events.models.TurnEndEvent;
import fr.aboucorp.variantchess.entities.Turn;
import fr.aboucorp.variantchess.entities.events.models.GameEvent;
import fr.aboucorp.variantchess.entities.events.models.MoveEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnStartEvent;

public class TurnManager implements GameEventSubscriber {

    private static TurnManager INSTANCE;
    private LinkedList<fr.aboucorp.variantchess.entities.Turn> turns;
    private GameEventManager eventManager;
    private final Team white;
    private final Team black;

    private TurnManager() {
        this.turns = new LinkedList<fr.aboucorp.variantchess.entities.Turn>();
        this.eventManager = GameEventManager.getINSTANCE();
        this.eventManager.subscribe(fr.aboucorp.variantchess.entities.events.models.MoveEvent.class,this,1);
        this.white = new Team("white",ChessColor.WHITE);
        this.black = new Team("white",ChessColor.BLACK);
    }

    public void newParty(ChessColor color) {
        Team team = (color == ChessColor.WHITE ? white : black);
        fr.aboucorp.variantchess.entities.Turn firsTurn = new fr.aboucorp.variantchess.entities.Turn(1,team);
        this.turns.add(firsTurn);
        String eventMessage = String.format("Next turn, color : %s",firsTurn.getTurnColor());
        this.eventManager.sendMessage(new fr.aboucorp.variantchess.entities.events.models.TurnStartEvent(eventMessage,firsTurn));
    }

    public void startTurn(){
        fr.aboucorp.variantchess.entities.Turn nextTurn;
        if (this.turns.getLast().getTurnColor() == ChessColor.WHITE) {
            nextTurn = new fr.aboucorp.variantchess.entities.Turn(this.turns.size()+1,black);
        }else{
            nextTurn = new fr.aboucorp.variantchess.entities.Turn(this.turns.size()+1,white);
        }
        this.turns.add(nextTurn);
        String eventMessage = String.format("Next turn, color : %s",nextTurn.getTurnColor());
        this.eventManager.sendMessage(new TurnStartEvent(eventMessage,nextTurn));
    }

    public fr.aboucorp.variantchess.entities.Turn endTurn(){
        this.eventManager.sendMessage(new TurnEndEvent("Ending turn",this.turns.getLast()));
        startTurn();
        return this.turns.getLast();
    }

    public Turn getPreviousTurn(){
        return this.turns.getLast();
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
    public void receiveGameEvent(GameEvent event) {
        if(event instanceof fr.aboucorp.variantchess.entities.events.models.MoveEvent){
            this.turns.getLast().setPlayed(((fr.aboucorp.variantchess.entities.events.models.MoveEvent) event).played);
            this.turns.getLast().setTo(((fr.aboucorp.variantchess.entities.events.models.MoveEvent) event).to);
            this.turns.getLast().setFrom(((fr.aboucorp.variantchess.entities.events.models.MoveEvent) event).from);
            this.turns.getLast().setDeadPiece(((MoveEvent) event).deadPiece);
        }
    }
}
