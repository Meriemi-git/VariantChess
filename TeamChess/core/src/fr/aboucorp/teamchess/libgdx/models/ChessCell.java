package fr.aboucorp.teamchess.libgdx.models;

import com.badlogic.gdx.graphics.g3d.Model;

import fr.aboucorp.generic.model.Location;
import fr.aboucorp.generic.model.enums.Color;

public class ChessCell extends ChessModel {
    public String LetterLabel;
    public int NumberLabel;

    public ChessCell(Model model, Location location, Color color) {
        super(model,location,color);
        this.setLabels(location);
    }

    private void setLabels(Location location) {
        this.LetterLabel = String.valueOf(location.x + 65);
        this.NumberLabel = super.location.z;
    }


    @Override
    public String toString() {
        return '[' + LetterLabel+NumberLabel + ']';
    }

}
