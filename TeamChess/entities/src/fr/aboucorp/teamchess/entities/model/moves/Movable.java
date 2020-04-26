package fr.aboucorp.teamchess.entities.model.moves;

import fr.aboucorp.teamchess.entities.model.Square;


public interface Movable {

    void move(Square cell);
}
