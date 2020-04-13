package fr.aboucorp.teamchess.libgdx.models;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;

import fr.aboucorp.generic.model.Cell;
import fr.aboucorp.generic.model.IPiece;
import fr.aboucorp.generic.model.enums.Color;

public abstract class ChessPiece extends ChessModel implements IPiece {

    private ChessCell cell;

    public ChessPiece(Model model, ChessCell cell, Color color){
        super(model,cell.location,color);
        this.cell = cell;
        cell.setPiece(this);
    }

    @Override
    public void move(Cell cell) {
        ((ChessCell)this.cell).setPiece(null);
        ((ChessCell)cell).setPiece(this);
        this.cell = (ChessCell) cell;
        this.transform.setTranslation(((ChessCell)cell).getBoundingBox().getCenter(new Vector3()));
    }

    public ChessCell getCell() {
        return cell;
    }
}
