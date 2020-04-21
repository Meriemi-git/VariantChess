package fr.aboucorp.teamchess.entities.model;

public class Turn {
    private int turnNumber;
    private Team team;

    public Turn(int turnNumber, fr.aboucorp.teamchess.entities.model.Team team) {
        this.turnNumber = turnNumber;
        this.team = team;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public void setTurnNumber(int turnNumber) {
        this.turnNumber = turnNumber;
    }

    public fr.aboucorp.teamchess.entities.model.Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public ChessColor getTurnColor(){
        return this.team.getChessColor();
    }
}
