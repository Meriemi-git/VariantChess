package fr.aboucorp.variantchess.entities.events.models;

import java.io.Serializable;

public class BoardEvent extends GameEvent implements Serializable {

    public BoardEvent(String eventMessage, int boardEventType) {
        super(eventMessage, boardEventType);
    }
}
