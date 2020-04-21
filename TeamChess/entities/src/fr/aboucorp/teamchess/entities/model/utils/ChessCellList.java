package fr.aboucorp.teamchess.entities.model.utils;

import fr.aboucorp.teamchess.entities.model.ChessCell;

public class ChessCellList extends ChessList<ChessCell> {
    public ChessCell getChessCellByLabel(String label){
        for(ChessCell cell : this){
            if(cell.getCellLabel().toLowerCase().equals(label.toLowerCase())){
                return cell;
            }
        }
        return null;
    }
}
