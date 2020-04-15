package fr.aboucorp.teamchess.libgdx.utils;

import java.util.ArrayList;
import java.util.Iterator;

import fr.aboucorp.entities.model.ChessColor;
import fr.aboucorp.entities.model.Location;
import fr.aboucorp.teamchess.libgdx.models.ChessModel;

public class ChessModelList extends ArrayList<ChessModel> {

    public ArrayList<ChessModel> filterByColor(ChessColor color){
        ArrayList<ChessModel> filteredPieces = new ArrayList<>();
        for (Iterator<ChessModel> iter = this.iterator(); iter.hasNext();){
            ChessModel piece = iter.next();
            if(piece.getGameElement().getChessColor() == color){
                filteredPieces.add(piece);
            }
        }
        return filteredPieces;
    }

    public ChessModel getByOriginalLocation(Location location) {
        for (Iterator<ChessModel> iter = this.iterator(); iter.hasNext();){
            ChessModel piece = iter.next();
            if(piece.getLocation().equals(location)){
               return  piece;
            }
        }
        return null;
    }
}
