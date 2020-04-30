package fr.aboucorp.teamchess.entities.model.boards;

import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.exceptions.FenStringBadFormatException;
import fr.aboucorp.teamchess.entities.model.utils.PieceList;
import fr.aboucorp.teamchess.entities.model.utils.SquareList;

public abstract class Board {

    public abstract void initBoard();

    public abstract void loadBoard(String fenString) throws FenStringBadFormatException;

    public abstract SquareList getSquares();

    public abstract PieceList getBlackPieces();

    public abstract PieceList getWhitePieces();

    public abstract PieceList getPiecesByColor(ChessColor color);

    public abstract void clearBoard();
}
