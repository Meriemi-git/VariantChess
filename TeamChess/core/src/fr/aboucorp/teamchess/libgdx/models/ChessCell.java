package fr.aboucorp.teamchess.libgdx.models;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.collision.BoundingBox;

import fr.aboucorp.generic.model.Cell;
import fr.aboucorp.generic.model.Location;
import fr.aboucorp.generic.model.enums.Color;

public class ChessCell extends ChessModel implements Cell {
    private String LetterLabel;
    private int NumberLabel;
    protected Location location;
    private BoundingBox boundingBox;
    private ChessPiece piece;

    public ChessCell(Model model, Location location, Color color) {
        super(model,location,color);
        this.setLabels(location);
        this.boundingBox = calculateBoundingBox(new BoundingBox()).mul(transform);
        this.location = location;
    }

    private void setLabels(Location location) {
        this.LetterLabel = String.valueOf(location.x + 65);
        this.NumberLabel = super.location.z;
    }

    @Override
    public String toString() {
        return '[' + this.LetterLabel+this.NumberLabel + ']';
    }

    public String getLetterLabel() {
        return this.LetterLabel;
    }

    public void setLetterLabel(String letterLabel) {
        this.LetterLabel = letterLabel;
    }

    public int getNumberLabel() {
        return this.NumberLabel;
    }

    public void setNumberLabel(int numberLabel) {
        this.NumberLabel = numberLabel;
    }

    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    public Location getLocation() {
        return this.location;
    }

    public ChessPiece getPiece() {
        return piece;
    }

    public void setPiece(ChessPiece piece) {
        this.piece = piece;
    }
}
