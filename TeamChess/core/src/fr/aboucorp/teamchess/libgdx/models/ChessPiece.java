package fr.aboucorp.teamchess.libgdx.models;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;

import fr.aboucorp.generic.model.Cell;
import fr.aboucorp.generic.model.IPiece;
import fr.aboucorp.generic.model.enums.Color;

public abstract class ChessPiece extends ChessModel implements IPiece {

    public ChessCell cell;


    public ChessPiece(Model model, ChessCell cell, Color color){
        super(model,cell.location,color);
        this.cell = cell;
    }

    @Override
    public void move(Cell cell) {
        Vector3 coordinates = new Vector3();
        coordinates.x = cell.location.x;
        coordinates.y = cell.location.y;
        coordinates.z = cell.location.z;
        this.transform.translate(coordinates);
    }

}
