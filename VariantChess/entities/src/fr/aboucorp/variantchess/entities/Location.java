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
        return obj instanceof Location && ((Location) obj).getX() == this.x && ((Location) obj).getY() == this.y && ((Location) obj).getZ() == this.z;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }


    public float getZ() {
        return this.z;
    }

    @Override
    public Location clone()  {
        return new Location(this.x,this.y,this.z);
    }

    @Override
    public String toString() {
        return "[" + (char) (65 + (7 - this.x)) + (this.z +1) + "]";
    }
}
