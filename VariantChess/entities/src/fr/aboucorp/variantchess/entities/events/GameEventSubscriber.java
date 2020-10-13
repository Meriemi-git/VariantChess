package fr.aboucorp.variantchess.entities.events;

import fr.aboucorp.variantchess.entities.events.models.GameEvent;

public interface GameEventSubscriber {
    void receiveEvent(GameEvent event);
}
