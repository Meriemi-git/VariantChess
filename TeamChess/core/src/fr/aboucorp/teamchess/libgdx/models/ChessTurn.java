package fr.aboucorp.teamchess.libgdx.models;

import fr.aboucorp.generic.model.Team;
import fr.aboucorp.generic.model.Turn;

public class ChessTurn extends Turn {

    private ChessPiece DeadPiece;

    public ChessTurn(int turnNumber, Team team) {
        super(turnNumber, team);
    }

    public ChessPiece getDeadPiece() {
        return DeadPiece;
    }

    public void setDeadPiece(ChessPiece deadPiece) {
        DeadPiece = deadPiece;
    }
}
