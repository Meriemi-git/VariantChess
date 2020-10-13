package fr.aboucorp.variantchess.app.managers;


import android.util.Log;

import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.MatchPresenceEvent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import fr.aboucorp.variantchess.app.db.entities.ChessUser;
import fr.aboucorp.variantchess.app.managers.boards.BoardManager;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;
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
    private SessionManager sessionManager;
    private ChessUser chessUser;

    public OnlineMatchManager(BoardManager boardManager, GameEventManager gameEventManager, ChessUser chessUser) {
        super(boardManager, gameEventManager);
        this.chessUser = chessUser;
        this.sessionManager = SessionManager.getInstance();
        this.sessionManager.setMatchListener(this);
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
                    playOppositeMove(boardState);
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
    public void receiveEvent(GameEvent event) {
        if (event instanceof BoardEvent && ((BoardEvent) event).boardEventType == EventType.CHECKMATE) {
            ChessColor winner = this.boardManager.getWinner();
            this.gameEventManager.sendEvent(new PartyEvent(String.format("Game finished ! Winner : %s", winner != null ? winner.name() : "EQUALITY"), EventType.END_GAME));
        } else if (event instanceof MoveEvent && ((BoardEvent) event).boardEventType == EventType.MOVE) {
            endTurn((MoveEvent) event);
        }

    }

    @Override
    public void startParty(ChessMatch chessMatch) {
        super.startParty(chessMatch);
        if (!chessMatch.whitePlayer.getUserID().equals(this.chessUser.userId)) {
            this.boardManager.waitForNextTurn();
        }
    }

    @Override
    public void endTurn(MoveEvent event) {
        Log.i("fr.aboucorp.variantchess", "EndTurn");
        this.currentTurn.setFen(boardManager.getFenFromBoard());
        if (event != null) {
            this.currentTurn.setPlayed(event.played);
            this.currentTurn.setTo(event.to);
            this.currentTurn.setFrom(event.from);
            this.currentTurn.setDeadPiece(event.deadPiece);
        }
        this.chessMatch.getTurns().add(this.currentTurn);
        this.gameEventManager.sendEvent(new TurnEndEvent("Ending turn", this.chessMatch.getTurns().getLast()));
        if (boardManager.getGameState() != WAIT_FOR_NEXT_TURN) {
            this.boardManager.waitForNextTurn();
            String boardState = this.boardManager.getBoardState();
            Log.i("fr.aboucorp.variantchess", String.format("New state send in matchData : %s", boardState));
            this.sessionManager.sendData(boardState, this.chessMatch.getMatchId(), OPCode.SEND_NEW_FEN);
        } else {
            this.boardManager.stopWaitingForNextTurn();
        }
        startTurn();
    }

    @Override
    public void startTurn() {
        Log.i("fr.aboucorp.variantchess", "StartTurn");
        Turn nextTurn;
        Player player = null;
        if (this.chessMatch.getTurns().size() > 0 && this.chessMatch.getTurns().getLast().getTurnColor() == ChessColor.WHITE) {
            player = chessMatch.getBlackPlayer();
        } else {
            player = chessMatch.getWhitePlayer();
        }
        nextTurn = new Turn(this.chessMatch.getTurns().size() + 1, player);
        this.currentTurn = nextTurn;
        String eventMessage = String.format("Turn %s, color : %s", nextTurn.getTurnNumber(), nextTurn.getTurnColor());
        this.gameEventManager.sendEvent(new TurnStartEvent(eventMessage, nextTurn));
    }

    public void playOppositeMove(String boardState) {
        Log.i("fr.aboucorp.variantchess", String.format("playOppositeMove"));
        this.boardManager.playTheOppositeMove(boardState);
    }
}
