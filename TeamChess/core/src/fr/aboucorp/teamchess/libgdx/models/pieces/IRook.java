package fr.aboucorp.teamchess.libgdx.models.pieces;

import java.util.ArrayList;
import java.util.List;

import fr.aboucorp.generic.model.Board;
import fr.aboucorp.generic.model.Cell;
import fr.aboucorp.generic.model.IPiece;
import fr.aboucorp.generic.model.Location;
import fr.aboucorp.teamchess.libgdx.models.ChessBoard;
import fr.aboucorp.teamchess.libgdx.models.ChessCell;
import fr.aboucorp.teamchess.libgdx.models.ChessPiece;
import fr.aboucorp.teamchess.libgdx.utils.ChessCellArray;

public interface IRook extends IChessPiece {
    default  List<Cell> getPossibleMoves(IPiece piece, Board board){
        ChessCellArray allCells = ((ChessBoard)board).getChessCellArray();
        List<Cell> possibleCells = new ArrayList<Cell>();
        Location start = ((ChessPiece) piece).getCell().getLocation();

        for(int x = start.x+1; x < 8 ; x++){
            ChessCell validCell = checkCellValidity(allCells,x,start.z);
            if(validCell != null){
                possibleCells.add(validCell);
            }else{
                break;
            }
        }

        for(int x = start.x-1 ; x >= 0 ; x--){
            ChessCell validCell = checkCellValidity(allCells,x,start.z);
            if(validCell != null){
                possibleCells.add(validCell);
            }else{
                break;
            }
        }

        for(int z = start.z-1 ;  z >= 0; z-- ){
            ChessCell validCell = checkCellValidity(allCells,start.x,z);
            if(validCell != null){
                possibleCells.add(validCell);
            }else{
                break;
            }
        }

        for(int z = start.z+1 ; z < 8; z++ ){
            ChessCell validCell = checkCellValidity(allCells,start.x,z);
            if(validCell != null){
                possibleCells.add(validCell);
            }else{
                break;
            }
        }
        return possibleCells;
    }
}
