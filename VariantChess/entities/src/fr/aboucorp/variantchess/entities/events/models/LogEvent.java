package fr.aboucorp.variantchess.entities.events.models;

import java.io.Serializable;

import fr.aboucorp.variantchess.entities.enums.EventType;

public class LogEvent extends GameEvent implements Serializable {
    public LogEvent(String eventMessage) {
        super(eventMessage, EventType.LOG);
    }
}
