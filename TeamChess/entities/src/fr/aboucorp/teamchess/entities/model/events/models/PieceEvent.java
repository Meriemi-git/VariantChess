package fr.aboucorp.teamchess.entities.model.events.models;

import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.enums.BoardEventType;

public class PieceEvent extends BoardEvent {
    public Piece piece;
    public PieceEvent(String eventMessage, BoardEventType type, Piece piece) {
        super(eventMessage, type);
        this.piece = piece;
    }
}
