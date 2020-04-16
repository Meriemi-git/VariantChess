package fr.aboucorp.teamchess.libgdx.models;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;

import fr.aboucorp.entities.model.Location;

public class ChessPieceModel extends ChessModel {

    public ChessPieceModel(Model model, Location location, Material originalMaterial) {
        super(model, location, originalMaterial);
    }
}
