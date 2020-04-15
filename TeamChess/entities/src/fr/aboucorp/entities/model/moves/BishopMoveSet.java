package fr.aboucorp.entities.model.moves;

public interface BishopMoveSet {
     /*default List<Cell> getPossibleMoves(Piece piece, Board board){
         ChessCellArray allCells = ((ChessBoard3d)board).getChessCellArray();
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
     }*/
}
