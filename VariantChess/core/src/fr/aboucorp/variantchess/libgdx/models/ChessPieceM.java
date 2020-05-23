package fr.aboucorp.variantchess.libgdx.models;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;

import fr.aboucorp.variantchess.entities.Location;
import fr.aboucorp.variantchess.entities.enums.PieceId;

public class ChessPieceM extends ChessModel {
    public final PieceId id;
    protected ChessPieceM(Model model, Location location, Material originalMaterial, PieceId id) {
        super(model, location, originalMaterial);
        this.materials.get(0).set(originalMaterial);
        this.id = id;
    }
}
