package fr.aboucorp.entities.model.pieces;

import fr.aboucorp.entities.model.ChessColor;
import fr.aboucorp.entities.model.ChessPiece;
import fr.aboucorp.entities.model.Location;

public class Pawn extends ChessPiece {

    public Pawn(Location location, ChessColor chessColor) {
        super(location, chessColor);
    }
}
