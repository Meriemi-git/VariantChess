package fr.aboucorp.teamchess.entities.model.events;

import fr.aboucorp.teamchess.entities.model.ChessTurn;

public class TurnEndEvent extends TurnEvent {
    public TurnEndEvent(String eventMessage, ChessTurn turn) {
        super(eventMessage, turn);
    }
}
