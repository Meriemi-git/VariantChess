package fr.aboucorp.teamchess.entities.model.events;

import fr.aboucorp.teamchess.entities.model.Turn;

public class TurnEvent extends GameEvent {
    public final Turn turn;
    public TurnEvent(String eventMessage,Turn turn) {
        super(eventMessage);
        this.turn = turn;
    }
}
