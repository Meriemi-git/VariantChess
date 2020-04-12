package fr.aboucorp.generic.model;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private List<List<Cell>> cells;

    public Board(){
        this.cells = new ArrayList<List<Cell>>();
    }
}
