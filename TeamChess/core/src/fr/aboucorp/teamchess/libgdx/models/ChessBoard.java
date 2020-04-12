package fr.aboucorp.teamchess.libgdx.models;


import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Array;

import fr.aboucorp.generic.model.Board;
import fr.aboucorp.teamchess.libgdx.utils.ChessCellArray;

public class ChessBoard extends Board {
    private ChessCellArray chessCells ;
    private Array<ChessPiece> blackPieces ;
    private Array<ChessPiece> whitePieces ;
    private Array<ModelInstance> devStuff ;

    public ChessBoard() {
        this.chessCells = new ChessCellArray();
        this.blackPieces = new Array();
        this.whitePieces = new Array();
        this.devStuff = new Array<ModelInstance>();
    }

    public ChessCellArray getChessCells() {
        return chessCells;
    }

    public Array<ChessPiece> getBlackPieces() {
        return blackPieces;
    }

    public Array<ChessPiece> getWhitePieces() {
        return whitePieces;
    }

    public Array<ModelInstance> getDevStuff() {
        return devStuff;
    }
}
