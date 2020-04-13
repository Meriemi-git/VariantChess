package fr.aboucorp.teamchess.libgdx.models.pieces.impl;

import com.badlogic.gdx.graphics.g3d.Model;

import java.util.List;

import fr.aboucorp.generic.model.Board;
import fr.aboucorp.generic.model.Cell;
import fr.aboucorp.generic.model.IPiece;
import fr.aboucorp.generic.model.enums.Color;
import fr.aboucorp.teamchess.libgdx.models.ChessCell;
import fr.aboucorp.teamchess.libgdx.models.ChessPiece;

public class Pawn extends ChessPiece {

    public Pawn(Model model, ChessCell cell, Color color) {
        super(model, cell,color);
    }

    @Override
    public List<Cell> getPossibleMoves(IPiece piece, Board board) {
        return null;
    }
}
