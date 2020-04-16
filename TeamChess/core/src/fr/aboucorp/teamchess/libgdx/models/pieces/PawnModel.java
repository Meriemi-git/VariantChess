package fr.aboucorp.teamchess.libgdx.models.pieces;


import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;

import fr.aboucorp.entities.model.Location;
import fr.aboucorp.teamchess.libgdx.models.ChessPieceModel;

public class PawnModel extends ChessPieceModel {
    public PawnModel(Model model, Location location, Material originalMaterial) {
        super(model, location, originalMaterial);
    }
}