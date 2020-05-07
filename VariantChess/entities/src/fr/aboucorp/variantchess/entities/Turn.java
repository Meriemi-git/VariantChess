package fr.aboucorp.variantchess.entities;

import java.time.Duration;

public class Turn implements Comparable<Turn>{

    public final int turnNumber;
    public Duration duration;
    public fr.aboucorp.variantchess.entities.Square to;
    public fr.aboucorp.variantchess.entities.Square from;
    public fr.aboucorp.variantchess.entities.Piece played;
    public fr.aboucorp.variantchess.entities.Piece DeadPiece;
    public final fr.aboucorp.variantchess.entities.Team team;

    public Turn(int turnNumber, Team team) {
        this.turnNumber = turnNumber;
        this.team = team;
    }

    public ChessColor getTurnColor(){
        return this.team.getChessColor();
    }

    public fr.aboucorp.variantchess.entities.Piece getDeadPiece() {
        return DeadPiece;
    }

    public void setDeadPiece(fr.aboucorp.variantchess.entities.Piece deadPiece) {
        DeadPiece = deadPiece;
    }

    public fr.aboucorp.variantchess.entities.Square getTo() {
        return to;
    }

    public void setTo(fr.aboucorp.variantchess.entities.Square to) {
        this.to = to;
    }

    public fr.aboucorp.variantchess.entities.Piece getPlayed() {
        return played;
    }

    public void setPlayed(Piece played) {
        this.played = played;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public fr.aboucorp.variantchess.entities.Square getFrom() {
        return from;
    }

    public void setFrom(Square from) {
        this.from = from;
    }

    @Override
    public int compareTo(Turn turn) {
        return Integer.compare(this.turnNumber,turn.turnNumber);
    }
}
