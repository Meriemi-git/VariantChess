package fr.aboucorp.teamchess.entities.model.events.models;

import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.Square;
import fr.aboucorp.teamchess.entities.model.enums.BoardEventType;

public class CastlingEvent extends PieceEvent {
    public final Square detination;
    public CastlingEvent(String eventMessage, BoardEventType type, Piece piece, Square detination) {
        super(eventMessage, type, piece);
        this.detination = detination;
    }
}
