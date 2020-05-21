package fr.aboucorp.variantchess.entities.boards;

import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.exceptions.FenStringBadFormatException;
import fr.aboucorp.variantchess.entities.utils.PieceList;
import fr.aboucorp.variantchess.entities.utils.SquareList;

public abstract class Board {

    public abstract void initBoard();

    public abstract void loadBoard(String fenString) throws FenStringBadFormatException;

    public abstract SquareList getSquares();

    public abstract PieceList getBlackPieces();

    public abstract PieceList getWhitePieces();

    public abstract PieceList getPiecesByColor(ChessColor color);

    public abstract void clearBoard();
}
