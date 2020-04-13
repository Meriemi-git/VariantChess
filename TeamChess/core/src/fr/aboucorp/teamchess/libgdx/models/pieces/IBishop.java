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

public interface IBishop extends IChessPiece{
     default List<Cell> getPossibleMoves(IPiece piece, Board board){
         ChessCellArray allCells = ((ChessBoard)board).getChessCellArray();
         List<Cell> possibleCells = new ArrayList<Cell>();
         Location start = ((ChessPiece) piece).getCell().getLocation();
         // top left direction
         for(int x = start.x+1, z = start.z+1 ; x < 8 && z < 8; x++,z++ ){
             ChessCell validCell = checkCellValidity(allCells,x,z);
             if(validCell != null){
                 possibleCells.add(validCell);
             }else{
                 break;
             }
         }
         // Top Right direction
         for(int x = start.x-1, z = start.z+1 ; x >= 0 && z < 8; x--,z++ ){
             ChessCell validCell = checkCellValidity(allCells,x,z);
             if(validCell != null){
                 possibleCells.add(validCell);
             }else{
                 break;
             }
         }
         // Down Left direction
         for(int x = start.x-1, z = start.z-1 ; x >= 0 && z >= 0; x--,z-- ){
             ChessCell validCell = checkCellValidity(allCells,x,z);
             if(validCell != null){
                 possibleCells.add(validCell);
             }else{
                 break;
             }
         }
         // Down Right direction
         for(int x = start.x+1, z = start.z-1 ; x < 8 && z >= 0; x++,z-- ){
             ChessCell validCell = checkCellValidity(allCells,x,z);
             if(validCell != null){
                 possibleCells.add(validCell);
             }else{
                 break;
             }
         }
         return possibleCells;
     }
}
