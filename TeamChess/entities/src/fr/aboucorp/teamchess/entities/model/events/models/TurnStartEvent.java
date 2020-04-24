package fr.aboucorp.teamchess.entities.model.events.models;

import fr.aboucorp.teamchess.entities.model.ChessTurn;

public class TurnStartEvent extends TurnEvent {
    public TurnStartEvent(String eventMessage, ChessTurn turn) {
        super(eventMessage, turn);
    }
}
