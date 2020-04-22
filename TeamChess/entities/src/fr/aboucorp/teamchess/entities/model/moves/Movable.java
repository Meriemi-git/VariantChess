package fr.aboucorp.teamchess.entities.model.moves;

import fr.aboucorp.teamchess.entities.model.ChessCell;


public interface Movable {

    void move(ChessCell cell);
}
