package fr.aboucorp.variantchess.libgdx.models;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Sprite;

import fr.aboucorp.variantchess.entities.Location;
import fr.aboucorp.variantchess.entities.enums.PieceId;

public class GraphicsGameElement {
    private final PieceId id;
    private ChessModel model3d;
    private Sprite model2d;
    private Location location;

    public GraphicsGameElement(Location location, PieceId id) {
        this.location = location;
        this.id = id;
    }

    public ChessModel getModel3d() {
        return this.model3d;
    }

    public void setModel3d(ChessModel model3d) {
        this.model3d = model3d;
    }

    public Sprite getModel2d() {
        return this.model2d;
    }

    public void setModel2d(Sprite model2d) {
        this.model2d = model2d;
    }

    public boolean isVisible(Camera camera, boolean isTacticalView) {
        // TODO
        return true;
    }

    public Location getLocation() {
        return this.location;
    }

    public void move2D(Location location) {
        this.location = location;
    }

    public void move3D(Location location) {
        this.model3d.move(location);
        this.location = location;
    }

    public PieceId getId() {
        return this.id;
    }
}

