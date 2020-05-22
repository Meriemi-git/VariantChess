package fr.aboucorp.variantchess.entities;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Party {
    LinkedList<String> fenMoves = new LinkedList<>();

    public LinkedList<String> getFenMoves() {
        return fenMoves;
    }

    public void setFenMoves(LinkedList<String> fenMoves) {
        this.fenMoves = fenMoves;
    }
}
