package fr.aboucorp.teamchess.entities.model.utils;

import java.util.ArrayList;
import java.util.Iterator;

import fr.aboucorp.teamchess.entities.model.GameElement;
import fr.aboucorp.teamchess.entities.model.Location;

public class ChessList<T extends GameElement> extends ArrayList<T> {

    public GameElement getItemByLocation(Location location) {
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
