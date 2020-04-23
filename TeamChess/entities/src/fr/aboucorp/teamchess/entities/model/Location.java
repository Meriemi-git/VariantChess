package fr.aboucorp.teamchess.entities.model;

public class Location {

    private final int x;
    private final int y;
    private final int z;

    public Location(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Location && ((Location) obj).getX() == x && ((Location) obj).getY() == y && ((Location) obj).getZ() == z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


    public int getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "[" + (char) (65 + (7 - x)) + (z+1) + "]";
    }
}
