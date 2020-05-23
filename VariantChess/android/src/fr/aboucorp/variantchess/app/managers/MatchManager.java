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
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;
import fr.aboucorp.variantchess.app.views.activities.VariantChessActivity;
import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.Party;
import fr.aboucorp.variantchess.entities.PartyLifeCycle;
import fr.aboucorp.variantchess.entities.enums.BoardEventType;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.events.GameEventSubscriber;
import fr.aboucorp.variantchess.entities.events.models.BoardEvent;
import fr.aboucorp.variantchess.entities.events.models.GameEvent;
import fr.aboucorp.variantchess.entities.events.models.LogEvent;
import fr.aboucorp.variantchess.entities.events.models.MoveEvent;
import fr.aboucorp.variantchess.entities.events.models.PartyEvent;

public class MatchManager implements GameEventSubscriber, MatchListener, BoardManager.BoardLoadingListener, PartyLifeCycle {
    private final BoardManager boardManager;
    private final TurnManager turnManager;
    private GameEventManager eventManager;
    private SessionManager sessionManager;
    private Party party;
    private final MatchEventListener eventListener;


    public MatchManager(VariantChessActivity activity, BoardManager boardManager, MatchEventListener eventListener) {
        this.boardManager = boardManager;
        this.eventListener = eventListener;
        this.boardManager.setBoardLoadingListener(this);
        this.turnManager = TurnManager.getINSTANCE();
        this.eventManager = GameEventManager.getINSTANCE();
        this.sessionManager = SessionManager.getInstance(activity);

    }

    @Override
    public void OnBoardLoaded() {
        this.eventManager.subscribe(PartyEvent.class,this,1);
        this.eventManager.subscribe(BoardEvent.class,this,1);
        this.turnManager.startParty(this.party);
    }

    public void loadBoard(String fenString){
        try {
            ChessColor color = this.boardManager.loadBoard(fenString);
            this.turnManager.startAtTurnColor(color);
        } catch (Exception e) {
            e.printStackTrace();
            this.eventManager.sendMessage(new LogEvent(String.format("Error during parsing fen string. Message : %s",e.getMessage())));
        }
    }

    public void endTurn(MoveEvent event) {
        this.turnManager.endTurn(event);
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
            ChessColor winner = boardManager.getWinner();
            this.eventManager.sendMessage(new PartyEvent(String.format("Game finished ! Winner : %s",winner != null ? winner.name() : "EQUALITY")));
        }else if(event instanceof MoveEvent && ((BoardEvent) event).type == BoardEventType.MOVE){
            endTurn(((MoveEvent)event));
        }
    }


    @Override
    public void startParty(Party party) {
        this.party = party;
        this.boardManager.startParty(party);
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


}
