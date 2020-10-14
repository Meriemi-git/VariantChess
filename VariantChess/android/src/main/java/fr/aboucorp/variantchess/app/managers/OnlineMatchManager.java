package fr.aboucorp.variantchess.app.managers;


import android.util.Log;

import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.MatchPresenceEvent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import fr.aboucorp.variantchess.app.db.entities.VariantUser;
import fr.aboucorp.variantchess.app.managers.boards.BoardManager;
import fr.aboucorp.variantchess.app.multiplayer.NakamaManager;
import fr.aboucorp.variantchess.app.multiplayer.listeners.MatchListener;
import fr.aboucorp.variantchess.app.utils.OPCode;
import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.ChessMatch;
import fr.aboucorp.variantchess.entities.Player;
import fr.aboucorp.variantchess.entities.Turn;
import fr.aboucorp.variantchess.entities.enums.EventType;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.events.models.BoardEvent;
import fr.aboucorp.variantchess.entities.events.models.GameEvent;
import fr.aboucorp.variantchess.entities.events.models.MoveEvent;
import fr.aboucorp.variantchess.entities.events.models.PartyEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnEndEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnStartEvent;

import static fr.aboucorp.variantchess.entities.enums.GameState.WAIT_FOR_NEXT_TURN;

public class OnlineMatchManager extends MatchManager implements MatchListener {
    private final NakamaManager nakamaManager;
    private final VariantUser variantUser;

    public OnlineMatchManager(BoardManager boardManager, GameEventManager gameEventManager, VariantUser variantUser) {
        super(boardManager, gameEventManager);
        this.variantUser = variantUser;
        this.nakamaManager = NakamaManager.getInstance();
        this.nakamaManager.setMatchListener(this);
    }

    @Override
    public void onMatchData(MatchData matchData) {
        try {
            ObjectInputStream ois;
            ois = new ObjectInputStream(new ByteArrayInputStream(matchData.getData()));
            switch ((int) matchData.getOpCode()) {
                case OPCode.SEND_NEW_FEN:
                    String boardState = (String) ois.readObject();
                    Log.i("fr.aboucorp.variantchess", String.format("Receiving FEN : %s", boardState));
                    this.playOppositeMove(boardState);
                    break;
                case OPCode.SEND_WHITE_PLAYER:
                    String matchState = (String) ois.readObject();
                    Log.i("fr.aboucorp.variantchess", String.format("Receving whitePlayer : %s", matchState));
                    break;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            Log.e("fr.aboucorp.variantchess", String.format("Error when receiving match data opeCode: %s", matchData.getOpCode()));
        }
    }

    @Override
    public void onMatchPresence(MatchPresenceEvent matchPresence) {
        Log.i("fr.aboucorp.variantchess", String.format("onMatchPresence : %s", matchPresence));
    }

    @Override
    public void OnBoardLoaded() {
        this.gameEventManager.subscribe(GameEvent.class, this, 1, "OnlineMatchManager => GameEvent");
        this.startTurn();
    }

    @Override
    public void receiveEvent(GameEvent event) {
        if (event instanceof BoardEvent && ((BoardEvent) event).boardEventType == EventType.CHECKMATE) {
            ChessColor winner = this.boardManager.getWinner();
            this.gameEventManager.sendEvent(new PartyEvent(String.format("Game finished ! Winner : %s", winner != null ? winner.name() : "EQUALITY"), EventType.END_GAME));
        } else if (event instanceof MoveEvent && ((BoardEvent) event).boardEventType == EventType.MOVE) {
            this.endTurn((MoveEvent) event);
        }
    }

    @Override
    public void startParty(ChessMatch chessMatch) {
        this.chessMatch = chessMatch;
        this.boardManager.startParty(chessMatch);
        if (!chessMatch.whitePlayer.getUserID().equals(this.variantUser.userId)) {
            this.boardManager.waitForNextTurn();
        }
    }

    @Override
    public void stopParty() {
        this.boardManager.stopParty();
        this.chessMatch = null;
    }

    @Override
    public void endTurn(MoveEvent event) {
        Log.i("fr.aboucorp.variantchess", "EndTurn");
        this.currentTurn.setFen(this.boardManager.getFenFromBoard());
        if (event != null) {
            this.currentTurn.setPlayed(event.played);
            this.currentTurn.setTo(event.to);
            this.currentTurn.setFrom(event.from);
            this.currentTurn.setDeadPiece(event.deadPiece);
        }
        this.chessMatch.getTurns().add(this.currentTurn);
        this.gameEventManager.sendEvent(new TurnEndEvent("Ending turn", this.chessMatch.getTurns().getLast()));
        if (this.boardManager.getGameState() != WAIT_FOR_NEXT_TURN) {
            this.boardManager.waitForNextTurn();
            String boardState = this.boardManager.getBoardState();
            Log.i("fr.aboucorp.variantchess", String.format("New state send in matchData : %s", boardState));
            this.nakamaManager.sendData(boardState, this.chessMatch.getMatchId(), OPCode.SEND_NEW_FEN);
        } else {
            this.boardManager.stopWaitingForNextTurn();
        }
        this.startTurn();
    }

    @Override
    public void startTurn() {
        Log.i("fr.aboucorp.variantchess", "StartTurn");
        Turn nextTurn;
        Player player = null;
        if (this.chessMatch.getTurns().size() > 0 && this.chessMatch.getTurns().getLast().getTurnColor() == ChessColor.WHITE) {
            player = this.chessMatch.getBlackPlayer();
        } else {
            player = this.chessMatch.getWhitePlayer();
        }
        nextTurn = new Turn(this.chessMatch.getTurns().size() + 1, player);
        this.currentTurn = nextTurn;
        String eventMessage = String.format("Turn %s, color : %s", nextTurn.getTurnNumber(), nextTurn.getTurnColor());
        this.gameEventManager.sendEvent(new TurnStartEvent(eventMessage, nextTurn));
    }

    @Override
    public String getPartyInfos() {
        return this.currentTurn.getTurnColor().name();
    }

    @Override
    public void passTurn() {
        Log.i("fr.aboucorp.variantchess", String.format("Passing Turn"));
        this.currentTurn.setFen(this.boardManager.getFenFromBoard());
        this.chessMatch.getTurns().add(this.currentTurn);
        List<String> receiversName = this.gameEventManager.sendEvent(new TurnEndEvent("Ending turn", this.chessMatch.getTurns().getLast()));
        Log.i("fr.aboucorp.varaintchess", String.format("In passTurn() send TurnEndEvent reiceivers :\n%s", String.join("\n", receiversName)));
        String boardState = this.boardManager.getBoardState();
        this.boardManager.waitForNextTurn();
        this.nakamaManager.sendData(boardState, this.chessMatch.getMatchId(), OPCode.SEND_NEW_FEN);
        this.startTurn();

    }

    @Override
    public boolean isMyTurn() {
        return this.currentTurn.getPlayer().getUsername().equals(this.variantUser.username);
    }


    public void playOppositeMove(String boardState) {
        Log.i("fr.aboucorp.variantchess", String.format("playOppositeMove"));
        this.boardManager.playTheOppositeMove(boardState);
    }
}
