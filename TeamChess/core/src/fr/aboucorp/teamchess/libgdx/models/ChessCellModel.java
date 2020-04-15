package fr.aboucorp.teamchess.libgdx.models;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;

import fr.aboucorp.entities.model.ChessCell;
import fr.aboucorp.entities.model.Location;

public class ChessCellModel extends ChessModel {
    private ChessCell cell;
    public ChessCellModel(Model model, Location location, Material originalMaterial,ChessCell cell) {
        super(model, location, originalMaterial);
        this.cell = cell;
    }

    public ChessCell getCell() {
        return cell;
    }

}
