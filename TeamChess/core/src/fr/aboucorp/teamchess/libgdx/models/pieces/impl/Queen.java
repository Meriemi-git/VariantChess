package fr.aboucorp.teamchess.libgdx.models.pieces.impl;

import com.badlogic.gdx.graphics.g3d.Model;

import fr.aboucorp.generic.model.enums.Color;
import fr.aboucorp.teamchess.libgdx.models.ChessCell;
import fr.aboucorp.teamchess.libgdx.models.ChessPiece;
import fr.aboucorp.teamchess.libgdx.models.pieces.IBishop;
import fr.aboucorp.teamchess.libgdx.models.pieces.IRook;

public class Queen extends ChessPiece implements IRook, IBishop {

    public Queen(Model model, ChessCell cell, Color color) {
        super(model, cell,color);
    }


}
