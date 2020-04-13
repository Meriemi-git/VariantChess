package fr.aboucorp.teamchess.libgdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

import fr.aboucorp.teamchess.libgdx.models.ChessModel;

public class MaterialManager {
    private Material selectedPieceMaterial;

    public MaterialManager() {
        this.selectedPieceMaterial = new Material();
        this.selectedPieceMaterial.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 0.8f));
        this.selectedPieceMaterial.set(ColorAttribute.createDiffuse(Color.ROYAL));

    }

    public void resetMaterial(ChessModel piece) {
        Material oldMat = piece.materials.get(0);
        oldMat.clear();
        oldMat.set(piece.getOriginalMaterial());
    }

    public void setSelectedMaterial(ChessModel piece) {
        Material actualMaterial = piece.materials.get(0);
        actualMaterial.clear();
        actualMaterial.set(selectedPieceMaterial);
    }
}
