package fr.aboucorp.teamchess.entities.model.moves.movesets;

import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.boards.ClassicBoard;
import fr.aboucorp.teamchess.entities.model.moves.AbstractMoveSet;
import fr.aboucorp.teamchess.entities.model.utils.SquareList;

public class QueenMoveSet extends AbstractMoveSet {

    private RookMoveSet rookMoveSet;
    private  BishopMoveSet bishopMoveSet;

    public QueenMoveSet(Piece thisPiece, ClassicBoard classicBoard) {
        super(thisPiece, classicBoard);
        this.rookMoveSet = new RookMoveSet(thisPiece, classicBoard);
        this.bishopMoveSet = new BishopMoveSet(thisPiece, classicBoard);
    }

    @Override
    protected SquareList getPossibleMoves(Piece piece,ChessColor turnColor) {
        SquareList squares = new SquareList();
        SquareList rookMoves  = rookMoveSet.getPossibleMoves(piece,turnColor);
        SquareList bishopMoves = bishopMoveSet.getPossibleMoves(piece,turnColor);
        if(rookMoves != null){
            squares.addAll(rookMoves);
        }
        if(bishopMoves != null){
            squares.addAll(bishopMoves);
        }
        return squares;
    }

    @Override
    public SquareList getThreats(Piece piece, ChessColor turnColor) {
        return getPossibleMoves(piece,turnColor);
    }
}
