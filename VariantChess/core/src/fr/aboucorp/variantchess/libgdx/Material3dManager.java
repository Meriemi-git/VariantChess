package fr.aboucorp.variantchess.libgdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

import fr.aboucorp.variantchess.entities.enums.PieceId;
import fr.aboucorp.variantchess.libgdx.models.ChessModel;
import fr.aboucorp.variantchess.libgdx.models.GraphicsGameElement;

class Material3dManager {

    private Material selectedPieceMaterial;
    private Material occupiedMaterial;
    private TextureAtlas piecesAtlas;


    public Material3dManager() {
        this.selectedPieceMaterial = new Material();
        this.selectedPieceMaterial.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 0.8f));
        this.selectedPieceMaterial.set(ColorAttribute.createDiffuse(Color.ROYAL));

        this.occupiedMaterial = new Material();
        this.occupiedMaterial.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 0.8f));
        this.occupiedMaterial.set(ColorAttribute.createDiffuse(Color.RED));
    }

    public String getRegionNameFromPieceId(PieceId id, boolean selection) {
        String regionName;
        switch (id) {
            case WK:
                regionName = "wk";
                break;
            case WQ:
                regionName = "wq";
                break;
            case WLN:
                regionName = "wrn";
                break;
            case WRN:
                regionName = "wln";
                break;
            case WRR:
            case WLR:
                regionName = "wr";
                break;
            case WRB:
            case WLB:
                regionName = "wb";
                break;
            case BK:
                regionName = "bk";
                break;
            case BQ:
                regionName = "bq";
                break;
            case BLN:
                regionName = "brn";
                break;
            case BRN:
                regionName = "bln";
                break;
            case BRR:
            case BLR:
                regionName = "br";
                break;
            case BRB:
            case BLB:
                regionName = "bb";
                break;
            case BP1:
            case BP2:
            case BP3:
            case BP4:
            case BP5:
            case BP6:
            case BP7:
            case BP8:
                regionName = "bp";
                break;
            default:
                regionName = "wp";
        }
        if (selection) {
            regionName += "_selected";
        }
        return regionName;
    }

    public void resetMaterial(GraphicsGameElement element) {
        if (element.getModel2d() != null) {
            element.setModel2d(this.getSpriteById(element.getPieceId(), false));
        }
        if (element.getModel3d() != null) {
            Material oldMat = element.getModel3d().materials.get(0);
            oldMat.clear();
            oldMat.set(element.getModel3d().getOriginalMaterial());
        }

    }

    private void set3DMaterial(ChessModel model, Material material) {
        Material actualMaterial = model.materials.get(0);
        actualMaterial.clear();
        actualMaterial.set(material);
    }

    public void setOccupiedMaterial(GraphicsGameElement element) {
        if (element.getModel3d() != null) {
            this.set3DMaterial(element.getModel3d(), this.occupiedMaterial);
        }
    }

    public void setSelectedMaterial(GraphicsGameElement element) {
        if (element.getModel2d() != null) {
            element.setModel2d(this.getSpriteById(element.getPieceId(), true));
        }
        if (element.getModel3d() != null) {
            this.set3DMaterial(element.getModel3d(), this.selectedPieceMaterial);
        }
    }

    public TextureAtlas getPiecesAtlas() {
        return this.piecesAtlas;
    }

    public void setPiecesAtlas(TextureAtlas piecesAtlas) {
        this.piecesAtlas = piecesAtlas;
    }


    public Sprite getSpriteById(PieceId id, boolean selection) {
        TextureAtlas.AtlasRegion region = this.piecesAtlas.findRegion(this.getRegionNameFromPieceId(id, selection));
        return new Sprite(region);
    }
}
