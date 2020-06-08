package fr.aboucorp.variantchess.entities;

import java.io.Serializable;

public class Location implements Cloneable, Serializable {

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

    @Override
    public Location clone() {
        return new Location(this.x, this.y, this.z);
    }

    @Override
    public String toString() {
        return "[" + (char) (65 + (7 - this.x)) + (this.z + 1) + "]";
    }

    public static Location fromString(String string) throws NumberFormatException {
        try {
            String[] stringValues = string.substring(1, string.length() - 2).split(",");
            float x = Float.parseFloat(stringValues[0]);
            float y = Float.parseFloat(stringValues[0]);
            float z = Float.parseFloat(stringValues[0]);
            return new Location(x, y, z);
        } catch (Exception ex) {
            throw new NumberFormatException("Error during location string parsing");
        }
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

    public String getStringValue() {
        return "(" + this.x + "," + this.y + "," + this.z + ")";
    }

}
