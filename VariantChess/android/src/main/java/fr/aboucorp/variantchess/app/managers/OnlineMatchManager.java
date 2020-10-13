package fr.aboucorp.variantchess.app.managers;


import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.MatchPresenceEvent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import fr.aboucorp.variantchess.app.db.entities.ChessUser;
import fr.aboucorp.variantchess.app.managers.boards.BoardManager;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;
import fr.aboucorp.variantchess.app.multiplayer.listeners.MatchListener;
import fr.aboucorp.variantchess.app.utils.LogUtil;
import fr.aboucorp.variantchess.app.utils.OPCode;
import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.ChessMatch;
import fr.aboucorp.variantchess.entities.Turn;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.events.models.GameEvent;
import fr.aboucorp.variantchess.entities.events.models.MoveEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnEndEvent;

public class OnlineMatchManager extends MatchManager implements MatchListener {
    private SessionManager sessionManager;
    private ChessUser currentPlayer;

    public OnlineMatchManager(BoardManager boardManager, GameEventManager gameEventManager, ChessUser currentPlayer) {
        super(boardManager, gameEventManager);
        this.currentPlayer = currentPlayer;
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
                    LogUtil.i("fr.aboucorp.variantchess", String.format("Receiving FEN : %s", boardState));
                    playOppositeMove(boardState);
                    break;
                case OPCode.SEND_WHITE_PLAYER:
                    String matchState = (String) ois.readObject();
                    LogUtil.i("fr.aboucorp.variantchess", String.format("Receving whitePlayer : %s", matchState));
                    break;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            LogUtil.e("fr.aboucorp.variantchess", String.format("Error when receiving match data opeCode: %s", matchData.getOpCode()));
        }
    }

    @Override
    public void onMatchPresence(MatchPresenceEvent matchPresence) {
        LogUtil.i("fr.aboucorp.variantchess", String.format("onMatchPresence : %s", matchPresence));
    }

    @Override
    public void startParty(ChessMatch chessMatch) {
        super.startParty(chessMatch);
        if (!chessMatch.whitePlayer.getUserID().equals(this.currentPlayer.userId)) {
            this.boardManager.waitForNextTurn();
        }
    }

    @Override
    public void endTurn(MoveEvent event) {
        this.turnManager.endTurn(event, this.boardManager.getFenFromBoard());
        this.boardManager.waitForNextTurn();
    }

    @Override
    public void receiveGameEvent(GameEvent event) {
        super.receiveGameEvent(event);
        if (event instanceof TurnEndEvent && this.currentPlayer.userId.equals(((TurnEndEvent) event).turn.getPlayer().getUserID())) {
            String boardState = this.boardManager.getBoardState();
            LogUtil.i("fr.aboucorp.variantchess", String.format("New state send in matchData : %s", boardState));
            this.sessionManager.sendData(boardState, this.chessMatch.getMatchId(), OPCode.SEND_NEW_FEN);
        }
    }

    public void playOppositeMove(String boardState) {
        Turn opposantTurn = this.boardManager.playTheOpposantMove(boardState);
        if (this.turnManager.getTurnColor() == ChessColor.WHITE) {
            opposantTurn.setPlayer(this.chessMatch.whitePlayer);
        } else {
            opposantTurn.setPlayer(this.chessMatch.blackPlayer);
        }
        this.turnManager.appendTurn(opposantTurn);
        this.boardManager.stopWaitForNextTurn();
        this.turnManager.startTurn();
    }
}
