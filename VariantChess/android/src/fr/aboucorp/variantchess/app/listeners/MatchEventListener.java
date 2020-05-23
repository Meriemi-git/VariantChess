package fr.aboucorp.variantchess.app.listeners;

import fr.aboucorp.variantchess.entities.events.models.GameEvent;

public interface MatchEventListener {
    void OnMatchEvent(GameEvent event);
}
