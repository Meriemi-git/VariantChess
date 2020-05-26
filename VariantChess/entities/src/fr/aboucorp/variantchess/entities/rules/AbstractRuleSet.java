package fr.aboucorp.variantchess.entities.rules;

import fr.aboucorp.variantchess.entities.events.GameEventManager;

public abstract class AbstractRuleSet {
    public int moveNumber = 0;
    protected GameEventManager gameEventManager;

    void clearRules() {
        this.moveNumber = 0;
    }
}
