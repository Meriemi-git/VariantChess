package fr.aboucorp.teamchess.libgdx.models.pieces;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;

import fr.aboucorp.teamchess.entities.model.Location;
import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.libgdx.models.ChessPieceM;

public class RookM extends ChessPieceM {
    public RookM(Model model, Location location, Material originalMaterial, PieceId id) {
        super(model, location, originalMaterial, id);
    }
}
