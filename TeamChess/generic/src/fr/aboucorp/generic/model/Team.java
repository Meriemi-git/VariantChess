package fr.aboucorp.generic.model;


import java.util.List;

import fr.aboucorp.generic.model.enums.Color;

public class Team {
    private String name;
    private List<Player> players;
    private Color color;

    public Team(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
