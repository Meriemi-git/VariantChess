package fr.aboucorp.variantchess.app.listeners;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

import fr.aboucorp.variantchess.app.managers.boards.BoardManager;
import fr.aboucorp.variantchess.libgdx.models.GraphicsGameElement;
import fr.aboucorp.variantchess.libgdx.utils.GraphicGameArray;

/**
 * Permet de trouver quel modèle 3D a été sélectionné en fonction des coordonnées du clic sur l'écran
 * Created by Doremus on 04/02/2017.
 */

class TouchedModelFinder {

    private BoardManager boardManager;

    public TouchedModelFinder(BoardManager boardManager) {
        this.boardManager = boardManager;
    }


    public GraphicsGameElement getTouched3DModel(float screenX, float screenY, GraphicGameArray models) {
        Ray ray = this.boardManager.getCamera().getPickRay(screenX, screenY);
        double distance = -1;
        int result = -1;
        for (int i = 0; i < models.size; i++) {
            Vector3 position = new Vector3();
            final GraphicsGameElement element = models.get(i);
            element.getModel3d().transform.getTranslation(position);
            BoundingBox box = element.getModel3d().calculateBoundingBox(new BoundingBox()).mul(element.getModel3d().transform.cpy());
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

    public GraphicsGameElement getTouched2DModel(float screenX, float screenY, GraphicGameArray models) {
        Ray ray = this.boardManager.getCamera().getPickRay(screenX, screenY);
        for (int i = 0; i < models.size; i++) {
            final GraphicsGameElement element = models.get(i);
            if (Intersector.intersectRayBoundsFast(ray, new Vector3(element.getLocation().getX(), 0, element.getLocation().getZ()), new Vector3(1, 1, 1))) {
                return element;
            }
        }
        return null;
    }
}
