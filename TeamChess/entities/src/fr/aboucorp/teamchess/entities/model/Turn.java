package fr.aboucorp.teamchess.entities.model;

import java.time.Duration;

public class Turn {

    public final int turnNumber;
    public Duration duration;
    public Square from;
    public Square to;
    public Piece played;
    public Piece DeadPiece;
    public final Team team;

    public Turn(int turnNumber, Team team) {
        this.turnNumber = turnNumber;
        this.team = team;
    }

    public ChessColor getTurnColor(){
        return this.team.getChessColor();
    }

    public Piece getDeadPiece() {
        return DeadPiece;
    }

    public void setDeadPiece(Piece deadPiece) {
        DeadPiece = deadPiece;
    }

    public Square getFrom() {
        return from;
    }

    public void setFrom(Square from) {
        this.from = from;
    }

    public Square getTo() {
        return to;
    }

    public void setTo(Square to) {
        this.to = to;
    }

    public Piece getPlayed() {
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




}
