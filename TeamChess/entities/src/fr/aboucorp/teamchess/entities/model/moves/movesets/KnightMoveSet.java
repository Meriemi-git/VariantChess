package fr.aboucorp.teamchess.entities.model.moves.movesets;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessCell;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.ChessPiece;
import fr.aboucorp.teamchess.entities.model.Location;
import fr.aboucorp.teamchess.entities.model.utils.ChessCellList;

public class KnightMoveSet extends AbstractMoveSet {
    @Override
    public ChessCellList getMoves(ChessPiece piece, Board board, ChessColor turnColor) {
        ChessCellList validCells = new ChessCellList();
        Location start = piece.getLocation();

        for (ChessCell cell : board.getChessCells()) {
            if(cell.getPiece() == null) {
                Location end = cell.getLocation();
                if (end.getX() == start.getX() + 2
                        && (end.getZ() == start.getZ() + 1
                        || end.getZ() == start.getZ() - 1)) {
                    validCells.add(cell);
                }
                if (end.getX() == start.getX() - 2
                        && (end.getZ() == start.getZ() + 1
                        || end.getZ() == start.getZ() - 1)) {
                    validCells.add(cell);
                }
                if (end.getZ() == start.getZ() + 2
                        && (end.getX() == start.getX() + 1
                        || end.getX() == start.getX() - 1)) {
                    validCells.add(cell);
                }
                if (end.getZ() == start.getZ() - 2
                        && (end.getX() == start.getX() + 1
                        || end.getX() == start.getX() - 1)) {
                    validCells.add(cell);
                }
            }
        }
        return  validCells;
    }
}
