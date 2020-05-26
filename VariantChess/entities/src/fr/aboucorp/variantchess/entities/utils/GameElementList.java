package fr.aboucorp.variantchess.entities.utils;

import java.util.ArrayList;
import java.util.Iterator;

import fr.aboucorp.variantchess.entities.GameElement;
import fr.aboucorp.variantchess.entities.Location;

public abstract class GameElementList<T extends GameElement> extends ArrayList<T> {

    public fr.aboucorp.variantchess.entities.GameElement getItemByLocation(Location location) {
      for(GameElement element : this){
        if(element.getLocation().equals(location)){
            return element;
        }
      }
      return null;
    }

    public T removeByLocation(Location location) {
        T removed;
        for (Iterator<T> iter = this.iterator(); iter.hasNext();){
            T element = iter.next();
            if(element.getLocation().equals(location)){
                removed = element;
                iter.remove();
                return removed;
            }
        }
        return null;
    }
}
