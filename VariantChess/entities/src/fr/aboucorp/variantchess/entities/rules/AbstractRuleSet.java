package fr.aboucorp.variantchess.entities.rules;

import fr.aboucorp.variantchess.entities.events.GameEventManager;

public abstract class AbstractRuleSet {
    protected GameEventManager gameEventManager;
    public int moveNumber = 0;

    void clearRules(){
        this.moveNumber = 0;
    }
}
