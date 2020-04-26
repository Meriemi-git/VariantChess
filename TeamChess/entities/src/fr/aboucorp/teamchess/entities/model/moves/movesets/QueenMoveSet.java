package fr.aboucorp.teamchess.entities.model.moves.movesets;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.moves.AbstractMoveSet;
import fr.aboucorp.teamchess.entities.model.utils.ChessCellList;

public class QueenMoveSet extends AbstractMoveSet {

    private RookMoveSet rookMoveSet;
    private  BishopMoveSet bishopMoveSet;

    public QueenMoveSet(Piece thisPiece, Board board) {
        super(thisPiece,board);
        this.rookMoveSet = new RookMoveSet(thisPiece,board);
        this.bishopMoveSet = new BishopMoveSet(thisPiece,board);
    }

    @Override
    protected ChessCellList getPossibleMoves(Piece piece, Board board, ChessColor turnColor) {
        ChessCellList cells = new  ChessCellList();
        ChessCellList rookMoves  = rookMoveSet.getPossibleMoves(piece,board,turnColor);
        ChessCellList bishopMoves = bishopMoveSet.getPossibleMoves(piece, board,turnColor);
        if(rookMoves != null){
            cells.addAll(rookMoves);
        }
        if(bishopMoves != null){
            cells.addAll(bishopMoves);
        }
        return cells;
    }

    @Override
    public ChessCellList getThreats(Piece piece, Board board, ChessColor turnColor) {
        return getPossibleMoves(piece,board,turnColor);
    }
}
