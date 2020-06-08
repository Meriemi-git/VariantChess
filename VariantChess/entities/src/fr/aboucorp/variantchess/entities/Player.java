package fr.aboucorp.variantchess.entities;


import java.io.Serializable;

public class Player implements Serializable {
    private final String displayName;
    private final ChessColor color;
    private final String userID;

    public Player(String displayName, ChessColor color, String userID) {
        this.displayName = displayName;
        this.color = color;
        this.userID = userID;
    }


    public ChessColor getColor() {
        return this.color;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getUserID() {
        return this.userID;
    }

}
