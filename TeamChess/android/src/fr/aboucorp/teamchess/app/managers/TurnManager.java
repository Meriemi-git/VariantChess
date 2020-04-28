package fr.aboucorp.teamchess.app.managers;

import java.util.LinkedList;

import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.Team;
import fr.aboucorp.teamchess.entities.model.Turn;
import fr.aboucorp.teamchess.entities.model.events.GameEventManager;
import fr.aboucorp.teamchess.entities.model.events.GameEventSubscriber;
import fr.aboucorp.teamchess.entities.model.events.models.GameEvent;
import fr.aboucorp.teamchess.entities.model.events.models.MoveEvent;
import fr.aboucorp.teamchess.entities.model.events.models.TurnEndEvent;
import fr.aboucorp.teamchess.entities.model.events.models.TurnStartEvent;

public class TurnManager implements GameEventSubscriber {

    private static TurnManager INSTANCE;
    private LinkedList<Turn> turns;
    private GameEventManager eventManager;
    private final Team white;
    private final Team black;

    private TurnManager() {
        this.turns = new LinkedList<Turn>();
        this.eventManager = GameEventManager.getINSTANCE();
        this.eventManager.subscribe(MoveEvent.class,this,1);
        this.white = new Team("white",ChessColor.WHITE);
        this.black = new Team("white",ChessColor.BLACK);
    }

    public void newParty(ChessColor color) {
        Team team = (color == ChessColor.WHITE ? white : black);
        Turn firsTurn = new Turn(1,team);
        this.turns.add(firsTurn);
        String eventMessage = String.format("Next turn, color : %s",firsTurn.getTurnColor());
        this.eventManager.sendMessage(new TurnStartEvent(eventMessage,firsTurn));
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

    public Turn endTurn(){
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
        if(event instanceof MoveEvent){
            this.turns.getLast().setPlayed(((MoveEvent) event).played);
            this.turns.getLast().setFrom(((MoveEvent) event).from);
            this.turns.getLast().setTo(((MoveEvent) event).to);
            this.turns.getLast().setDeadPiece(((MoveEvent) event).deadPiece);
        }
    }


}
