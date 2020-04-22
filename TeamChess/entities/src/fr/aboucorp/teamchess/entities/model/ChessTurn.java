package fr.aboucorp.teamchess.entities.model;

import java.time.Duration;

public class ChessTurn {

    public final int turnNumber;
    public Duration duration;
    public ChessCell from;
    public ChessCell to;
    public ChessPiece played;
    public ChessPiece DeadPiece;
    public final Team team;

    public ChessTurn(int turnNumber, Team team) {
        this.turnNumber = turnNumber;
        this.team = team;
    }

    public ChessColor getTurnColor(){
        return this.team.getChessColor();
    }

    public ChessPiece getDeadPiece() {
        return DeadPiece;
    }

    public void setDeadPiece(ChessPiece deadPiece) {
        DeadPiece = deadPiece;
    }

    public ChessCell getFrom() {
        return from;
    }

    public void setFrom(ChessCell from) {
        this.from = from;
    }

    public ChessCell getTo() {
        return to;
    }

    public void setTo(ChessCell to) {
        this.to = to;
    }

    public ChessPiece getPlayed() {
        return played;
    }

    public void setPlayed(ChessPiece played) {
        this.played = played;
    }


    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }




}
