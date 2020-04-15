package fr.aboucorp.entities.model.moves;

public interface RookMoveSet  {
   /* default  List<Cell> getPossibleMoves(Piece piece, Board board){
        ChessCellArray allCells = ((ChessBoard3d)board).getChessCellArray();
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
    }*/
}
