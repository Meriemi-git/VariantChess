package fr.aboucorp.teamchess.app.managers;

import java.util.LinkedList;

import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.ChessTurn;
import fr.aboucorp.teamchess.entities.model.Team;
import fr.aboucorp.teamchess.entities.model.events.GameEventManager;
import fr.aboucorp.teamchess.entities.model.events.GameEventSubscriber;
import fr.aboucorp.teamchess.entities.model.events.models.GameEvent;
import fr.aboucorp.teamchess.entities.model.events.models.MoveEvent;
import fr.aboucorp.teamchess.entities.model.events.models.TurnEndEvent;
import fr.aboucorp.teamchess.entities.model.events.models.TurnStartEvent;

public class TurnManager implements GameEventSubscriber {

    private static TurnManager INSTANCE;
    private LinkedList<ChessTurn> turns;
    private GameEventManager eventManager;
    private final Team white;
    private final Team black;

    private TurnManager() {
        this.turns = new LinkedList<ChessTurn>();
        this.eventManager = GameEventManager.getINSTANCE();
        this.eventManager.subscribe(MoveEvent.class,this);
        this.white = new Team("white",ChessColor.WHITE);
        this.black = new Team("white",ChessColor.BLACK);
    }


    public void startTurn(){
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
        String eventMessage = String.format("Next turn, color : %s",nextTurn.getTurnColor());
        this.eventManager.sendMessage(new TurnStartEvent(eventMessage,nextTurn));
    }

    public ChessTurn endTurn(){
        this.eventManager.sendMessage(new TurnEndEvent("Ending turn",this.turns.getLast()));
        startTurn();
        return this.turns.getLast();
    }

    public ChessTurn getPreviousTurn(){
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
