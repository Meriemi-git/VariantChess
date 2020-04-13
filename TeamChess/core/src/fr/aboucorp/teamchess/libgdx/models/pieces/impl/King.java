package fr.aboucorp.teamchess.libgdx.models.pieces.impl;

import com.badlogic.gdx.graphics.g3d.Model;

import fr.aboucorp.generic.model.enums.Color;
import fr.aboucorp.teamchess.libgdx.models.ChessCell;
import fr.aboucorp.teamchess.libgdx.models.ChessPiece;
import fr.aboucorp.teamchess.libgdx.models.pieces.IKing;


public class King extends ChessPiece implements IKing {

    public King(Model model, ChessCell cell, Color color) {
        super(model, cell,color);
    }

}
