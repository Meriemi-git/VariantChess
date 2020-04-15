package fr.aboucorp.entities.model;

public class Turn {
    private int turnNumber;
    private Team team;

    public Turn(int turnNumber, Team team) {
        this.turnNumber = turnNumber;
        this.team = team;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public void setTurnNumber(int turnNumber) {
        this.turnNumber = turnNumber;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
