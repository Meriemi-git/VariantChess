package fr.aboucorp.teamchess.libgdx.models;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

import fr.aboucorp.generic.model.Location;
import fr.aboucorp.generic.model.enums.Color;

public class ChessModel extends ModelInstance {
    protected Color color;
    protected Location location;

    public ChessModel(Model model, Location location, Color color) {
        super(model,location.x,location.y,location.z);
        this.color = color;
        this.location = location;
    }

    public boolean isLocatedIn(int x, int y, int z){
        return x == location.x && y == location.y && z == location.z;
    }

}
