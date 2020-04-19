package fr.aboucorp.teamchess.entities.model.moves.movesets;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessCell;
import fr.aboucorp.teamchess.entities.model.ChessPiece;
import fr.aboucorp.teamchess.entities.model.utils.ChessList;

public class QueenMoveSet extends AbstractMoveSet {

    private RookMoveSet rookMoveSet;
    private  BishopMoveSet bishopMoveSet;

    public QueenMoveSet() {
        this.rookMoveSet = new RookMoveSet();
        this.bishopMoveSet = new BishopMoveSet();
    }

    @Override
    public ChessList<ChessCell> getMoves(ChessPiece piece, Board board) {
        ChessList<ChessCell> cells = new  ChessList<ChessCell>();
        ChessList<ChessCell> rookMoves  = rookMoveSet.getMoves(piece,board);
        ChessList<ChessCell> bishopMoves = bishopMoveSet.getMoves(piece, board);
        if(rookMoves != null){
            cells.addAll(rookMoves);
        }
        if(bishopMoves != null){
            cells.addAll(bishopMoves);
        }
        return cells;
    }
}
