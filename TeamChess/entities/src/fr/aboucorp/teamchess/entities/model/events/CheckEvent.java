package fr.aboucorp.teamchess.entities.model.events;

import fr.aboucorp.teamchess.entities.model.ChessColor;

public class CheckEvent extends GameEvent{
    private final ChessColor color;

    public CheckEvent(String eventMessage, ChessColor color) {
        super(eventMessage);
        this.color = color;
    }
}
