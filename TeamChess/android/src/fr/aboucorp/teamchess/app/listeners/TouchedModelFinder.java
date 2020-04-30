package fr.aboucorp.teamchess.app.listeners;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

import java.util.ArrayList;

import fr.aboucorp.teamchess.app.managers.PartyManager;
import fr.aboucorp.teamchess.libgdx.models.ChessModel;

/**
 * Permet de trouver quel modèle 3D a été sélectionné en fonction des coordonnées du clic sur l'écran
 * Created by Doremus on 04/02/2017.
 */

public class TouchedModelFinder {

    private PartyManager partyManager;

    public TouchedModelFinder(PartyManager partyManager) {
        this.partyManager = partyManager;
    }


    public ChessModel getTouchedModel(float screenX, float screenY, ArrayList<ChessModel> models) {
        Ray ray = this.partyManager.getBoardManager().getCamera().getPickRay(screenX, screenY);
        double distance = -1;
        int result = -1;
        for (int i = 0; i < models.size(); i++) {
            Vector3 position = new Vector3();
            final ChessModel piece = models.get(i);
            piece.transform.getTranslation(position);
            BoundingBox box = piece.calculateBoundingBox(new BoundingBox()).mul(piece.transform.cpy());
            position.add(box.getCenter(new Vector3()));
            double dist2 = ray.origin.dst2(position);
            if (distance > 0f && dist2 > distance) continue;
            if (Intersector.intersectRayBoundsFast(ray, box)) {
                result = i;
                distance = dist2;
            }
        }
        if (result == -1) {
            return null;
        }
        return models.get(result);
    }
}
