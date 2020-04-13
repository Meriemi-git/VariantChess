package fr.aboucorp.generic.model;

import java.util.List;

public interface IPiece {
    void move(Cell cell);
    List<Cell> getPossibleMoves(IPiece piece,Board board);

}
