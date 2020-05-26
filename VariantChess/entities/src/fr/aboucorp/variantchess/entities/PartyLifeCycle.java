package fr.aboucorp.variantchess.entities;


public interface PartyLifeCycle {
    void startParty(Match match);

    void stopParty();
}
