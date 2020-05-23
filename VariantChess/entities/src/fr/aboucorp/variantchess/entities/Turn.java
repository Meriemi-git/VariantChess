package fr.aboucorp.variantchess.entities;

import java.time.Duration;

public class Turn implements Comparable<Turn>{

    private final int turnNumber;
    private Duration duration;
    private Square to;
    private Square from;
    private Piece played;
    private Piece deadPiece;
    private final Team team;
    private String fen;

    public Turn(int turnNumber, Team team) {
        this.turnNumber = turnNumber;
        this.team = team;
    }

    public ChessColor getTurnColor(){
        return this.team.getChessColor();
    }

    public Piece getDeadPiece() {
        return this.deadPiece;
    }

    public void setDeadPiece(Piece deadPiece) {
        this.deadPiece = deadPiece;
    }

    public Square getTo() {
        return this.to;
    }

    public void setTo(Square to) {
        this.to = to;
    }

    public Piece getPlayed() {
        return this.played;
    }

    public void setPlayed(Piece played) {
        this.played = played;
    }

    public Duration getDuration() {
        return this.duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Square getFrom() {
        return this.from;
    }

    public void setFrom(Square from) {
        this.from = from;
    }

    public int getTurnNumber() {
        return this.turnNumber;
    }

    public Team getTeam() {
        return this.team;
    }

    public String getFen() {
        return this.fen;
    }

    public void setFen(String fen) {
        this.fen = fen;
    }

    @Override
    public int compareTo(Turn turn) {
        return Integer.compare(this.turnNumber,turn.turnNumber);
    }
}
