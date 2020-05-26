package fr.aboucorp.variantchess.libgdx.utils;

import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

import fr.aboucorp.variantchess.entities.Location;
import fr.aboucorp.variantchess.entities.enums.PieceId;
import fr.aboucorp.variantchess.libgdx.models.GraphicsGameElement;

public class GraphicGameArray extends Array<GraphicsGameElement> {


    public GraphicsGameElement getByLocation(Location location) {
        for (Iterator<GraphicsGameElement> iter = this.iterator(); iter.hasNext(); ) {
            GraphicsGameElement model = iter.next();
            if (model.getLocation().equals(location)) {
                return model;
            }
        }
        return null;
    }

    public GraphicsGameElement removeByLocation(Location location) {
        GraphicsGameElement removed;
        for (Iterator<GraphicsGameElement> iter = this.iterator(); iter.hasNext(); ) {
            GraphicsGameElement model = iter.next();
            if (model.getLocation().equals(location)) {
                removed = model;
                iter.remove();
                return removed;
            }
        }
        return null;
    }


    public GraphicsGameElement getElementById(PieceId pieceId) {
        for (Iterator<GraphicsGameElement> iter = this.iterator(); iter.hasNext(); ) {
            GraphicsGameElement model = iter.next();
            if (model.getId() != null && model.getId().equals(pieceId)) {
                return model;
            }
        }
        return null;
    }
}
