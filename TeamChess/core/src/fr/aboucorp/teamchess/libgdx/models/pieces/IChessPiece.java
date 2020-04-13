package fr.aboucorp.teamchess.libgdx.models.pieces;

import fr.aboucorp.generic.model.IPiece;
import fr.aboucorp.teamchess.libgdx.exceptions.CellNotFoundException;
import fr.aboucorp.teamchess.libgdx.models.ChessCell;
import fr.aboucorp.teamchess.libgdx.utils.ChessCellArray;

public interface IChessPiece extends IPiece {
    default ChessCell checkCellValidity(ChessCellArray allCells, int x , int z){
        try {
            ChessCell cell = allCells.getPieceByLocation(x, z);
            if (cell.getPiece() == null) {
                return cell;
            }else{
                return null;
            }
        } catch (CellNotFoundException e) {
            // Out of bound move, exit
            return null;
        }
    }
}
