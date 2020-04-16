package fr.aboucorp.teamchess.libgdx.utils;

import java.util.ArrayList;
import java.util.Iterator;

import fr.aboucorp.entities.model.Location;
import fr.aboucorp.teamchess.libgdx.models.ChessModel;

public class ChessModelList extends ArrayList<ChessModel> {


    public ChessModel getByLocation(Location location) {
        for (Iterator<ChessModel> iter = this.iterator(); iter.hasNext();){
            ChessModel piece = iter.next();
            if(piece.getLocation().equals(location)){
               return  piece;
            }
        }
        return null;
    }
}
