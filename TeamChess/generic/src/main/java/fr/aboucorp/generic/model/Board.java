package fr.aboucorp.generic.model;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private List<List<Cell>> squares;

    public Board(){
        this.squares = new ArrayList<List<Cell>>();
    }
}
