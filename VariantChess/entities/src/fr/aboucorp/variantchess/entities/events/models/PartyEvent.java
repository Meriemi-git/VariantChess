package fr.aboucorp.variantchess.entities.events.models;

import java.io.Serializable;

public class PartyEvent extends GameEvent implements Serializable {
    public PartyEvent(String eventMessage, int boardEventType) {
        super(eventMessage, boardEventType);
    }
}
