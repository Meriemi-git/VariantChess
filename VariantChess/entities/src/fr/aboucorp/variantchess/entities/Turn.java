package fr.aboucorp.variantchess.entities;

import java.io.Serializable;
import java.time.Duration;

import fr.aboucorp.variantchess.entities.enums.PieceId;

public class Turn implements Comparable<Turn>, Serializable {

    protected int turnNumber;
    protected Duration duration;
    protected Location to;
    protected Location from;
    protected PieceId played;
    protected PieceId deadPiece;
    protected Player player;
    protected String fen;

    public Turn(int turnNumber, Player player) {
        this.turnNumber = turnNumber;
        this.player = player;
    }

    public Turn() {
    }

    public ChessColor getTurnColor() {
        return this.player.getColor();
    }

    public PieceId getDeadPiece() {
        return this.deadPiece;
    }

    public void setDeadPiece(PieceId deadPiece) {
        this.deadPiece = deadPiece;
    }

    public Location getTo() {
        return this.to;
    }

    public void setTo(Location to) {
        this.to = to;
    }

    public PieceId getPlayed() {
        return this.played;
    }

    public void setPlayed(PieceId played) {
        this.played = played;
    }

    public Duration getDuration() {
        return this.duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Location getFrom() {
        return this.from;
    }

    public void setFrom(Location from) {
        this.from = from;
    }

    public int getTurnNumber() {
        return this.turnNumber;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getFen() {
        return this.fen;
    }

    public void setFen(String fen) {
        this.fen = fen;
    }

    @Override
    public int compareTo(Turn turn) {
        return Integer.compare(this.turnNumber, turn.turnNumber);
    }
}
