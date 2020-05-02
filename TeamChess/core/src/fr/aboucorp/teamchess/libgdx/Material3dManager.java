package fr.aboucorp.teamchess.libgdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

import fr.aboucorp.teamchess.libgdx.models.ChessModel;

public class Material3dManager {
    private static Material3dManager INSTANCE;
    private Material selectedPieceMaterial;
    private Material occupiedMaterial;
    public boolean picturesLoading;


    private Material3dManager() {
        this.selectedPieceMaterial = new Material();
        this.selectedPieceMaterial.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 0.8f));
        this.selectedPieceMaterial.set(ColorAttribute.createDiffuse(Color.ROYAL));

        this.occupiedMaterial = new Material();
        this.occupiedMaterial.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 0.8f));
        this.occupiedMaterial.set(ColorAttribute.createDiffuse(Color.RED));
    }


    public void resetMaterial(ChessModel piece) {
        Material oldMat = piece.materials.get(0);
        oldMat.clear();
        oldMat.set(piece.getOriginalMaterial());
    }

    public void setSelectedMaterial(ChessModel model) {
        setMaterial(model,this.selectedPieceMaterial);
    }

    public static Material3dManager getInstance(){
        if(INSTANCE == null){
            INSTANCE = new Material3dManager();
        }
        return INSTANCE;
    }

    private void setMaterial(ChessModel model, Material material){
        Material actualMaterial = model.materials.get(0);
        actualMaterial.clear();
        actualMaterial.set(material);
    }

    public void setOccupiedMaterial(ChessModel model) {
        setMaterial(model,this.occupiedMaterial);
    }

}
