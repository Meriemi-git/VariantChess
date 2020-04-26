package fr.aboucorp.teamchess.entities.model.utils;

import fr.aboucorp.teamchess.entities.model.Square;

public class ChessCellList extends ChessList<Square> {
    public Square getChessCellByLabel(String label){
        for(Square cell : this){
            if(cell.getCellLabel().toLowerCase().equals(label.toLowerCase())){
                return cell;
            }
        }
        return null;
    }
}
