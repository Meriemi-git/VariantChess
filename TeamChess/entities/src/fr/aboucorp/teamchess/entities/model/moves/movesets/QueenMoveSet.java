package fr.aboucorp.teamchess.entities.model.moves.movesets;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.moves.AbstractMoveSet;
import fr.aboucorp.teamchess.entities.model.utils.SquareList;

public class QueenMoveSet extends AbstractMoveSet {

    private RookMoveSet rookMoveSet;
    private  BishopMoveSet bishopMoveSet;

    public QueenMoveSet(Piece thisPiece, Board board) {
        super(thisPiece,board);
        this.rookMoveSet = new RookMoveSet(thisPiece,board);
        this.bishopMoveSet = new BishopMoveSet(thisPiece,board);
    }

    @Override
    protected SquareList getPossibleMoves(Piece piece, Board board, ChessColor turnColor) {
        SquareList squares = new SquareList();
        SquareList rookMoves  = rookMoveSet.getPossibleMoves(piece,board,turnColor);
        SquareList bishopMoves = bishopMoveSet.getPossibleMoves(piece, board,turnColor);
        if(rookMoves != null){
            squares.addAll(rookMoves);
        }
        if(bishopMoves != null){
            squares.addAll(bishopMoves);
        }
        return squares;
    }

    @Override
    public SquareList getThreats(Piece piece, Board board, ChessColor turnColor) {
        return getPossibleMoves(piece,board,turnColor);
    }
}
