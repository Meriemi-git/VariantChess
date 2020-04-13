package fr.aboucorp.teamchess.libgdx.models;


import com.badlogic.gdx.graphics.g3d.ModelInstance;

import java.util.ArrayList;
import java.util.List;

import fr.aboucorp.generic.model.Board;
import fr.aboucorp.teamchess.libgdx.utils.ChessCellArray;

public class ChessBoard extends Board {
    private ChessCellArray chessCells;
    private ArrayList<ChessModel> blackPieces ;
    private ArrayList<ChessModel> whitePieces ;
    private ArrayList<ModelInstance> devStuff ;

    public ChessBoard() {
        this.chessCells = new ChessCellArray();
        this.blackPieces = new ArrayList();
        this.whitePieces = new ArrayList();
        this.devStuff = new ArrayList<ModelInstance>();
    }

    public List<ChessModel> getFlattenCells() {
        return chessCells.getFlattenCells();
    }

    public ChessCellArray getChessCellArray() {
        return chessCells;
    }

    public ArrayList<ChessModel> getBlackPieces() {
        return blackPieces;
    }

    public ArrayList<ChessModel> getWhitePieces() {
        return whitePieces;
    }

    public ArrayList<ModelInstance> getDevStuff() {
        return devStuff;
    }
}
