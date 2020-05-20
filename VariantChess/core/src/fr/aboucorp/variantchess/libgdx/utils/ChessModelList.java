package fr.aboucorp.variantchess.libgdx.utils;

import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Iterator;

import fr.aboucorp.variantchess.entities.Location;
import fr.aboucorp.variantchess.libgdx.models.ChessModel;

public class ChessModelList extends Array<ChessModel> {


    public ChessModel getByLocation(Location location) {
        for (Iterator<ChessModel> iter = this.iterator(); iter.hasNext();){
            ChessModel model = iter.next();
            if(model.getLocation().equals(location)){
               return  model;
            }
        }
        return null;
    }

    public ChessModel removeByLocation(Location location) {
        ChessModel removed;
        for (Iterator<ChessModel> iter = this.iterator(); iter.hasNext();){
            ChessModel model = iter.next();
            if(model.getLocation().equals(location)){
                removed = model;
                iter.remove();
                return removed;
            }
        }
        return null;
    }


}
