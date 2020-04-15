package fr.aboucorp.entities.model;



public abstract class ChessPiece extends GameElement {

    public ChessPiece(Location location, ChessColor chessColor){
        super(location, chessColor);
    }

    public abstract void move(Location location);

  /*  public ChessCell checkCellValidity(ChessCellArray allCells, int x , int z){
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
    }*/


}
