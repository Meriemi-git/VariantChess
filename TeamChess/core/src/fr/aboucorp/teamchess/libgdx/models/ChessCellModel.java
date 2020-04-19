package fr.aboucorp.teamchess.libgdx.models;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;

import fr.aboucorp.teamchess.entities.model.Location;

public class ChessCellModel extends ChessModel {
    private final String label;
    public ChessCellModel(Model model, Location location, Material originalMaterial,String label) {
        super(model, location, originalMaterial);
        this.label = label;
    }
    public String getLabel() {
        return label;
    }
}
