package fr.aboucorp.variantchess.entities.events.models;

import java.io.Serializable;

import fr.aboucorp.variantchess.entities.enums.PieceId;

public class PieceEvent extends BoardEvent implements Serializable {
    public PieceId played;

    public PieceEvent(String eventMessage, int boardEventType, PieceId played) {
        super(eventMessage, boardEventType);
        this.played = played;
    }
}
