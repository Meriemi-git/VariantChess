package fr.aboucorp.teamchess.entities.model.events;

import fr.aboucorp.teamchess.entities.model.events.models.GameEvent;

public interface GameEventSubscriber {
    void receiveGameEvent(GameEvent event);
}
