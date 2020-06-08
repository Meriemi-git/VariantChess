package fr.aboucorp.variantchess.entities.events.models;

import java.io.Serializable;

import fr.aboucorp.variantchess.entities.enums.PieceId;

public class CheckOutEvent extends PieceEvent implements Serializable {
    public CheckOutEvent(String eventMessage, int boardEventType, PieceId played) {
        super(eventMessage, boardEventType, played);
    }
}
