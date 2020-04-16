package fr.aboucorp.teamchess.libgdx.models;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import fr.aboucorp.entities.model.Location;


public abstract class ChessModel extends ModelInstance {
    private Location location;
    private Material originalMaterial;
    private BoundingBox boundingBox;

    public ChessModel(Model model, Location location, Material originalMaterial) {
        super(model,location.getX(),location.getY(),location.getZ());
        this.originalMaterial = originalMaterial;
        this.materials.get(0).set(originalMaterial);
        this.location = location;
        this.boundingBox = calculateBoundingBox(new BoundingBox()).mul(transform);
    }

    public void move(Location location) {
        this.location = location;
        this.transform.setTranslation(new Vector3(location.getX(),location.getY(),location.getZ()));
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
}
