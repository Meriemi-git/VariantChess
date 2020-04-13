package fr.aboucorp.teamchess.libgdx.models.pieces.impl;

import com.badlogic.gdx.graphics.g3d.Model;

import fr.aboucorp.generic.model.enums.Color;
import fr.aboucorp.teamchess.libgdx.models.ChessCell;
import fr.aboucorp.teamchess.libgdx.models.ChessPiece;
import fr.aboucorp.teamchess.libgdx.models.pieces.IBishop;

public class Bishop extends ChessPiece implements IBishop {

    public Bishop(Model model, ChessCell cell, Color color) {
        super(model,cell,color);
    }

}
