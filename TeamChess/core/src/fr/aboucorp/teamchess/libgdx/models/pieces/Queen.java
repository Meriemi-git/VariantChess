package fr.aboucorp.teamchess.libgdx.models.pieces;

import com.badlogic.gdx.graphics.g3d.Model;

import fr.aboucorp.teamchess.libgdx.models.ChessCell;
import fr.aboucorp.teamchess.libgdx.models.ChessPiece;
import fr.aboucorp.generic.model.enums.Color;

public class Queen extends ChessPiece {

    public Queen(Model model, ChessCell cell, Color color) {
        super(model, cell,color);
    }

}
