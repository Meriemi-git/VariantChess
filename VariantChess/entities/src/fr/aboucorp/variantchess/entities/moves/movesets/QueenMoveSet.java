package fr.aboucorp.variantchess.entities.moves.movesets;

import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.Piece;
import fr.aboucorp.variantchess.entities.boards.ClassicBoard;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.moves.AbstractMoveSet;
import fr.aboucorp.variantchess.entities.utils.SquareList;

public class QueenMoveSet extends AbstractMoveSet {

    private RookMoveSet rookMoveSet;
    private BishopMoveSet bishopMoveSet;

    public QueenMoveSet(Piece thisPiece, ClassicBoard classicBoard, GameEventManager gameEventManager) {
        super(thisPiece, classicBoard,gameEventManager);
        this.rookMoveSet = new RookMoveSet(thisPiece, classicBoard,gameEventManager);
        this.bishopMoveSet = new BishopMoveSet(thisPiece, classicBoard,gameEventManager);
    }

    @Override
    protected SquareList getPossibleMoves(Piece piece, ChessColor turnColor) {
        SquareList squares = new SquareList();
        SquareList rookMoves  = this.rookMoveSet.getPossibleMoves(piece,turnColor);
        SquareList bishopMoves = this.bishopMoveSet.getPossibleMoves(piece,turnColor);
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
        return this.getPossibleMoves(piece,turnColor);
    }
}
