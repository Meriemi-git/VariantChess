package fr.aboucorp.variantchess.entities;


public interface PartyLifeCycle {
    void startParty(ChessMatch chessMatch);

    void stopParty();
}
