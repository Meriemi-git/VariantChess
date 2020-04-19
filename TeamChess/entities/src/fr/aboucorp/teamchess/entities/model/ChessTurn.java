package fr.aboucorp.teamchess.entities.model;

public class ChessTurn extends Turn {

    private fr.aboucorp.teamchess.entities.model.ChessPiece DeadPiece;

    public ChessTurn(int turnNumber, Team team) {
        super(turnNumber, team);
    }

    public fr.aboucorp.teamchess.entities.model.ChessPiece getDeadPiece() {
        return DeadPiece;
    }

    public void setDeadPiece(ChessPiece deadPiece) {
        DeadPiece = deadPiece;
    }
}
