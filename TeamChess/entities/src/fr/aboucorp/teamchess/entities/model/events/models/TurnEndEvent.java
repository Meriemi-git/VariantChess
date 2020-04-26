package fr.aboucorp.teamchess.entities.model.events.models;

import fr.aboucorp.teamchess.entities.model.Turn;

public class TurnEndEvent extends TurnEvent {
    public TurnEndEvent(String eventMessage, Turn turn) {
        super(eventMessage, turn);
    }
}
