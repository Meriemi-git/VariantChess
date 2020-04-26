package fr.aboucorp.teamchess.entities.model.events.models;

import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.enums.BoardEventType;

public class CheckOutEvent extends PieceEvent {
    public CheckOutEvent(String eventMessage, BoardEventType type, Piece piece) {
        super(eventMessage, type, piece);
    }
}
