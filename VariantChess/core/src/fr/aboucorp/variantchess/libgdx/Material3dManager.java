package fr.aboucorp.variantchess.libgdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

import fr.aboucorp.variantchess.entities.enums.PieceId;
import fr.aboucorp.variantchess.libgdx.models.ChessModel;
import fr.aboucorp.variantchess.libgdx.models.GraphicsGameElement;

class Material3dManager {
    private Material selectedPieceMaterial;
    private Material occupiedMaterial;

    public Material3dManager() {
        this.selectedPieceMaterial = new Material();
        this.selectedPieceMaterial.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 0.8f));
        this.selectedPieceMaterial.set(ColorAttribute.createDiffuse(Color.ROYAL));

        this.occupiedMaterial = new Material();
        this.occupiedMaterial.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 0.8f));
        this.occupiedMaterial.set(ColorAttribute.createDiffuse(Color.RED));
    }


    public void resetMaterial(GraphicsGameElement element, boolean isTactical) {
        if (isTactical) {
            // TODO
        } else {
            Material oldMat = element.getModel3d().materials.get(0);
            oldMat.clear();
            oldMat.set(element.getModel3d().getOriginalMaterial());
        }

    }

    public void setSelectedMaterial(GraphicsGameElement element, boolean isTactical) {
        if (isTactical) {
            // TODO
        } else {
            this.set3DMaterial(element.getModel3d(), this.selectedPieceMaterial);
        }

    }

    private void set3DMaterial(ChessModel model, Material material) {
        Material actualMaterial = model.materials.get(0);
        actualMaterial.clear();
        actualMaterial.set(material);
    }

    public void setOccupiedMaterial(GraphicsGameElement element, boolean isTactical) {
        if (isTactical) {
            // TODO
        } else {
            this.set3DMaterial(element.getModel3d(), this.occupiedMaterial);
        }
    }

    public String getRegionNameFromPieceId(PieceId id) {
        switch (id) {
            case WK:
                return "wk";
            case WQ:
                return "wq";
            case WLN:
                return "wrn";
            case WRN:
                return "wln";
            case WRR:
            case WLR:
                return "wr";
            case WRB:
            case WLB:
                return "wb";
            case WP1:
            case WP2:
            case WP3:
            case WP4:
            case WP5:
            case WP6:
            case WP7:
            case WP8:
                return "wp";
            case BK:
                return "bk";
            case BQ:
                return "bq";
            case BLN:
                return "brn";
            case BRN:
                return "bln";
            case BRR:
            case BLR:
                return "br";
            case BRB:
            case BLB:
                return "bb";
            case BP1:
            case BP2:
            case BP3:
            case BP4:
            case BP5:
            case BP6:
            case BP7:
            case BP8:
                return "bp";
            default:
                return "wp";
        }
    }

}
