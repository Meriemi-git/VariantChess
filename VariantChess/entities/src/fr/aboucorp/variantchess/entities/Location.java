package fr.aboucorp.variantchess.entities;

public class Location implements Cloneable {

    private final float x;
    private final float y;
    private final float z;

    public Location(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Location && ((Location) obj).getX() == x && ((Location) obj).getY() == y && ((Location) obj).getZ() == z;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }


    public float getZ() {
        return z;
    }

    @Override
    public Location clone()  {
        return new Location(this.x,this.y,this.z);
    }

    @Override
    public String toString() {
        return "[" + (char) (65 + (7 - x)) + (z+1) + "]";
    }
}
