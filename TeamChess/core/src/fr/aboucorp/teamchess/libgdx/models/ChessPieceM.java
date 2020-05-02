package fr.aboucorp.teamchess.libgdx.models;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;

import fr.aboucorp.teamchess.entities.model.Location;
import fr.aboucorp.teamchess.entities.model.enums.PieceId;

public class ChessPieceM extends ChessModel {
    public final PieceId id;
    public ChessPieceM(Model model, Location location, Material originalMaterial, PieceId id) {
        super(model, location, originalMaterial);
        this.materials.get(0).set(originalMaterial);
        this.id = id;
    }
}
