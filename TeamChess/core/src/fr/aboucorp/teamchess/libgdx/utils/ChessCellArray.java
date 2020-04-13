package fr.aboucorp.teamchess.libgdx.utils;

import java.util.ArrayList;

import fr.aboucorp.teamchess.libgdx.exceptions.CellNotFoundException;
import fr.aboucorp.teamchess.libgdx.models.ChessCell;
import fr.aboucorp.teamchess.libgdx.models.ChessModel;

public class ChessCellArray extends ArrayList {

    private ArrayList<ArrayList<ChessCell>> cells;

    public ChessCellArray() {
        this.cells = new ArrayList<>(8);
        for (int i = 0; i < 8; i++) {
            ArrayList<ChessCell> column = new ArrayList<>(8);
            cells.add(column);
            for (int j = 0 ; j < 8 ; j++){
                column.add(null);
            }
        }
    }


    @Override
    public boolean add(Object o) {
        ChessCell cell = (ChessCell)o;
        int x = cell.getLocation().x;
        int z = cell.getLocation().z;
        if(x < 0 || x >= 8 || z < 0 || z >= 8){
            return false;
        }
        ArrayList<ChessCell> column = cells.get(x);
        if(column == null){
            column = new ArrayList<>();
            column.set(z, (ChessCell) o);
            cells.set(x, column);
        }else{
            column.set(z, (ChessCell) o);
        }
        return true;
    }

    public ChessCell getPieceByLocation(int x , int z) throws CellNotFoundException {
        ArrayList<ChessCell> column = this.cells.get(x);
        if(column == null){
            throw new CellNotFoundException(String.format("No cell found for coordinates x:%s; z:%s;", x,z));
        }
        try{
            ChessCell cell = column.get(z);
            if(cell == null){
                throw new CellNotFoundException(String.format("No cell found for coordinates x:%s; z:%s;",x,z));
            }
            return cell;
        }catch(IndexOutOfBoundsException ex){
            throw new CellNotFoundException(String.format("No cell found for coordinates x:%s; z:%s;",x,z));
        }
    }

    public ArrayList<ChessModel> getFlattenCells() {
        ArrayList<ChessModel> flattenCells = new ArrayList();
        for(ArrayList<ChessCell> columns : this.cells){
            flattenCells.addAll(columns);
        }
        return flattenCells;
    }
}
