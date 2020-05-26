package fr.aboucorp.variantchess.app.managers;


import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.Match;
import fr.aboucorp.variantchess.entities.PartyLifeCycle;
import fr.aboucorp.variantchess.entities.Player;
import fr.aboucorp.variantchess.entities.Turn;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.events.models.MoveEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnEndEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnStartEvent;

public class TurnManager implements PartyLifeCycle {

    private GameEventManager gameEventManager;
    private Player whitePlayer;
    private Player blackPlayer;
    private Match match;
    private Turn current;

    public TurnManager(GameEventManager gameEventManager) {
        this.gameEventManager = gameEventManager;
    }


    public void endTurn(MoveEvent event, String fenFromBoard) {
        this.current.setFen(fenFromBoard);
        if (event != null) {
            this.current.setPlayed(event.played);
            this.current.setTo(event.to);
            this.current.setFrom(event.from);
            this.current.setDeadPiece(event.deadPiece);
        }
        this.match.getTurns().add(this.current);
        this.gameEventManager.sendMessage(new TurnEndEvent("Ending turn", this.match.getTurns().getLast()));
    }


    public ChessColor getTurnColor() {
        return this.current.getTurnColor();
    }

    @Override
    public void startParty(Match match) {
        this.match = match;
        this.whitePlayer = match.getWhitePlayer();
        this.blackPlayer = match.getBlackPlayer();
        startTurn();
    }

    public void startTurn() {
        Turn nextTurn;
        Player player = null;
        if (this.match.getTurns().size() > 0 && this.match.getTurns().getLast().getTurnColor() == ChessColor.WHITE) {
            player = this.blackPlayer;
        } else {
            player = this.whitePlayer;
        }
        nextTurn = new Turn(this.match.getTurns().size() + 1, player);
        this.current = nextTurn;
        String eventMessage = String.format("Turn %s, color : %s", nextTurn.getTurnNumber(), nextTurn.getTurnColor());
        this.gameEventManager.sendMessage(new TurnStartEvent(eventMessage, nextTurn));
    }

    @Override
    public void stopParty() {
        this.match = null;
    }

}
