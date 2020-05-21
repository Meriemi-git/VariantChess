package fr.aboucorp.variantchess.libgdx.models;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import fr.aboucorp.variantchess.entities.Location;


public abstract class ChessModel extends ModelInstance {
    private Location location;
    private Material originalMaterial;
    private BoundingBox boundingBox = new BoundingBox();
    private Vector3 center = new Vector3();
    private Vector3 dimensions = new Vector3();

    public ChessModel(Model model, Location location, Material originalMaterial) {
        super(model,location.getX(),location.getY(),location.getZ());
        this.originalMaterial = originalMaterial;
        this.location = location.clone();
        calculateBoundingBox(boundingBox);
        this.boundingBox.getCenter(center);
        this.boundingBox.getDimensions(dimensions);
    }

    public void move(Location location) {
        this.location = location;
        this.transform.setTranslation(new Vector3(location.getX(),location.getY(),location.getZ()));
    }

    public boolean isVisible(final Camera cam) {
        Vector3 position = new Vector3();
        transform.getTranslation(position);
        position.add(this.center);
        return cam.frustum.boundsInFrustum(position,this.dimensions);
    }


    public Material getOriginalMaterial() {
        return originalMaterial;
    }

    public Location getLocation() {
        return location;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    public Vector3 getCenter() {
        return center;
    }

    public Vector3 getDimensions() {
        return dimensions;
    }
}
