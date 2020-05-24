package fr.aboucorp.variantchess.app.managers;

import android.util.Log;

import com.heroiclabs.nakama.ChannelPresenceEvent;
import com.heroiclabs.nakama.Error;
import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.MatchPresenceEvent;
import com.heroiclabs.nakama.MatchmakerMatched;
import com.heroiclabs.nakama.StatusPresenceEvent;
import com.heroiclabs.nakama.StreamData;
import com.heroiclabs.nakama.StreamPresenceEvent;
import com.heroiclabs.nakama.api.ChannelMessage;
import com.heroiclabs.nakama.api.NotificationList;

import fr.aboucorp.variantchess.app.listeners.MatchEventListener;
import fr.aboucorp.variantchess.app.managers.boards.BoardManager;
import fr.aboucorp.variantchess.app.multiplayer.MatchListener;
import fr.aboucorp.variantchess.app.parcelables.MatchP;
import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.Match;
import fr.aboucorp.variantchess.entities.PartyLifeCycle;
import fr.aboucorp.variantchess.entities.Turn;
import fr.aboucorp.variantchess.entities.enums.BoardEventType;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.events.GameEventSubscriber;
import fr.aboucorp.variantchess.entities.events.models.BoardEvent;
import fr.aboucorp.variantchess.entities.events.models.GameEvent;
import fr.aboucorp.variantchess.entities.events.models.MoveEvent;
import fr.aboucorp.variantchess.entities.events.models.PartyEvent;

public class MatchManager implements GameEventSubscriber, MatchListener, BoardManager.BoardLoadingListener, PartyLifeCycle {
    private final BoardManager boardManager;
    private final TurnManager turnManager;
    private GameEventManager eventManager;
    private MatchEventListener eventListener;

    private MatchP match;


    public MatchManager(BoardManager boardManager,GameEventManager gameEventManager) {
        this.eventManager = gameEventManager;
        this.boardManager = boardManager;
        this.boardManager.setBoardLoadingListener(this);
        this.turnManager = new TurnManager(gameEventManager);
    }

    @Override
    public void OnBoardLoaded() {
        this.eventManager.subscribe(GameEvent.class,this,1);
        this.turnManager.startParty(this.match);
    }

    public void endTurn(MoveEvent event) {
        this.turnManager.endTurn(event,this.boardManager.getFenFromBoard());
        this.turnManager.startTurn();
    }

    public String getPartyInfos(){
        return this.turnManager.getTurnColor().name();
    }

    @Override
    public void receiveGameEvent(GameEvent event) {
        this.eventListener.OnMatchEvent(event);
        if(event instanceof PartyEvent){
            Log.i("fr.aboucorp.variantchess",event.message);
        }else if(event instanceof BoardEvent && ((BoardEvent) event).type == BoardEventType.CHECKMATE){
            ChessColor winner = this.boardManager.getWinner();
            this.eventManager.sendMessage(new PartyEvent(String.format("Game finished ! Winner : %s",winner != null ? winner.name() : "EQUALITY")));
        }else if(event instanceof MoveEvent && ((BoardEvent) event).type == BoardEventType.MOVE){
            this.endTurn(((MoveEvent)event));
        }
    }

    @Override
    public void startParty(Match match) {
        this.match = (MatchP) match;
        this.boardManager.startParty(match);
    }

    @Override
    public void stopParty() {
        this.eventManager.stopParty();
        this.boardManager.stopParty();
    }

    @Override
    public void onDisconnect(Throwable t) {

    }

    @Override
    public void onError(Error error) {

    }

    @Override
    public void onChannelMessage(ChannelMessage message) {

    }

    @Override
    public void onChannelPresence(ChannelPresenceEvent presence) {

    }

    @Override
    public void onMatchmakerMatched(MatchmakerMatched matched) {

    }

    @Override
    public void onMatchData(MatchData matchData) {

    }

    @Override
    public void onMatchPresence(MatchPresenceEvent matchPresence) {

    }

    @Override
    public void onNotifications(NotificationList notifications) {

    }

    @Override
    public void onStatusPresence(StatusPresenceEvent presence) {

    }

    @Override
    public void onStreamPresence(StreamPresenceEvent presence) {

    }

    @Override
    public void onStreamData(StreamData data) {

    }

    public void setEventListener(MatchEventListener eventListener) {
        this.eventListener = eventListener;
    }
}
