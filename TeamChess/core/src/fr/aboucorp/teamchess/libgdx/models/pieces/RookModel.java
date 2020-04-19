package fr.aboucorp.teamchess.libgdx.models.pieces;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;

import fr.aboucorp.teamchess.entities.model.Location;
import fr.aboucorp.teamchess.libgdx.models.ChessPieceModel;

public class RookModel extends ChessPieceModel {
    public RookModel(Model model, Location location, Material originalMaterial) {
        super(model, location, originalMaterial);
    }
}
