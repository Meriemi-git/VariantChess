package fr.aboucorp.teamchess.entities.model.utils;

import fr.aboucorp.teamchess.entities.model.Square;

public class SquareList extends ChessList<Square> {
    public Square getSquareByLabel(String label){
        for(Square square : this){
            if(square.getSquareLabel().toLowerCase().equals(label.toLowerCase())){
                return square;
            }
        }
        return null;
    }
}
