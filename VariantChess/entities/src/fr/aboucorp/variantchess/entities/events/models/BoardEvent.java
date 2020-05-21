package fr.aboucorp.variantchess.entities.events.models;

import fr.aboucorp.variantchess.entities.enums.BoardEventType;

public class BoardEvent extends GameEvent {
    public final BoardEventType type;

    public BoardEvent(String eventMessage, BoardEventType type) {
        super(eventMessage);
        this.type = type;
    }
}
