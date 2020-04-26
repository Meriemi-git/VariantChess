package fr.aboucorp.teamchess.entities.model.events.models;

import fr.aboucorp.teamchess.entities.model.Turn;

public class TurnStartEvent extends TurnEvent {
    public TurnStartEvent(String eventMessage, Turn turn) {
        super(eventMessage, turn);
    }
}
