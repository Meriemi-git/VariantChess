package fr.aboucorp.teamchess.entities.model.events.models;

import fr.aboucorp.teamchess.entities.model.enums.BoardEventType;

public class BoardEvent extends GameEvent {
    public final BoardEventType type;

    public BoardEvent(String eventMessage, BoardEventType type) {
        super(eventMessage);
        this.type = type;
    }
}
