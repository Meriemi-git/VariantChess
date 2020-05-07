package fr.aboucorp.variantchess.libgdx.models.pieces;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;

import fr.aboucorp.variantchess.entities.Location;
import fr.aboucorp.variantchess.entities.enums.PieceId;
import fr.aboucorp.variantchess.libgdx.models.ChessPieceM;

public class KingM extends ChessPieceM {
    public KingM(Model model, Location location, Material originalMaterial, PieceId id) {
        super(model, location, originalMaterial, id);
    }
}
