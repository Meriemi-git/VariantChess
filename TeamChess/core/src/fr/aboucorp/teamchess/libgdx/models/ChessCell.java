package fr.aboucorp.teamchess.libgdx.models;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.collision.BoundingBox;

import fr.aboucorp.generic.model.Location;
import fr.aboucorp.generic.model.enums.Color;

public class ChessCell extends ChessModel {
    private String LetterLabel;
    private int NumberLabel;
    private BoundingBox boundingBox;

    public ChessCell(Model model, Location location, Color color) {
        super(model,location,color);
        this.setLabels(location);
        this.boundingBox = calculateBoundingBox(new BoundingBox()).mul(transform);
    }

    private void setLabels(Location location) {
        this.LetterLabel = String.valueOf(location.x + 65);
        this.NumberLabel = super.location.z;
    }

    @Override
    public String toString() {
        return '[' + LetterLabel+NumberLabel + ']';
    }

    public String getLetterLabel() {
        return LetterLabel;
    }

    public void setLetterLabel(String letterLabel) {
        LetterLabel = letterLabel;
    }

    public int getNumberLabel() {
        return NumberLabel;
    }

    public void setNumberLabel(int numberLabel) {
        NumberLabel = numberLabel;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

}
