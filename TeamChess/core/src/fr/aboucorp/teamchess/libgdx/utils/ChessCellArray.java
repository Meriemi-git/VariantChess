package fr.aboucorp.teamchess.libgdx.utils;

import java.util.ArrayList;

import fr.aboucorp.entities.model.exceptions.CellNotFoundException;
import fr.aboucorp.teamchess.libgdx.models.ChessCellModel;
import fr.aboucorp.teamchess.libgdx.models.ChessModel;

public class ChessCellArray extends ArrayList {

    private ArrayList<ArrayList<ChessCellModel>> cells;

    public ChessCellArray() {
        this.cells = new ArrayList<>(8);
        for (int i = 0; i < 8; i++) {
            ArrayList<ChessCellModel> column = new ArrayList<>(8);
            cells.add(column);
            for (int j = 0 ; j < 8 ; j++){
                column.add(null);
            }
        }
    }


    @Override
    public boolean add(Object o) {
        ChessCellModel cell = (ChessCellModel)o;
        int x = cell.getLocation().getX();
        int z = cell.getLocation().getZ();
        if(x < 0 || x >= 8 || z < 0 || z >= 8){
            return false;
        }
        ArrayList<ChessCellModel> column = cells.get(x);
        if(column == null){
            column = new ArrayList<>();
            column.set(z, (ChessCellModel) o);
            cells.set(x, column);
        }else{
            column.set(z, (ChessCellModel) o);
        }
        return true;
    }

    public ChessCellModel getPieceByLocation(int x , int z) throws CellNotFoundException {
        ArrayList<ChessCellModel> column = this.cells.get(x);
        if(column == null){
            throw new CellNotFoundException(String.format("No cell found for coordinates x:%s; z:%s;", x,z));
        }
        try{
            ChessCellModel cell = column.get(z);
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
        for(ArrayList<ChessCellModel> columns : this.cells){
            flattenCells.addAll(columns);
        }
        return flattenCells;
    }
}
