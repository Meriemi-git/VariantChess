package fr.aboucorp.variantchess.entities.events.models;

import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.enums.BoardEventType;
import fr.aboucorp.variantchess.entities.enums.PieceId;

public class PieceEvent extends BoardEvent {
    public PieceId played;
    public PieceEvent(String eventMessage, BoardEventType type, PieceId played) {
        super(eventMessage, type);
        this.played = played;
    }
}
