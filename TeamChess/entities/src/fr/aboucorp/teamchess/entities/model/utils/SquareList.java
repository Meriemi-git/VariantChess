package fr.aboucorp.teamchess.entities.model.utils;

import java.util.ArrayList;
import java.util.List;

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

    public List<Square> getSquaresByLine(int lineNumber){
        List<Square> squares = new ArrayList<>();
        for(Square square : this){
            if(square.getLocation().getZ() == lineNumber){
                squares.add(square);
            }
        }
        return squares;
    }
}
