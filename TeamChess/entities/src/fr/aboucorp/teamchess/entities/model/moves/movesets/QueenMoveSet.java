package fr.aboucorp.teamchess.entities.model.moves.movesets;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.ChessPiece;
import fr.aboucorp.teamchess.entities.model.utils.ChessCellList;

public class QueenMoveSet extends AbstractMoveSet {

    private RookMoveSet rookMoveSet;
    private  BishopMoveSet bishopMoveSet;

    public QueenMoveSet() {
        this.rookMoveSet = new RookMoveSet();
        this.bishopMoveSet = new BishopMoveSet();
    }

    @Override
    public ChessCellList getMoves(ChessPiece piece, Board board, ChessColor turnColor) {
        ChessCellList cells = new  ChessCellList();
        ChessCellList rookMoves  = rookMoveSet.getMoves(piece,board,turnColor);
        ChessCellList bishopMoves = bishopMoveSet.getMoves(piece, board,turnColor);
        if(rookMoves != null){
            cells.addAll(rookMoves);
        }
        if(bishopMoves != null){
            cells.addAll(bishopMoves);
        }
        return cells;
    }
}
