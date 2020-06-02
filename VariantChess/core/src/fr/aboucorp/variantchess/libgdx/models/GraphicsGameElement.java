package fr.aboucorp.variantchess.libgdx.models;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Sprite;

import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.Location;
import fr.aboucorp.variantchess.entities.enums.PieceId;

public class GraphicsGameElement {
    private final PieceId id;
    private final ChessColor color;
    private ChessModel model3d;
    private Sprite model2d;
    private Location location;
    private boolean useShader;

    public GraphicsGameElement(Location location, PieceId id, ChessColor color) {
        this.location = location;
        this.id = id;
        this.color = color;
    }

    public ChessColor getColor() {
        return this.color;
    }

    public PieceId getPieceId() {
        return this.id;
    }

    public Location getLocation() {
        return this.location;
    }

    public Sprite getModel2d() {
        return this.model2d;
    }

    public void setModel2d(Sprite model2d) {
        this.model2d = model2d;
    }

    public ChessModel getModel3d() {
        return this.model3d;
    }

    public void setModel3d(ChessModel model3d) {
        this.model3d = model3d;
    }

    public boolean isUseShader() {
        return this.useShader;
    }

    public void setUseShader(boolean useShader) {
        this.useShader = useShader;
    }

    public boolean isVisible(Camera camera, boolean isTacticalView) {
        // TODO
        return true;
    }

    public void move(Location location) {
        this.location = location;
        if (this.model3d != null) {
            this.model3d.move(location);
        }
    }
}

