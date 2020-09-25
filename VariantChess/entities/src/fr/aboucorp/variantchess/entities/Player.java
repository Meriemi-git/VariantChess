package fr.aboucorp.variantchess.entities;


import java.io.Serializable;

public class Player implements Serializable {
    private final String username;
    private final ChessColor color;
    private final String userID;

    public Player(String username, ChessColor color, String userID) {
        this.username = username;
        this.color = color;
        this.userID = userID;
    }


    public ChessColor getColor() {
        return this.color;
    }

    public String getUsername() {
        return this.username;
    }

    public String getUserID() {
        return this.userID;
    }

}
