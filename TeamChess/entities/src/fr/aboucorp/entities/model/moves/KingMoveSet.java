package fr.aboucorp.entities.model.moves;



public interface KingMoveSet  {

   /* @Override
    default List<Cell> getPossibleMoves(Piece piece, Board board) {
        ChessCellArray allCells = ((ChessBoard3d)board).getChessCellArray();
        List<Cell> possibleCells = new ArrayList<Cell>();
        Location start = ((ChessPiece) piece).getCell().getLocation();
        ChessCell up = checkCellValidity(allCells,start.x+1,start.z);
        if(up != null){
            possibleCells.add(up);
        }
        ChessCell  upRight = checkCellValidity(allCells,start.x+1,start.z-1);
        if(upRight != null){
            possibleCells.add(upRight);
        }
        ChessCell  upLeft = checkCellValidity(allCells,start.x+1,start.z+1);
        if(upLeft != null){
            possibleCells.add(upLeft);
        }
        ChessCell down = checkCellValidity(allCells,start.x-1,start.z);
        if(down != null){
            possibleCells.add(down);
        }
        ChessCell  downLeft = checkCellValidity(allCells,start.x-1,start.z+1);
        if(downLeft != null){
            possibleCells.add(downLeft);
        }
        ChessCell  downRight = checkCellValidity(allCells,start.x-1,start.z-1);
        if(downRight != null){
            possibleCells.add(downRight);
        }
        ChessCell  left = checkCellValidity(allCells,start.x,start.z+1);
        if(left != null){
            possibleCells.add(left);
        }
        ChessCell  right = checkCellValidity(allCells,start.x,start.z-1);
        if(right != null){
            possibleCells.add(right);
        }
        return possibleCells;
    }
*/
}