package fr.aboucorp.teamchess.entities.model.moves.movesets;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessCell;
import fr.aboucorp.teamchess.entities.model.ChessPiece;
import fr.aboucorp.teamchess.entities.model.Location;
import fr.aboucorp.teamchess.entities.model.utils.ChessList;

public class BishopMoveSet extends AbstractMoveSet{

    @Override
    public ChessList<ChessCell> getMoves(ChessPiece piece, Board board) {
        ChessList<ChessCell> allCells = board.getChessCells();
        ChessList<ChessCell> validLocations = new  ChessList<ChessCell>();
        Location start = piece.getLocation();
        // top left direction
        for(int x = start.getX()+1, z = start.getZ()+1 ; x < 8 && z < 8; x++,z++ ){
            ChessCell cell = (ChessCell) allCells.getItemByLocation(new Location(x,0,z));
            if(cell != null && cell.getPiece() == null){
                validLocations.add(cell);
            }else{
                break;
            }
        }
        // Top Right direction
        for(int x = start.getX()-1, z = start.getZ()+1 ; x >= 0 && z < 8; x--,z++ ){
            ChessCell cell = (ChessCell) allCells.getItemByLocation(new Location(x,0,z));
            if(cell != null && cell.getPiece() == null){
                validLocations.add(cell);
            }else{
                break;
            }
        }
        // Down Left direction
        for(int x = start.getX()-1, z = start.getZ()-1 ; x >= 0 && z >= 0; x--,z-- ){
            ChessCell cell = (ChessCell) allCells.getItemByLocation(new Location(x,0,z));
            if(cell != null && cell.getPiece() == null){
                validLocations.add(cell);
            }else{
                break;
            }
        }
        // Down Right direction
        for(int x = start.getX()+1, z = start.getZ()-1 ; x < 8 && z >= 0; x++,z-- ){
            ChessCell cell = (ChessCell) allCells.getItemByLocation(new Location(x,0,z));
            if(cell != null && cell.getPiece() == null){
                validLocations.add(cell);
            }else{
                break;
            }
        }
        return validLocations;
    }
}
