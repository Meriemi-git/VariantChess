package fr.aboucorp.variantchess.entities.rules;

public abstract class AbstractRuleSet {

    public int moveNumber = 0;

    void clearRules(){
        moveNumber = 0;
    }
}
