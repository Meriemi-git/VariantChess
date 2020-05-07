package fr.aboucorp.variantchess.libgdx.models;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;

import fr.aboucorp.variantchess.entities.Location;

public class ChessSquareM extends ChessModel {
    private final String label;
    public ChessSquareM(Model model, Location location, Material originalMaterial, String label) {
        super(model, location, originalMaterial);
        this.materials.get(0).set(originalMaterial);
        this.materials.get(1).set(originalMaterial);
        this.label = label;
    }
    public String getLabel() {
        return label;
    }
}
