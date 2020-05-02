package fr.aboucorp.teamchess.libgdx.models.pieces;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;

import fr.aboucorp.teamchess.entities.model.Location;
import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.libgdx.models.ChessPieceM;

public class KnightM extends ChessPieceM {
    public KnightM(Model model, Location location, Material originalMaterial, PieceId id) {
        super(model, location, originalMaterial, id);
    }
}
