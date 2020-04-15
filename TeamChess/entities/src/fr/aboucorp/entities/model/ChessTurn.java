package fr.aboucorp.entities.model;

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
