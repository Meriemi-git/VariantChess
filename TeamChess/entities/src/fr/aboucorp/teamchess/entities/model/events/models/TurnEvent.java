package fr.aboucorp.teamchess.entities.model.events.models;

import fr.aboucorp.teamchess.entities.model.ChessTurn;

public abstract class TurnEvent extends GameEvent {
    public final ChessTurn turn;

    public TurnEvent(String eventMessage, ChessTurn turn) {
        super(eventMessage);
        this.turn = turn;
    }
}
